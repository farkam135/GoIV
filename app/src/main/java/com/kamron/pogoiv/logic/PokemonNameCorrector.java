package com.kamron.pogoiv.logic;

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
    private final LruCache<String, PokeDist> cachedCorrections;

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
     * 3. check if there's a cached result for the nickname bitmap hash
     * 4. check if there's a stored user correction for the scanned pokemon name
     * 5. get the pokemon with the closest name within the evolution line guessed from the candy
     * <p>
     * The order is decided by having high reliability guessing modules run first, and if they cant find an answer,
     * fall back to less accurate methods.
     *
     * @param poketext         the scanned pokemon nickname
     * @param candytext        the scanned pokemon candy name
     * @param candyUpgradeCost the scanned pokemon evolution candy cost
     * @return a Pokedist with the best guess of the pokemon
     */
    public PokeDist getPossiblePokemon(String poketext, String candytext, Optional<Integer> candyUpgradeCost) {
        ArrayList<Pokemon> bestGuessEvolutionLine = getBestGuessForEvolutionLine(candytext);
        PokeDist guess = null;
        //Warning: Don't forget to call cacheResult before exiting. Preferably keep a single return point.

        //1. Check if nickname perfectly matches a pokemon (which means pokemon is probably not renamed)
        guess = new PokeDist(pokeInfoCalculator.get(poketext), 0);

        //2. See if we can get a perfect match with candy name & upgrade cost
        if (guess.pokemon == null) {
            Pokemon candyAndUpgradeGuess = getCandyNameEvolutionCostGuess(bestGuessEvolutionLine,
                    candyUpgradeCost);
            if (candyAndUpgradeGuess != null) {
                guess = new PokeDist(candyAndUpgradeGuess, 0);
            }
        }

        //3. check if there's a cached guess for the hash of the bitmap of the nickname
        //TODO
        // Cacheguess and userCorrections are two different caches, one checks for the bitmap cache, one
        // checks for the scanned string cache, however the bitmap cache does not seem to save properly - I'm
        // disabling this module until it's been fixed. - Though we could maybe remove this guessing module altogether.
        /*
        if (guess.pokemon == null) {
            PokeDist cacheGuess = cachedCorrections.get(poketext);
            if (cacheGuess != null) {
                guess =  new PokeDist(cacheGuess.pokemon, cacheGuess.dist);
            }
        }*/

        //4.  If the user previous corrected this text, go with that.
        if (guess.pokemon == null) {
            if (userCorrections.containsKey(poketext)) {
                poketext = userCorrections.get(poketext);
                guess = new PokeDist(pokeInfoCalculator.get(poketext), 20);
            }
        }
        //5. All else failed: make a wild guess based only on closest name match
        if (guess.pokemon == null) {
            guess = getNicknameGuess(poketext, pokeInfoCalculator.getPokedex());
        }
        cacheResult(poketext, guess);
        return guess;
    }

    /**
     * Cache a result.
     *
     * @param poketext   the text to put in the cache as the scanned nickname
     * @param cacheValue the value for the pokemon and the distance (how much we estimated)
     */
    private void cacheResult(String poketext, PokeDist cacheValue) {
        cachedCorrections.put(poketext, cacheValue);
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
        if (evolutionCost.isPresent()) {
            if (bestGuessEvolutionLine.get(0).evolutions.size() > 1 && evolutionCost.get() == -1) {
                return null; //found multiple possible evolutions, dont know which one is the real answer
            }

            for (Pokemon pokemon : bestGuessEvolutionLine) {
                if (pokemon.candyEvolutionCost == evolutionCost.get()) {
                    return pokemon;
                }
            }
        }


        //evolution cost scan failed, or no match
        return null;
    }

    /**
     * A method which returns the best guess at which pokemon it is according to similarity with the nickname
     * in the given pokemon list.
     *
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
        return new PokeDist(bestMatchPokemon, lowestDist);
    }

    /**
     * A class representing a result of pokemon search.
     */
    @AllArgsConstructor
    public static class PokeDist {
        /**
         * A pokemon.
         */
        public final Pokemon pokemon;

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
        return pokeInfoCalculator.getEvolutionLine(bestMatch.pokemon);
    }
}
