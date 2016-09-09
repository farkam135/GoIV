package com.kamron.pogoiv.logic;

import android.support.v4.util.Pair;
import android.util.Log;
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

    /**
     * Gets the best matching pokemon that can be found given the input, by doing the following:
     * 1. check if the nickname perfectly matches a pokemon
     * 2. check if candyname + evolution cost perfectly matches a pokemon
     * 3. check if there's a cached result for the scanned pokemon name
     * 4. get the pokemon with the closest name within the evolution line guessed from the candy
     *
     * @param poketext         the scanned pokemon nickname
     * @param candytext        the scanned pokemon candy name
     * @param candyUpgradecost the scanned pokemon evolution candy cost
     * @return a Pokedist with the best guess of the pokemon
     */
    public PokeDist getPossiblePokemon(String poketext, String candytext, int candyUpgradecost) {
        Log.d("GuessPoke", "Scanned nick: " + poketext + " Scanned candy: " + candytext + " Scanned upgradecost"
                + (candyUpgradecost + ""));

        candytext = candytext.split(" ")[0]; //Sometimes it reads example "pidgey can", this removes "can"

        //1. if nickname perfectly matches a pokemon, return that
        PokeDist nicknameguess = getNicknameGuess(poketext, candytext);
        if (nicknameguess.dist == 0) {
            Log.d("GuessPoke", "1. Nickname guess perfect match: " + poketext);
            cacheResult(poketext, nicknameguess);
            return nicknameguess;
        }

        //2. if we can get a perfect match with candy name & upgrade cost, return that
        Pokemon candyAndUpgradeGuess = getCandyNameEvolutionCostGuess(candytext, candyUpgradecost);
        if (candyAndUpgradeGuess != null){
            Log.d("GuessPoke", "2. candy and upgrade guess perfect match: " + candyAndUpgradeGuess.name);
            cacheResult(poketext, new PokeDist(candyAndUpgradeGuess.number, 0));
            return new PokeDist(candyAndUpgradeGuess.number, 0);
        }

        //3. if there's a cached result for the nickname, return that
        PokeDist cacheGuess = getCacheGuess(poketext);
        if (cacheGuess != null){
            Log.d("GuessPoke", "3. cache guess remembered: " + pokeInfoCalculator.get(cacheGuess.pokemonId).name);
            return cacheGuess;
        }

        //4. make a wild guess by returning whatever pokemon is closest to the nicknamee of the pokemon in what we
        // think is the evolution line from the candy
        Log.d("GuessPoke", "4. Guessing based on nickname and candy name: " +  pokeInfoCalculator.get(nicknameguess
                .pokemonId).name);
        cacheResult(poketext, nicknameguess);
        return nicknameguess;
    }

    /**
     * Cache a result.
     *
     * @param poketext   the text to put in the cache as the scanned nickname
     * @param cacheValue the value for the pokemon and the distance (how much we estimated)
     */
    private void cacheResult(String poketext, PokeDist cacheValue){
        cachedCorrections.put(poketext, new Pair<>(pokeInfoCalculator.get(cacheValue.pokemonId).name, cacheValue.dist));
    }

    /**
     * Compute the most likely pokemon ID based on the pokemon and candy names.
     *
     * @return a PokeDist with pokemon ID and distance.
     */
    public PokeDist getPossiblePokemonOld(String poketext, String candytext, int candyUpgradeCost) {
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

    /**
     * A method which returns if there's a pokemon which matches the candy name & evolution cost. This method will
     * work regardless of whether the pokemon has been renamed or not.
     *
     * @param candyname     The candy name to search the evolution line of
     * @param evolutionCost the scanned cost to evolve the pokemon
     * @return a pokemon that perfectly matches the input, or null if no match was found
     */
    private Pokemon getCandyNameEvolutionCostGuess(String candyname, int evolutionCost) {
        Pokemon pokemonCandyMatch = pokeInfoCalculator.get(candyname);
        if (pokemonCandyMatch == null) return null;

        ArrayList<Pokemon> evolutionLine = pokeInfoCalculator.getEvolutionLine(pokemonCandyMatch);
        for (Pokemon pokemon : evolutionLine) {
            if (pokemon.candyEvolutionCost == evolutionCost) {
                return pokemon;
            }
        }
        return null; //no match
    }

    /**
     * A method which returns the best guess at which pokemon it is according to similarity with the nickname, if
     * nickname is perfect match, returns pokedist with pokemon id & distance 0.
     *
     * @param poketext the nickname to compare with
     * @return a pokedist with the best match pokemon as id, and the distance which is higher the further away the
     * poke guess was
     */
    private PokeDist getNicknameGuess(String poketext, String candytext) {
        //return if there's a perfect match
        Pokemon perfectMatch = pokeInfoCalculator.get(poketext);
        if (perfectMatch != null) {
            return new PokeDist(perfectMatch.number, 0);
        }

        //if there's no perfect match, get the pokemon that best matches the nickname within the best guess evo-line

        ArrayList<Pokemon> bestGuessEvolutionLine = getBestGuessForEvolutionLine(candytext);

        Pokemon bestMatchPokemon = null;
        int lowestDist = Integer.MAX_VALUE;
        for (Pokemon trypoke : bestGuessEvolutionLine) {

            int dist = trypoke.getDistanceCaseInsensitive(poketext);
            if (dist < lowestDist) {
                bestMatchPokemon = trypoke;
            }
        }
        return new PokeDist(bestMatchPokemon.number, lowestDist);
    }

    /**
     * get the evolution line which closest matches the scanned candyname
     *
     * @param candyname the candyname to find a match for
     * @return an evolutionline which the candyname best matches the base evolution pokemon name
     */
    private ArrayList<Pokemon> getBestGuessForEvolutionLine(String candyname) {
        Pokemon bestMatchCandy = null;
        int lowestDistCandy = Integer.MAX_VALUE;
        for (Pokemon trypoke : pokeInfoCalculator.getPokedex()) {
            if (trypoke.devoNumber != -1) continue; //candy name will only ever match the base evolution
            int dist = trypoke.getDistanceCaseInsensitive(candyname);
            if (dist < lowestDistCandy) {
                bestMatchCandy = trypoke;
            }
        }
        return pokeInfoCalculator.getEvolutionLine(bestMatchCandy);
    }

    /**
     * A method which returns the best guess at which pokemon it is according to the cache module.
     *
     * @param poketext the text to search for a cached value with
     * @return a pokedist with the result if cache exists, if no previous correction, returns null
     */
    private PokeDist getCacheGuess(String poketext) {
        /* If the user previous corrected this text, go with that. */
        int poketextDist = 0;
        Pokemon p;
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
        return null;
    }

    @AllArgsConstructor
    public static class PokeDist {
        public final int pokemonId;
        public final int dist;
    }
}
