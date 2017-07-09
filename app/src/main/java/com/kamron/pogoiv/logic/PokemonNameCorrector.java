package com.kamron.pogoiv.logic;


import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;

/**
 * Component for user-trainable autocorrection of pokemon names.
 * Responsibility for storing and loading user corrections rests with the caller.
 * Created by pgiarrusso on 5/9/2016.
 */
public class PokemonNameCorrector {
    private final PokeInfoCalculator pokeInfoCalculator;

    public PokemonNameCorrector(PokeInfoCalculator pokeInfoCalculator) {
        this.pokeInfoCalculator = pokeInfoCalculator;
    }

    /**
     * Gets the best matching pokemon that can be found given the input, by doing the following:
     * 1. check if the nickname perfectly matches a pokemon
     * 2. check if candyname + evolution cost perfectly matches a pokemon
     * 3. check correction for Eevee’s Evolution
     * 4. get the pokemon with the closest name within the evolution line guessed from the candy
     * 5. All else failed: make a wild guess based only on closest name match
     * <p>
     * The order is decided by having high reliability guessing modules run first, and if they cant find an answer,
     * fall back to less accurate methods.
     *
     * @param poketext         the scanned pokemon nickname
     * @param candytext        the scanned pokemon candy name
     * @param candyUpgradeCost the scanned pokemon evolution candy cost
     * @return a Pokedist with the best guess of the pokemon
     */
    public PokeDist getPossiblePokemon(String poketext, String candytext, Optional<Integer> candyUpgradeCost,
                                       String pokemonType) {
        ArrayList<Pokemon> bestGuessEvolutionLine = null;
        PokeDist guess;

        //1. Check if nickname perfectly matches a pokemon (which means pokemon is probably not renamed)
        guess = new PokeDist(pokeInfoCalculator.get(poketext), 0);

        //2. See if we can get a perfect match with candy name & upgrade cost
        if (guess.pokemon == null) {
            bestGuessEvolutionLine = getBestGuessForEvolutionLine(candytext);

            ArrayList<Pokemon> candyNameEvolutionCostGuess =
                    getCandyNameEvolutionCostGuess(bestGuessEvolutionLine, candyUpgradeCost);
            if (candyNameEvolutionCostGuess != null) {
                if (candyNameEvolutionCostGuess.size() == 1) {
                    //we have only one guess this is the one
                    guess = new PokeDist(candyNameEvolutionCostGuess.get(0), 0);
                } else if (candyNameEvolutionCostGuess.size() > 1) {
                    //if we have multiple guesses let the PokeDist guess based on name
                    bestGuessEvolutionLine = candyNameEvolutionCostGuess;
                }
            }
        }


        //3.  check correction for Eevee’s Evolution using it's Pokemon Type
        if (guess.pokemon == null && candytext.toLowerCase().contains(pokeInfoCalculator.get(132).name.toLowerCase())) {
            HashMap<String, String> eeveelutionCorrection = new HashMap<>();
            // It might be good to move this to a resource at some point
            eeveelutionCorrection.put("WATER", pokeInfoCalculator.get(133).name); //Vaporeon
            eeveelutionCorrection.put("ELECTRIC", pokeInfoCalculator.get(134).name); //Jolteon
            eeveelutionCorrection.put("FIRE", pokeInfoCalculator.get(135).name); //Flareon
            eeveelutionCorrection.put("PSYCHIC", pokeInfoCalculator.get(195).name); //Espeon
            eeveelutionCorrection.put("DARK", pokeInfoCalculator.get(196).name); //Umbreon
            // Preparing for the future....
            // eeveelutionCorrection.put("GRASS", pokeInfoCalculator.get(469).name); //Leafeon
            // eeveelutionCorrection.put("ICE", pokeInfoCalculator.get(470).name); //Glaceon
            // eeveelutionCorrection.put("FAIRY", pokeInfoCalculator.get(699).name); //Sylveon
            if (eeveelutionCorrection.containsKey(pokemonType)) {
                poketext = eeveelutionCorrection.get(pokemonType);
                guess = new PokeDist(pokeInfoCalculator.get(poketext), 0);
            }
        }

        //4. maybe the candy upgrade cost was scanned wrong because the candy icon was interpreted as a number (for
        // example the black candy is not cleaned by the ocr). Try checking if any in the possible evolutions that
        // match without the first character.
        if (guess.pokemon == null && candyUpgradeCost.isPresent()) {
            String textInterpretation = candyUpgradeCost.get().toString();
            String cleanedInterpretation = textInterpretation.substring(1, textInterpretation.length());
            Optional<Integer> cleanedInt = Optional.of(Integer.parseInt(cleanedInterpretation));
            ArrayList<Pokemon> candyNameEvolutionCostGuess =
                    getCandyNameEvolutionCostGuess(bestGuessEvolutionLine, cleanedInt);
            if (candyNameEvolutionCostGuess != null) {
                if (candyNameEvolutionCostGuess.size() == 1) {
                    //we have only one guess this is the one
                    guess = new PokeDist(candyNameEvolutionCostGuess.get(0), 0);
                } else if (candyNameEvolutionCostGuess.size() > 1) {
                    //if we have multiple guesses let the PokeDist guess based on name
                    bestGuessEvolutionLine = candyNameEvolutionCostGuess;
                }
            }
        }

        //5.  get the pokemon with the closest name within the evolution line guessed from the candy (or candy and
        // cost calculation).
        if (guess.pokemon == null && bestGuessEvolutionLine != null) {
            guess = getNicknameGuess(poketext, bestGuessEvolutionLine);
        }


        //6. All else failed: make a wild guess based only on closest name match
        if (guess.pokemon == null) {
            guess = getNicknameGuess(poketext, pokeInfoCalculator.getPokedex());
        }
        return guess;
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
    private ArrayList<Pokemon> getCandyNameEvolutionCostGuess(ArrayList<Pokemon> bestGuessEvolutionLine,
                                                              Optional<Integer> evolutionCost) {
        if (evolutionCost.isPresent()) {
            ArrayList<Pokemon> PokemonValidOptions = new ArrayList<Pokemon>();
            for (Pokemon pokemon : bestGuessEvolutionLine) {
                if (evolutionCost.get().equals(pokemon.candyEvolutionCost)) {
                    PokemonValidOptions.add(pokemon);
                }
            }
            return PokemonValidOptions;
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
     * Get the evolution line which closest matches the string. The string is supposed to be the base evolution of a
     * line.
     *
     * @param input the base evolution (ex weedle) to find a match for
     * @return an evolution line which the string best matches the base evolution pokemon name
     */
    private ArrayList<Pokemon> getBestGuessForEvolutionLine(String input) {
        //candy name will only ever match the base evolution, so search in getBasePokemons().
        PokeDist bestMatch = getNicknameGuess(input, pokeInfoCalculator.getCandyPokemons());
        return pokeInfoCalculator.getEvolutionLine(bestMatch.pokemon);
    }

    /**
     * A class representing a result of pokemon search. A higher distance means the result was more uncertain. This
     * is used to colorize the background for the guessed pokemon in the overlay input screen.
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
}
