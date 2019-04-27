package com.kamron.pogoiv.scanlogic;


import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.utils.StringUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

import static com.kamron.pogoiv.scanlogic.Pokemon.Type;

/**
 * Component for user-trainable autocorrection of pokemon names.
 * Responsibility for storing and loading user corrections rests with the caller.
 * Created by pgiarrusso on 5/9/2016.
 */
public class PokemonNameCorrector {
    private static PokemonNameCorrector instance;
    private final PokeInfoCalculator pokeInfoCalculator;
    private final Map<String, PokemonBase> normalizedPokemonNameMap;
    private final Map<String, PokemonBase> normalizedCandyPokemons;
    private final Map<Pokemon.Type, String> normalizedTypeNames;
    private Resources res;
    private static String nidoFemale;
    private static String nidoMale;
    private static String nidoUngendered;

    private PokemonNameCorrector(Context context) {
        this.pokeInfoCalculator = PokeInfoCalculator.getInstance(context);

        // create and cache the pokedex pokemons collection with normalized their names as keys
        Map<String, PokemonBase> pokemap = new HashMap<>();
        for (PokemonBase pokemon : pokeInfoCalculator.getPokedex()) {
            pokemap.put(StringUtils.normalize(pokemon.name), pokemon);
        }
        this.normalizedPokemonNameMap = pokemap;
        this.res = context.getResources();

        nidoFemale = StringUtils.normalize(pokeInfoCalculator.get(28).name);
        nidoMale = StringUtils.normalize(pokeInfoCalculator.get(31).name);
        nidoUngendered = nidoFemale.replace("♀", "").toLowerCase();

        // create and cache the normalized pokemon type locale name
        this.normalizedTypeNames = new EnumMap<>(Pokemon.Type.class);
        for (int i = 0; i < res.getStringArray(R.array.typeName).length; i++) {
            this.normalizedTypeNames.put(Pokemon.Type.values()[i],
                    StringUtils.normalize(res.getStringArray(R.array.typeName)[i]));
        }

        // create and cache the candy pokemons collection with normalized their names as keys
        this.normalizedCandyPokemons = new HashMap<>();
        for (PokemonBase pokemon : pokeInfoCalculator.getCandyPokemons()) {
            this.normalizedCandyPokemons.put(StringUtils.normalize(pokemon.name), pokemon);
        }
    }

