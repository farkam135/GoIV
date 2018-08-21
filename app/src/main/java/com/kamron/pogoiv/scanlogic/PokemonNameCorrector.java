package com.kamron.pogoiv.scanlogic;


import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import com.kamron.pogoiv.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;

import static com.kamron.pogoiv.scanlogic.Pokemon.Type;

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
     * @param scanData The OCR'd data
     * @return a Pokedist with the best guess of the pokemon
     */
    public PokeDist getPossiblePokemon(@NonNull ScanData scanData) {
        ArrayList<Pokemon> bestGuessEvolutionLine = null;
        PokeDist guess;

        //1. Check if nickname perfectly matches a pokemon (which means pokemon is probably not renamed)
        guess = new PokeDist(pokeInfoCalculator.get(scanData.getPokemonName()), 0);

        //2. See if we can get a perfect match with candy name & upgrade cost
        if (guess.pokemon == null) {
            bestGuessEvolutionLine = getBestGuessForEvolutionLine(scanData.getCandyName());

            ArrayList<Pokemon> candyNameEvolutionCostGuess =
                    getCandyNameEvolutionCostGuess(bestGuessEvolutionLine, scanData.getEvolutionCandyCost());
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


        //3.  check correction for abnormal pokemon using Pokemon Type (such as eevees evolutions, azuril.)
        if (guess.pokemon == null
                && StringUtils.normalize(
                        scanData.getCandyName()).contains(StringUtils.normalize(pokeInfoCalculator.get(132).name))) {
            HashMap<String, String> eeveelutionCorrection = new HashMap<>();
            eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.WATER),
                    pokeInfoCalculator.get(133).name); //Vaporeon
            eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.ELECTRIC),
                    pokeInfoCalculator.get(134).name); //Jolteon
            eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.FIRE),
                    pokeInfoCalculator.get(135).name); //Flareon
            eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.PSYCHIC),
                    pokeInfoCalculator.get(195).name); //Espeon
            eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.DARK),
                    pokeInfoCalculator.get(196).name); //Umbreon
            // Preparing for the future....
            // eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.GRASS),
            //         pokeInfoCalculator.get(469).name); //Leafeon
            // eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.ICE),
            //         pokeInfoCalculator.get(470).name); //Glaceon
            // eeveelutionCorrection.put(pokeInfoCalculator.getNormalizedType(Type.FAIRY),
            //         pokeInfoCalculator.get(699).name); //Sylveon
            String normalizedPokemonType = StringUtils.normalize(scanData.getPokemonType());
            if (eeveelutionCorrection.containsKey(normalizedPokemonType)) {
                String name = eeveelutionCorrection.get(normalizedPokemonType);
                guess = new PokeDist(pokeInfoCalculator.get(name), 0);
            }
        }

        //3.1 Azuril and marill have the same evolution cost, but different types.
        if (StringUtils.normalize(
                scanData.getCandyName()).contains(StringUtils.normalize(pokeInfoCalculator.get(182).name))
                && (scanData.getEvolutionCandyCost().get() != -1)) { //its not an azumarill
            //if the scanned data contains the type water, it must be a marill, as azuril is normal type.
            if (StringUtils.normalize(scanData.getPokemonType()).contains(
                    pokeInfoCalculator.getNormalizedType(Type.WATER))) {
                guess = new PokeDist(pokeInfoCalculator.get(182), 0);
            } else {
                guess = new PokeDist(pokeInfoCalculator.get(297), 0);
            }
        }

        //4. maybe the candy upgrade cost was scanned wrong because the candy icon was interpreted as a number (for
        // example the black candy is not cleaned by the ocr). Try checking if any in the possible evolutions that
        // match without the first character.
        if (guess.pokemon == null && scanData.getEvolutionCandyCost().isPresent()) {
            String textInterpretation = scanData.getEvolutionCandyCost().get().toString();
            String cleanedInterpretation = textInterpretation.substring(1, textInterpretation.length());
            Optional<Integer> cleanedInt;
            try {
                cleanedInt = Optional.of(Integer.parseInt(cleanedInterpretation));
            } catch (NumberFormatException e) {
                cleanedInt = Optional.absent();
            }
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
            guess = getNicknameGuess(scanData.getPokemonName(), bestGuessEvolutionLine);
        }


        //6. Check if the found pokemon should be alolan variant or not.
        if (scanData != null && scanData.getPokemonType() != null) {
            PokeDist alolanGuess = checkAlolanVariant(guess, scanData);
            if (alolanGuess != null) {
                guess = alolanGuess;
            }

        }

        //7. All else failed: make a wild guess based only on closest name match
        if (guess.pokemon == null) {
            guess = getNicknameGuess(scanData.getPokemonName(), pokeInfoCalculator.getPokedex());
        }


        //if (guess.pokemon.number)
        return guess;
    }

    private PokeDist checkAlolanVariant(PokeDist guess,
                                        ScanData scanData) {
        try {
            switch (guess.pokemon.number) {
                case (102): // Exeggutor (dex 103)
                    // check types including dragon
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DRAGON))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (18): // Rattata
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (19): // Raticate
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (25): // Raichu
                    // check types including psychic
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.PSYCHIC))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (26): // Sandshrew
                    // check types including ice
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ICE))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (27): // Sandslash
                    // check types including ice
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ICE))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (36): // Vulpix
                    // check types including ice
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ICE))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (37): // Ninetales
                    // check types including ice
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ICE))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (49): // Diglett
                    // check types including steel
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.STEEL))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (50): // Dugtrio
                    // check types including steel
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.STEEL))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (51): // Meowth
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (52): // Persian
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (73): // Geodude
                    // check types including electric
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ELECTRIC))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (74): // Graveler
                    // check types including electric
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ELECTRIC))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (75): // Golem
                    // check types including electric
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.ELECTRIC))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (87): // Grimer
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (88): // Muk
                    // check types including dark
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.DARK))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;
                case (104): // Marowak
                    // check types including fire
                    if (StringUtils.normalize(scanData.getPokemonType()).contains(
                            pokeInfoCalculator.getNormalizedType(Type.FIRE))) {
                        return new PokeDist(pokeInfoCalculator.get(guess.pokemon.number).forms.get(0), 0);
                    }
                    break;

                default:
                    // do nothing

            }
        } catch (NullPointerException e) {
            return null;
        }

        return null;
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
            int dist = Data.levenshteinDistance(trypoke.name, poketext);
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
