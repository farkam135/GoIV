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
     * @param scanData    The OCR'd data
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


        //3.  check correction for Eevee’s Evolution using it's Pokemon Type
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
        aloGuess = checkForAlolanVariant(guess, scanData);

        if (aloGuess != null){
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
     * @param guess The already calculated pokemon.
     * @param scanData The scanned pokemon data
     * @return The alolan variant if the scanned typing matches alolan, or null.
     */
    private PokeDist checkForAlolanVariant(PokeDist guess, ScanData scanData) {
        if (guess.pokemon.number == pokeInfoCalculator.get("exeggutor").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DRA")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan exeggutor"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("rattata").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan rattata"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("raticate").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan raticate"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("raichu").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("PSY")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan raichu"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("sandshrew").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ICE")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan sandshrew"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("sandslash").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ICE")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan sandslash"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("vulpix").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ICE")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan vulpix"), 0);
                return aloGuess;
            }
        }

        if (guess.pokemon.number == pokeInfoCalculator.get("ninetales").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("STEEL")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan ninetales"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("diglett").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ICE")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan diglett"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("dugtrio").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("STEEL")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan dugtrio"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("meowth").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan meowth"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("persian").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan persian"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("geodude").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ELECTRIC")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan geodude"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("graveler").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ELECTRIC")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan graveler"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("golem").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("ELECTRIC")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan golem"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("grimer").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan grimer"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("muk").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("DARK")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan muk"), 0);
                return aloGuess;
            }
        }
        if (guess.pokemon.number == pokeInfoCalculator.get("marowak").number){
            System.out.println(scanData.getPokemonType());
            if (scanData.getPokemonType().contains("GHOST")){
                PokeDist aloGuess = new PokeDist(pokeInfoCalculator.get("alolan marowak"), 0);
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