    public static PokemonNameCorrector getInstance(Context context) {
        if (instance == null) {
            instance = new PokemonNameCorrector(context);
        }

        return instance;
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
        String normalizedPokemonName = getNormalizedPokemonName(scanData);
        String normalizedCandyName = getNormalizedCandyName(scanData);
        ArrayList<Pokemon> bestGuessEvolutionLine = null;
        PokeDist guess;

        //1. Check if nickname perfectly matches a pokemon (which means pokemon is probably not renamed)
        Pokemon namedGuess = null;
        if (normalizedPokemonNameMap.containsKey(normalizedPokemonName)) {
            namedGuess = normalizedPokemonNameMap.get(normalizedPokemonName).forms.get(0);
        }
        guess = new PokeDist(namedGuess, 0);

        //2. See if we can get a perfect match with candy name & upgrade cost
        if (guess.pokemon == null) {
            bestGuessEvolutionLine = getBestGuessForEvolutionLine(normalizedCandyName);

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
                && normalizedCandyName.contains(StringUtils.normalize(pokeInfoCalculator.get(132).name))) {
            HashMap<String, Integer> eeveelutionCorrection = new HashMap<>();
            eeveelutionCorrection.put(normalizePokemonType(Type.WATER), 133); //Vaporeon pokedex#
            eeveelutionCorrection.put(normalizePokemonType(Type.ELECTRIC), 134); //Jolteon pokedex#
            eeveelutionCorrection.put(normalizePokemonType(Type.FIRE), 135); //Flareon pokedex#
            eeveelutionCorrection.put(normalizePokemonType(Type.PSYCHIC), 195); //Espeon pokedex#
            eeveelutionCorrection.put(normalizePokemonType(Type.DARK),196); //Umbreon pokedex#
            // Preparing for the future....
            // eeveelutionCorrection.put(normalizePokemonType(Type.GRASS), 469); //Leafeon pokedex#
            // eeveelutionCorrection.put(normalizePokemonType(Type.ICE), 470); //Glaceon pokedex#
            // eeveelutionCorrection.put(normalizePokemonType(Type.FAIRY), 699); //Sylveon pokedex#
            if (eeveelutionCorrection.containsKey(scanData.getNormalizedPokemonType())) {
                int eeveelutionPokedexId = eeveelutionCorrection.get(scanData.getNormalizedPokemonType());
                guess = new PokeDist(pokeInfoCalculator.getForm(eeveelutionPokedexId), 0);
            }
        }

        //3.1 Azuril and marill have the same evolution cost, but different types.
        if (normalizedCandyName.contains(StringUtils.normalize(pokeInfoCalculator.get(182).name))
                && (scanData.getEvolutionCandyCost().get() != -1)) { //its not an azumarill
            //if the scanned data contains the type water, it must be a marill, as azurill is normal type.
            if (scanData.getNormalizedPokemonType().contains(normalizePokemonType(Type.WATER))) {
                guess = new PokeDist(pokeInfoCalculator.getForm(182), 0); //marill
            } else {
                guess = new PokeDist(pokeInfoCalculator.getForm(297), 0); //azurill
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
            Map<String, Pokemon> pokemap = new HashMap<>();
            for (Pokemon pokemon : bestGuessEvolutionLine) {
                pokemap.put(StringUtils.normalize(pokemon.name), pokemon);
            }
            guess = guessBestPokemonByNormalizedName(normalizedPokemonName, pokemap);
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
            guess = guessBestPokemonBaseByNormalizedName(normalizedPokemonName, normalizedPokemonNameMap);
        }


        //if (guess.pokemon.number)
        return guess;
    }

    private String getNormalizedPokemonName(ScanData scanData) {
        String normalizedPokemonName = scanData.getNormalizedPokemonName();

        // remove characters not included in pokemon names or candy word. (ex. white space, -, etc)
        normalizedPokemonName = normalizedPokemonName.replaceAll("[^\\w♂♀]", "");
        if (normalizedPokemonName.contains(nidoUngendered)) {
            normalizedPokemonName = getNidoranGenderName(scanData.getPokemonGender());
        }

        return normalizedPokemonName;
    }

    private String getNormalizedCandyName(ScanData scanData) {
        String normalizedCandyName;
        String normalizedCandyText = scanData.getNormalizedCandyName();
        String candyWordLocale = res.getString(R.string.candy);

        // remove characters not included in pokemon names or candy word. (ex. white space, -, etc)
        normalizedCandyText = normalizedCandyText.replaceAll("[^\\w♂♀]", "");
        normalizedCandyName = normalizedCandyText.replace(StringUtils.normalize(candyWordLocale), "");
        if (normalizedCandyName.contains(nidoUngendered)) {
            normalizedCandyName = getNidoranGenderName(scanData.getPokemonGender());
        }

        return normalizedCandyName;
    }

    /**
     * Get the correctly gendered name of a pokemon.
     *
     * @param pokemonGender The gender of the nidoranX.
     * @return The correct name of the pokemon, with the gender symbol at the end.
     */
    @NonNull
    private static String getNidoranGenderName(Pokemon.Gender pokemonGender) {
        switch (pokemonGender) {
            case F: return nidoFemale;
            case M: return nidoMale;
            default: return "";
        }
    }

    /**
     * Returns the normalized type name for such as fire or water, in the correct current locale name.
     *
     * @param type The enum for the type to get the correct name for.
     */
    private String normalizePokemonType(Pokemon.Type type) {
        return normalizedTypeNames.get(type);
    }

    private PokeDist createFormPokeDist(PokeDist guess, ScanData scanData, Type pokeType, int formIndex) {
        if (scanData.getNormalizedPokemonType().contains(normalizePokemonType(pokeType))) {
            return new PokeDist(guess.pokemon.base.forms.get(formIndex), 0);
        } else {
            return null;
        }
    }

    private PokeDist createFormPokeDists(PokeDist guess, ScanData scanData, Type... pokeTypes) {
        for (int i = 0; i < pokeTypes.length; i++) {
            PokeDist dist = createFormPokeDist(guess, scanData, pokeTypes[i], i);
            if (dist != null) {
                return dist;
            }
        }
        return null;
    }

    private PokeDist checkAlolanVariant(PokeDist guess, ScanData scanData) {
        try {
            switch (guess.pokemon.number) {
                case (102): // Exeggutor (dex 103)
                    // check types including dragon
                    return createFormPokeDist(guess, scanData, Type.DRAGON, 1);
                case (18): // Rattata
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (19): // Raticate
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (25): // Raichu
                    // check types including psychic
                    return createFormPokeDist(guess, scanData, Type.PSYCHIC, 1);
                case (26): // Sandshrew
                    // check types including ice
                    return createFormPokeDist(guess, scanData, Type.ICE, 1);
                case (27): // Sandslash
                    // check types including ice
                    return createFormPokeDist(guess, scanData, Type.ICE, 1);
                case (36): // Vulpix
                    // check types including ice
                    return createFormPokeDist(guess, scanData, Type.ICE, 1);
                case (37): // Ninetales
                    // check types including ice
                    return createFormPokeDist(guess, scanData, Type.ICE, 1);
                case (49): // Diglett
                    // check types including steel
                    return createFormPokeDist(guess, scanData, Type.STEEL, 1);
                case (50): // Dugtrio
                    // check types including steel
                    return createFormPokeDist(guess, scanData, Type.STEEL, 1);
                case (51): // Meowth
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (52): // Persian
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (73): // Geodude
                    // check types including electric
                    return createFormPokeDist(guess, scanData, Type.ELECTRIC, 1);
                case (74): // Graveler
                    // check types including electric
                    return createFormPokeDist(guess, scanData, Type.ELECTRIC, 1);
                case (75): // Golem
                    // check types including electric
                    return createFormPokeDist(guess, scanData, Type.ELECTRIC, 1);
                case (87): // Grimer
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (88): // Muk
                    // check types including dark
                    return createFormPokeDist(guess, scanData, Type.DARK, 1);
                case (104): // Marowak
                    // check types including fire
                    return createFormPokeDist(guess, scanData, Type.FIRE, 1);
                case (412): // Wormadam
                    return createFormPokeDists(guess, scanData, Type.DARK, Type.GROUND, Type.STEEL);
                case (478): // Rotom
                    return createFormPokeDists(guess, scanData, Type.GHOST, Type.ICE, Type.FLYING, Type.GRASS,
                            Type.WATER, Type.FIRE);
                case (491): // Shayamin
                    PokeDist dist = createFormPokeDist(guess, scanData, Type.FLYING, 1);
                    if (dist == null) {
                        dist = new PokeDist(guess.pokemon.base.forms.get(0), 0);
                    }
                    return dist;
                case (492): // Rotom
                    return createFormPokeDists(guess, scanData, Type.NORMAL, Type.FIGHTING, Type.FLYING, Type.POISON,
                            Type.GROUND, Type.ROCK, Type.BUG, Type.GHOST, Type.STEEL, Type.FIRE, Type.WATER, Type.GRASS,
                            Type.ELECTRIC, Type.PSYCHIC, Type.ICE, Type.DRAGON, Type.DARK, Type.FAIRY);
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
     * A method which returns the best guess at which pokemon it is according to similarity with the name
     * in the given pokemons collection.
     *
     * @param normalizedName the normalized input name to compare with
     * @param pokemons the pokemons collection with normalized their names as keys to search the normalized name into.
     * @return a pokedist representing the search result.
     */
    private PokeDist guessBestPokemonBaseByNormalizedName(String normalizedName, Map<String, PokemonBase> pokemons) {
        Dist<PokemonBase> dist = guessBestMatch(normalizedName, pokemons);
        Pokemon bestMatch;
        if (dist.object != null) {
            bestMatch = dist.object.forms.get(0);
        } else {
            bestMatch = null;
        }
        return new PokeDist(bestMatch, dist.dist);
    }

    /**
     * A method which returns the best guess at which pokemon it is according to similarity with the name
     * in the given pokemons collection.
     *
     * @param normalizedName the normalized input name to compare with
     * @param pokemons the pokemons collection with normalized their names as keys to search the normalized name into.
     * @return a pokedist representing the search result.
     */
    private PokeDist guessBestPokemonByNormalizedName(String normalizedName, Map<String, Pokemon> pokemons) {
        return PokeDist.fromDist(guessBestMatch(normalizedName, pokemons));
    }

    /**
     * A method which returns the best guess at which pokemon it is according to similarity with the name
     * in the given pokemons collection.
     *
     * @param normalizedName the normalized input name to compare with
     * @param pokemons the pokemons collection with normalized their names as keys to search the normalized name into.
     * @return a pokedist representing the search result.
     */
    private <T> Dist<T> guessBestMatch(String normalizedName, Map<String, T> pokemons) {
        //if there's no perfect match, get the pokemon that best matches the nickname within the best guess evo-line
        T bestMatch = null;
        int lowestDist = Integer.MAX_VALUE;
        for (Map.Entry<String, T> trypoke : pokemons.entrySet()) {
            int dist = Data.levenshteinDistance(trypoke.getKey(), normalizedName);
            if (dist < lowestDist) {
                bestMatch = trypoke.getValue();
                lowestDist = dist;
                if (dist == 0) break;
            }
        }
        return new Dist<>(bestMatch, lowestDist);
    }

    /**
     * Get the evolution line which closest matches the string. The string is supposed to be the base evolution of a
     * line.
     *
     * @param input the base evolution (ex weedle) to find a match for
     * @return an evolution line which the string best matches the base evolution pokemon name
     */
    private ArrayList<Pokemon> getBestGuessForEvolutionLine(String input) {
        PokeDist bestMatch = guessBestPokemonBaseByNormalizedName(input, normalizedCandyPokemons);
        return pokeInfoCalculator.getEvolutionLine(bestMatch.pokemon);
    }

    /**
     * A class representing a result of pokemon search. A higher distance means the result was more uncertain. This
     * is used to colorize the background for the guessed pokemon in the overlay input screen.
     */
    @AllArgsConstructor
    public static class Dist<T> {
        /**
         * A pokemon.
         */
        public final T object;

        /**
         * A string distance between a searched pokemon name and the name of pokemonId.
         * Since it's a distance, the smaller it is the closer is the match.
         */
        public final int dist;
    }

    // TODO: Replace everywhere with Dist<Pokemon>?
    public static class PokeDist extends Dist<Pokemon> {

        public final Pokemon pokemon;

        public PokeDist(Pokemon pokemon, int dist) {
            super(pokemon, dist);
            this.pokemon = pokemon;
        }

        public static PokeDist fromDist(Dist<Pokemon> dist) {
            return new PokeDist(dist.object, dist.dist);
        }
    }
}
