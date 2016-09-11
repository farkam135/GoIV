package com.kamron.pogoiv.logic;

import android.support.v4.util.Pair;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

/**
 * Component for user-trainable autocorrection of pokemon names.
 * Responsibility for storing and loading user corrections rests with the caller.
 * Created by pgiarrusso on 5/9/2016.
 */
public class PokemonNameCorrector {
    private final PokeInfoCalculator pokeInfoCalculator;
    private final HashMap<String, String> userCorrections;
    /* We don't want memory usage to get out of hand for stuff that can be computed. */
    private final LruCache<String, Pair<String, Integer>> cachedCorrections;

    public PokemonNameCorrector(PokeInfoCalculator pokeInfoCalculator, Map<String, String> storedUserCorrections) {
        this.pokeInfoCalculator = pokeInfoCalculator;
        userCorrections = new HashMap<>(pokeInfoCalculator.getPokedex().size());
        userCorrections.putAll(storedUserCorrections);
        userCorrections.put("Sparky", pokeInfoCalculator.get(132).name);
        userCorrections.put("Rainer", pokeInfoCalculator.get(132).name);
        userCorrections.put("Pyro", pokeInfoCalculator.get(132).name);
        cachedCorrections = new LruCache<>(pokeInfoCalculator.getPokedex().size() * 2);
    }

    public void putCorrection(String ocredPokemonName, String correctedPokemonName) {
        /* TODO: Should we set a size limit on that and throw away LRU entries? */
        userCorrections.put(ocredPokemonName, correctedPokemonName);
    }

    @AllArgsConstructor
    public static class PokeDist {
        public final int pokemonId;
        public final int dist;
    }

    /**
     * Fix Nidoran gender identification
     *
     * @param ocrText   The text that OCR generated
     * @param p         The pokemon that was the best match for the OCR text
     * @return          a Pokemon that's fixed if it was Nidoran. Otherwise, unchanged.
     */
    private Pokemon fixNido(String ocrText, Pokemon p) {
        String pokeName = p.name;
        Pokemon p28 = pokeInfoCalculator.get(28);
        Pokemon p31 = pokeInfoCalculator.get(31);

        if (pokeName.equals(p28.name) || pokeName.equals(p31.name)) {
            char lastChar = ocrText.charAt(ocrText.length() - 1);
            if (lastChar == 'd') {
                return p31;
            } else if (lastChar == 'Q') {
                return p28;
            }
        }

        return p;
    }

    /**
     * Compute the most likely pokemon ID based on the pokemon and candy names.
     *
     * @return a PokeDist with pokemon ID and distance.
     */
    public PokeDist getPossiblePokemon(String poketext, String candytext) {
        int poketextDist = 0;
        int bestCandyMatch = Integer.MAX_VALUE;
        Pokemon p;

        /* If the user previous corrected this text, go with that. */
        if (userCorrections.containsKey(poketext)) {
            poketext = userCorrections.get(poketext);
        }

        /* If we already did similarity search for this, go with the cached value. */
        Pair<String, Integer> cached = cachedCorrections.get(poketext);
        if (cached != null) {
            poketext = cached.first;
            poketextDist = cached.second;
        }

        /* If the pokemon name was a perfect match, we are done. */
        p = pokeInfoCalculator.get(poketext);
        if (p != null) {
            return new PokeDist(p.number, poketextDist);
        }

        /* If not, we limit the Pokemon search by candy name first since fewer valid candy names
         * options should mean fewer false matches. */
        p = pokeInfoCalculator.get(candytext);

        /* If we can't find perfect candy match, do a distance/similarity based match */
        if (p == null) {
            for (Pokemon trypoke : pokeInfoCalculator.getPokedex()) {
                /* Candy names won't match evolutions */
                if (trypoke.devoNumber != -1) {
                    continue;
                }

                int dist = trypoke.getDistanceCaseInsensitive(candytext);
                if (dist < bestCandyMatch) {
                    p = trypoke;
                    bestCandyMatch = dist;
                }
            }
            assert p != null;
        } else {
            bestCandyMatch = 0;
        }

        p = fixNido(candytext, p);

        /* Search through all the pokemon with the same candy name and pick the one with the best
         * match to the pokemon name (not the candy name) */
        ArrayList<Pokemon> candylist = new ArrayList<>();
        candylist.add(p);
        candylist.addAll(p.evolutions);
        /* If the base pokemon has only one evolution, they we consider another level of evolution */
        if (p.evolutions.size() == 1) {
            candylist.addAll(p.evolutions.get(0).evolutions);
        }

        int bestMatch = Integer.MAX_VALUE;
        for (Pokemon candyp : candylist) {
            int dist = candyp.getDistance(poketext);
            if (dist < bestMatch) {
                p = candyp;
                bestMatch = dist;
            }
        }

        /* Adding the candy distance and the pokemon name distance gives a better idea of how much
         * guess is going on. */
        int dist = bestCandyMatch + bestMatch;

        /* Cache this correction. We don't really need to save this across launches. */
        cachedCorrections.put(poketext, new Pair<>(p.name, dist));

        return new PokeDist(p.number, dist);
    }
}
