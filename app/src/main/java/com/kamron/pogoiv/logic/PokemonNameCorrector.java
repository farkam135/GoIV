package com.kamron.pogoiv.logic;

import android.support.v4.util.Pair;
import android.util.LruCache;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @param candyUpgradeCost the scanned pokemon evolution candy cost
     * @return a Pokedist with the best guess of the pokemon
     */
    public PokeDist getPossiblePokemon(String poketext, String candytext, Optional<Integer> candyUpgradeCost) {
        ArrayList<Pokemon> bestGuessEvolutionLine = getBestGuessForEvolutionLine(candytext);

        //1. if nickname perfectly matches a pokemon, return that
        Pokemon perfectMatch = pokeInfoCalculator.get(poketext);
        if (perfectMatch != null) {
            PokeDist nicknameguess = new PokeDist(perfectMatch.number, 0);
            cacheResult(poketext, nicknameguess);
            return nicknameguess;
        }

        //1.5 if there's no perfect match, get the pokemon that best matches the nickname within the best guess evo-line
        PokeDist nicknameguess = getNicknameGuess(poketext, bestGuessEvolutionLine);

        if (nicknameguess.dist == 0) {
            cacheResult(poketext, nicknameguess);
            return nicknameguess;
        }

        //2. if we can get a perfect match with candy name & upgrade cost, return that
        Pokemon candyAndUpgradeGuess = getCandyNameEvolutionCostGuess(bestGuessEvolutionLine,
                candyUpgradeCost);
        if (candyAndUpgradeGuess != null) {
            PokeDist ret = new PokeDist(candyAndUpgradeGuess.number, 0);
            cacheResult(poketext, ret);
            return ret;
        }

        //3. if there's a cached result for the nickname, return that
        PokeDist cacheGuess = getCacheGuess(poketext);
        if (cacheGuess != null) {
            return cacheGuess;
        }

        //4. make a wild guess by returning whatever pokemon is closest to the nicknamee of the pokemon in what we
        // think is the evolution line from the candy
        cacheResult(poketext, nicknameguess);
        return nicknameguess;
    }

    /**
     * Cache a result.
     *
     * @param poketext   the text to put in the cache as the scanned nickname
     * @param cacheValue the value for the pokemon and the distance (how much we estimated)
     */
    private void cacheResult(String poketext, PokeDist cacheValue) {
        cachedCorrections.put(poketext, new Pair<>(pokeInfoCalculator.get(cacheValue.pokemonId).name, cacheValue.dist));
    }

    /**
     * A method which returns if there's a pokemon which matches the candy name & evolution cost. This method will
     * work regardless of whether the pokemon has been renamed or not.
     * Will find the closest match to candy name as assumption
     *
     * @param bestGuessEvolutionLine The evolution line guessed from the candy name
     * @param evolutionCost          the scanned cost to evolve the pokemon
     * @return a pokemon that perfectly matches the input, or null if no match was found
     */
    private Pokemon getCandyNameEvolutionCostGuess(ArrayList<Pokemon> bestGuessEvolutionLine,
                                                   Optional<Integer> evolutionCost) {
        if (!evolutionCost.isPresent()) {
            return null; //evolution cost scan failed
        }

        for (Pokemon pokemon : bestGuessEvolutionLine) {
            if (pokemon.candyEvolutionCost == evolutionCost.get()) {
                return pokemon;
            }
        }
        return null; //no match
    }

    /**
     * A method which returns the best guess at which pokemon it is according to similarity with the nickname
     * in the given pokemon list.
     * @param poketext the nickname to compare with
     * @param pokemons the pokemon list to search the nickname into.
     * @return a pokedist representing the search result.
     */
    private PokeDist getNicknameGuess(String poketext, List<Pokemon> pokemons) {
        //if there's no perfect match, get the pokemon that best matches the nickname within the best guess evo-line
        Pokemon bestMatchPokemon = null;
        int lowestDist = Integer.MAX_VALUE;
        for (Pokemon trypoke : pokemons) {
            int dist = trypoke.getDistanceCaseInsensitive(poketext);
            if (dist < lowestDist) {
                bestMatchPokemon = trypoke;
                lowestDist = dist;
            }
        }
        return new PokeDist(bestMatchPokemon.number, lowestDist);
    }

    /**
     * A class representing a result of pokemon search.
     */
    @AllArgsConstructor
    public static class PokeDist {
        /**
         * A pokemon ID.
         */
        public final int pokemonId;

        /**
         * A string distance between a searched pokemon name and the name of pokemonId.
         * Since it's a distance, the smaller it is the closer is the match.
         */
        public final int dist;
    }

    /**
     * Get the evolution line which closest matches the string. The string is supposed to be the base evolution of a
     * line.
     *
     * @param input the base evolution (ex weedle) to find a match for
     * @return an evolution line which the string best matches the base evolution pokemon name
     */
    private ArrayList<Pokemon> getBestGuessForEvolutionLine(String input) {
        //candy name will only ever match the base evolution, so search in getBasePokemons().
        PokeDist bestMatch = getNicknameGuess(input, pokeInfoCalculator.getBasePokemons());
        return pokeInfoCalculator.getEvolutionLine(pokeInfoCalculator.get(bestMatch.pokemonId));
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
}
