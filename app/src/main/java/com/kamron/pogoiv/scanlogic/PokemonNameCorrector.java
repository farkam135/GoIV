package com.kamron.pogoiv.scanlogic;


import android.support.annotation.NonNull;

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
                && scanData.getCandyName().toLowerCase().contains(pokeInfoCalculator.get(132).name.toLowerCase())) {
            HashMap<String, String> eeveelutionCorrection = new HashMap<>();
            eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(2), //WATER
                    pokeInfoCalculator.get(133).name); //Vaporeon
            eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(3), //ELECTRIC
                    pokeInfoCalculator.get(134).name); //Jolteon
            eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(1), //FIRE
                    pokeInfoCalculator.get(135).name); //Flareon
            eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(10), //PSYCHIC
                    pokeInfoCalculator.get(195).name); //Espeon
            eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(15), //DARK
                    pokeInfoCalculator.get(196).name); //Umbreon
            // Preparing for the future....
            // eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(4), //GRASS
            //         pokeInfoCalculator.get(469).name); //Leafeon
            // eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(5), //ICE
            //         pokeInfoCalculator.get(470).name); //Glaceon
            // eeveelutionCorrection.put(pokeInfoCalculator.getTypeName(17), //FAIRY
            //         pokeInfoCalculator.get(699).name); //Sylveon
            if (eeveelutionCorrection.containsKey(scanData.getPokemonType())) {
                String name = eeveelutionCorrection.get(scanData.getPokemonType());
                guess = new PokeDist(pokeInfoCalculator.get(name), 0);
            }
        }

        //3.1 Azuril and marill have the same evolution cost, but different types.
        if (scanData.getCandyName().toLowerCase().contains(pokeInfoCalculator.get(182).name.toLowerCase())
                && (scanData.getEvolutionCandyCost().get() != -1)){ //its not an azumarill
            //if the scanned data contains the type water, it must be a marill, as azuril is normal type.
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(2))){
                guess = new PokeDist(pokeInfoCalculator.get(182), 0);
            } else{
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


        //6. All else failed: make a wild guess based only on closest name match
        if (guess.pokemon == null) {
            guess = getNicknameGuess(scanData.getPokemonName(), pokeInfoCalculator.getPokedex());
        }

        //Check if the found pokemon should be alolan variant or not.
        PokeDist aloGuess = null;
        if (guess != null) {
            if (guess.pokemon != null) {
                if (scanData.getPokemonType() != null){
                    if (scanData.getPokemonType().equals("") == false){
                        aloGuess = checkForAlolanVariant(guess, scanData);
                    }
                }
            }
        }

        if (aloGuess != null) {
            guess = aloGuess;
        }

        //if (guess.pokemon.number)
        return guess;
    }

    /**
     * Checks if a pokemon has an alolan variant, and if it does, checks if the scanned values has typing (Rock,
     * grass etc) that fits the alolan or normal variant. IF it fits the alolan variant, returns a pokedist
     * containing the alolan result, otherwise returns null.
     * value
     *
     * @param guess    The already calculated pokemon.
     * @param scanData The scanned pokemon data
     * @return The alolan variant if the scanned typing matches alolan, or null.
     */
    private PokeDist checkForAlolanVariant(PokeDist guess, ScanData scanData) {

        System.out.println("asdasdasd exeggutor" + pokeInfoCalculator.get("alolan exeggutor").number);
        if (guess.pokemon.number == 102) {//pokeInfoCalculator.get("exeggutor").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(14))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(389), 0);
                return aloGuess;
            }
        }

        System.out.println("asdasdasd rattata" + pokeInfoCalculator.get("alolan rattata").number);
        if (guess.pokemon.number == 18) {//pokeInfoCalculator.get("rattata").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(390), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd raticate" + pokeInfoCalculator.get("alolan raticate").number);
        if (guess.pokemon.number == 19) {//pokeInfoCalculator.get("raticate").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(391), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd raichu" + pokeInfoCalculator.get("alolan raichu").number);
        if (guess.pokemon.number == 25) {//pokeInfoCalculator.get("raichu").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(10))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(392), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd sandshrew" + pokeInfoCalculator.get("alolan sandshrew").number);
        if (guess.pokemon.number == 26 ) {//pokeInfoCalculator.get("sandshrew").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(5))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(393), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd sandslash" + pokeInfoCalculator.get("alolan sandslash").number);
        if (guess.pokemon.number ==27) {//pokeInfoCalculator.get("sandslash").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(5))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(394), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd vulpix" + pokeInfoCalculator.get("alolan vulpix").number);
        if (guess.pokemon.number == 36) {//pokeInfoCalculator.get("vulpix").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(5))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(395), 0);
                return aloGuess;
            }
        }

        System.out.println("asdasdasd ninetales" + pokeInfoCalculator.get("alolan ninetales").number);
        if (guess.pokemon.number == 37) {//pokeInfoCalculator.get("ninetales").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(5))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(396), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd diglett" + pokeInfoCalculator.get("alolan diglett").number);
        if (guess.pokemon.number == 49) {//pokeInfoCalculator.get("diglett").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(16))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(397), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd dugtrio" + pokeInfoCalculator.get("alolan dugtrio").number);
        if (guess.pokemon.number == 50) {//pokeInfoCalculator.get("dugtrio").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(16))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(398), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd meowth" + pokeInfoCalculator.get("alolan meowth").number);
        if (guess.pokemon.number == 51) {//pokeInfoCalculator.get("meowth").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(399), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd persian" + pokeInfoCalculator.get("alolan persian").number);
        if (guess.pokemon.number == 52) {//pokeInfoCalculator.get("persian").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(400), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd geodude" + pokeInfoCalculator.get("alolan geodude").number);
        if (guess.pokemon.number == 73) {//pokeInfoCalculator.get("geodude").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(3))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(401), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd graveler" + pokeInfoCalculator.get("alolan graveler").number);
        if (guess.pokemon.number == 74) {//pokeInfoCalculator.get("graveler").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(3))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(402), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd golem" + pokeInfoCalculator.get("alolan golem").number);
        if (guess.pokemon.number == 75) {//pokeInfoCalculator.get("golem").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(3))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(403), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd grimer" + pokeInfoCalculator.get("alolan grimer").number);
        if (guess.pokemon.number == 87) {//pokeInfoCalculator.get("grimer").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(404), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd muk" + pokeInfoCalculator.get("alolan muk").number);
        if (guess.pokemon.number == 88) {//pokeInfoCalculator.get("muk").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(15))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(405), 0);
                return aloGuess;
            }
        }
        System.out.println("asdasdasd marowak" + pokeInfoCalculator.get("alolan marowak").number);
        if (guess.pokemon.number == 104) {//pokeInfoCalculator.get("marowak").number) {
            if (scanData.getPokemonType().contains(pokeInfoCalculator.getTypeName(1))) {
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get(406), 0);
                return aloGuess;
            }
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
