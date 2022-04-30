package com.kamron.pogoiv.scanlogic;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Johan Swanberg on 2016-08-18.
 * A class which interprets pokemon information
 */
public class PokeInfoCalculator {
    private static PokeInfoCalculator instance;

    private ArrayList<PokemonBase> pokedex = new ArrayList<>();
    private List<Pokemon> formVariantPokemons;
    private String[] pokeNamesWithForm = {};

    //public static final int MELTAN_INDEX_OFFSET = 7;
    //public static final int MELMETAL_INDEX_OFFSET = 6;
    //public static final int OBSTAGOON_INDEX_OFFSET = 5;
    //public static final int PERRSERKER_INDEX_OFFSET = 4;
    //public static final int SIRFETCHD_INDEX_OFFSET = 3;
    //public static final int MRRIME_INDEX_OFFSET = 2;
    //public static final int RUNERIGUS_INDEX_OFFSET = 1;

    /**
     * Pokemons who's name appears as a type of candy.
     * For most, this is the basePokemon (ie: Pidgey candies)
     * For some, this is an original Gen1 Pokemon (ie: Magmar candies, instead of Magby candies)
     */
    private ArrayList<PokemonBase> candyPokemons = new ArrayList<>();

    protected static synchronized @NonNull PokeInfoCalculator getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new PokeInfoCalculator(GoIVSettings.getInstance(context), context.getResources());
        }
        return instance;
    }

    /**
     * Get the instance of pokeInfoCalculator. Must have been initiated first!
     *
     * @return the already activated instance of PokeInfoCalculator.
     */
    public static @Nullable PokeInfoCalculator getInstance() {
        return instance;
    }

    /**
     * Creates a pokemon info calculator with the pokemon as argument.
     *
     * @param settings Settings instance
     * @param res      System resources
     */
    private PokeInfoCalculator(@NonNull GoIVSettings settings, @NonNull Resources res) {
        populatePokemon(settings, res);

        // create and cache the full pokemon display name list
        ArrayList<String> pokemonNamesArray = new ArrayList<>();
        for (PokemonBase poke : getPokedex()) {
            for (Pokemon pokemonForm : poke.forms) {
                pokemonNamesArray.add(pokemonForm.toString());
            }
        }

        pokeNamesWithForm = pokemonNamesArray.toArray(new String[pokemonNamesArray.size()]);
    }

    public List<PokemonBase> getPokedex() {
        return Collections.unmodifiableList(pokedex);
    }

    public List<Pokemon> getPokedexForms() {
        return formVariantPokemons;
    }

    /**
     * Returns the full list of pokemons possible candy name.
     *
     * @return List of all candy pokemons that exist in Pokemon Go.
     */
    public List<PokemonBase> getCandyPokemons() {
        return Collections.unmodifiableList(candyPokemons);
    }

    /**
     * Returns a pokemon which corresponds to the number sent in.
     *
     * @param number the number which this application internally uses to identify pokemon
     * @return The pokemon if valid number, null if no pokemon found.
     */
    public PokemonBase get(int number) {
        if (number >= 0 && number < pokedex.size()) {
            return pokedex.get(number);
        }
        return null;
    }

    /**
     * Returns the normal form for a given number, working only for pokemons which don't have any forms.
     *
     * @param number the number which this application internally uses to identify pokemon
     * @return Pokemon instance
     */
    public Pokemon getForm(int number) {
        return getForm(number, "");
    }

    /**
     * Returns a specific pokemon for a given number and form name.
     *
     * @param number the number which this application internally uses to identify pokemon
     * @param formName the form name of the pokemon
     * @return Pokemon instance
     */
    public Pokemon getForm(int number, String formName) {
        return get(number).getForm(formName);
    }

    public static String[] getPokemonNamesArray(Resources res) {
        if (res.getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            // If flag ON, force to use English strings as pokemon name for OCR.
            Configuration conf = res.getConfiguration();
            Locale originalLocale; // Save original locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                originalLocale = conf.getLocales().get(0);
            } else {
                originalLocale = conf.locale;
            }
            conf.setLocale(Locale.ENGLISH);
            res.updateConfiguration(conf, null);
            String[] rtn = res.getStringArray(R.array.pokemon);
            conf.setLocale(originalLocale); // Restore to original locale
            res.updateConfiguration(conf, null);
            return rtn;
        }
        return res.getStringArray(R.array.pokemon);
    }

    private static String[] getPokemonDisplayNamesArray(GoIVSettings settings, Resources res) {
        if (settings.isShowTranslatedPokemonName()) {
            // If pref ON, use translated strings as pokemon name.
            return res.getStringArray(R.array.pokemon);
        }
        // Otherwise, use default locale's pokemon name.
        return getPokemonNamesArray(res);
    }

    /**
     * Return the full pokemon display names list, including forms.
     *
     * @return the full pokemon display names including forms as string array.
     */
    public String[] getPokemonNamesWithFormArray() {
        return pokeNamesWithForm;
    }

    /**
     * Fills the list "pokemon" with the information of all pokemon by reading the
     * arrays in integers.xml and the names from the strings.xml resources.
     */
    private void populatePokemon(@NonNull GoIVSettings settings, @NonNull Resources res) {
        final String[] names = getPokemonNamesArray(res);
        final String[] displayNames = getPokemonDisplayNamesArray(settings, res);
        final int[] attack = res.getIntArray(R.array.attack);
        final int[] defense = res.getIntArray(R.array.defense);
        final int[] stamina = res.getIntArray(R.array.stamina);
        final int[] devolution = res.getIntArray(R.array.devolutionNumber);
        final int[] evolutionCandyCost = res.getIntArray(R.array.evolutionCandyCost);
        final int[] candyNamesArray = res.getIntArray(R.array.candyNames);
        final int[] formsCountIndex = res.getIntArray(R.array.formsCountIndex);

        int pokeListSize = names.length;
        ArrayList<Pokemon> formVariantPokemons = new ArrayList<>();

        for (int i = 0; i < pokeListSize; i++) {
            PokemonBase p = new PokemonBase(names[i], displayNames[i], i, devolution[i],
                                            candyNamesArray[i], evolutionCandyCost[i]);
            pokedex.add(p);
        }

        for (int i = 0; i < pokeListSize; i++) {
            if (devolution[i] != -1) {
                PokemonBase devo = pokedex.get(devolution[i]);
                devo.evolutions.add(pokedex.get(i));
            } else {
                int candyNameIndex = candyNamesArray[i];
                if (candyNameIndex != -1) {
                    candyPokemons.add(pokedex.get(candyNameIndex));
                }
            }

            PokemonBase base = pokedex.get(i);

            //Check for different pokemon forms, such as alolan forms, and add them to the formsCount.
            if (formsCountIndex[i] != -1) {
                int[] formsCount = res.getIntArray(R.array.formsCount);
                int formsStartIndex = 0;

                for (int j = 0; j < formsCountIndex[i]; j++) {
                    formsStartIndex += formsCount[j];
                }

                for (int j = 0; j < formsCount[formsCountIndex[i]]; j++) {
                    Pokemon formPokemon = new Pokemon(base,
                            res.getStringArray(R.array.formNames)[formsStartIndex + j],
                            res.getIntArray(R.array.formAttack)[formsStartIndex + j],
                            res.getIntArray(R.array.formDefense)[formsStartIndex + j],
                            res.getIntArray(R.array.formStamina)[formsStartIndex + j]);
                    base.forms.add(formPokemon);
                    formVariantPokemons.add(formPokemon);
                }
            }
            else
            {
                Pokemon normal = new Pokemon(base, "", attack[i], defense[i], stamina[i]);
                base.forms.add(normal);
            }
        }

        this.formVariantPokemons = Collections.unmodifiableList(formVariantPokemons);
    }

    /**
     * Gets the needed required candy and stardust to hit max level (relative to trainer level).
     *
     * @param goalLevel             The level to reach
     * @param estimatedPokemonLevel The estimated level of the pokemon
     * @param isLucky               Whether the pokemon is lucky, therefore costs one half normal dust
     * @return The text that shows the amount of candy and stardust needed.
     */
    public UpgradeCost getUpgradeCost(double goalLevel, double estimatedPokemonLevel, boolean isLucky) {
        int neededCandy = 0;
        int neededCandyXl = 0;
        int neededStarDust = 0;

        int currentLevelIdx = Data.levelToLevelIdx(estimatedPokemonLevel);
        int goalLevelIdx = Data.levelToLevelIdx(goalLevel);

        while (currentLevelIdx < goalLevelIdx) {
            UpgradeCost costs = Data.costForIndex(currentLevelIdx);
            neededCandy += costs.candy;
            neededCandyXl += costs.candyXl;
            neededStarDust += costs.dust;

            currentLevelIdx++;
        }

        if (isLucky) {
            neededStarDust /= 2;
        }

        return new UpgradeCost(neededStarDust, neededCandy, neededCandyXl);
    }


    /**
     * Calculates all the IV information that can be gained from the pokemon level, hp and cp
     * and fills the information in an ScanResult.
     *
     * @param scanResult Pokefly scan results
     */
    public void getIVPossibilities(ScanResult scanResult) {
        scanResult.clearIVCombinations();

        if (scanResult.levelRange.min == scanResult.levelRange.max) {
            getSingleLevelIVPossibility(scanResult, scanResult.levelRange.min);
        }

        for (double i = scanResult.levelRange.min; i <= scanResult.levelRange.max; i += 0.5) {
            getSingleLevelIVPossibility(scanResult, i);
        }
    }

    /**
     * Calculates all the IV information that can be gained from the pokemon level, hp and cp
     * and fills the information in an ScanResult.
     *
     * @param scanResult Pokefly scan results
     *                   many possibilities.
     */
    private void getSingleLevelIVPossibility(ScanResult scanResult, double level) {
        int baseAttack = scanResult.pokemon.baseAttack;
        int baseDefense = scanResult.pokemon.baseDefense;
        int baseStamina = scanResult.pokemon.baseStamina;

        double lvlScalar = Data.getLevelCpM(level);
        double lvlScalarPow2 = Math.pow(lvlScalar, 2) * 0.1; // instead of computing again in every loop
        //IV vars for lower and upper end cp ranges

        for (int staminaIV = 0; staminaIV < 16; staminaIV++) {
            int hp = (int) Math.max(Math.floor((baseStamina + staminaIV) * lvlScalar), 10);
            if (hp == scanResult.hp) {
                double lvlScalarStamina = Math.sqrt(baseStamina + staminaIV) * lvlScalarPow2;
                for (int defenseIV = 0; defenseIV < 16; defenseIV++) {
                    for (int attackIV = 0; attackIV < 16; attackIV++) {
                        int cp = Math.max(10, (int) Math.floor((baseAttack + attackIV) * Math.sqrt(baseDefense
                                + defenseIV) * lvlScalarStamina));
                        if (cp == scanResult.cp) {
                            scanResult.addIVCombination(attackIV, defenseIV, staminaIV);
                        }
                    }
                }
            } else if (hp > scanResult.hp) {
                break;
            }
        }
    }


    /**
     * getCpAtRangeLeve
     * Used to calculate CP ranges for a species at a specific level based on the lowest and highest
     * IV combination.
     * <p/>
     * Returns a string on the form of "\n CP at lvl X: A - B" where x is the pokemon level, A is minCP and B is maxCP
     *
     * @param pokemon the index of the pokemon species within the pokemon list (sorted)
     * @param low     combination of lowest IVs
     * @param high    combination of highest IVs
     * @param level   pokemon level for CP calculation
     * @return CPrange containing the CP range including the specified level.
     */
    public CPRange getCpRangeAtLevel(Pokemon pokemon, IVCombination low, IVCombination high, double level) {
        if (low == null || high == null || level < 0 || pokemon == null) {
            return new CPRange(0, 0);
        }
        int baseAttack = pokemon.baseAttack;
        int baseDefense = pokemon.baseDefense;
        int baseStamina = pokemon.baseStamina;
        double lvlScalar = Data.getLevelCpM(level);
        int cpMin = (int) Math.floor(
                (baseAttack + low.att) * Math.sqrt(baseDefense + low.def) * Math.sqrt(baseStamina + low.sta)
                        * Math.pow(lvlScalar, 2) * 0.1);
        int cpMax = (int) Math.floor((baseAttack + high.att) * Math.sqrt(baseDefense + high.def)
                * Math.sqrt(baseStamina + high.sta) * Math.pow(lvlScalar, 2) * 0.1);
        if (cpMin > cpMax) {
            int tmp = cpMax;
            cpMax = cpMin;
            cpMin = tmp;
        }
        return new CPRange(cpMin, cpMax);
    }

    private Pokemon getDevolution(Pokemon poke) {
        if (poke.base.devoNumber >= 0) {
            PokemonBase devolvedBase = get(poke.base.devoNumber);
            return devolvedBase.getForm(poke);
        } else {
            return null;
        }
    }

    /**
     * Get the combined cost for evolving all steps between two pokemon, for example the cost from caterpie ->
     * metapod is 12,
     * caterpie -> butterfly is 12+50 = 62.
     *
     * @param start which pokemon to start from
     * @param end   the end evolution
     * @return the combined candy cost for all required evolutions
     */
    public int getCandyCostForEvolution(Pokemon start, Pokemon end) {
        int cost = 0;
        while (start != end) { //move backwards from end until you've reached start
            end = getDevolution(end);
            // Gone through all devolutions from the end but never passed by start -> start and end are not related
            if (end == null)
            {
                return 0;
            }
            cost += end.candyEvolutionCost;
        }
        return cost;
    }

    /**
     * Check if two pokemon are in the same complete evolution chain. Jolteon and vaporeon would return true
     *
     * @param p1 first pokemon
     * @param p2 second pokemon
     * @return true if both pokemon are in the same pokemon evolution tree
     */
    private boolean isInSameEvolutionChain(Pokemon p1, Pokemon p2) {
        ArrayList<PokemonBase> evolutionLine = getEvolutionLine(p1.base);
        for (PokemonBase base : evolutionLine) {
            if (base.number == p2.base.number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the lowest evolution in the chain of a pokemon.
     *
     * @param poke a pokemon, example charizard
     * @return a pokemon, in the example would return charmander
     */
    private Pokemon getLowestEvolution(Pokemon poke) {
        if (poke.devoNumber < 0) {
            return poke; //already lowest evolution
        }

        Pokemon current;
        do {
            current = poke;
            poke = getDevolution(current);
        }
        while (poke != null);
        return current;
    }

    /**
     * Get the lowest evolution in the chain of a pokemon.
     *
     * @param base a pokemon, example charizard
     * @return a pokemon, in the example would return charmander
     */
    private PokemonBase getLowestEvolution(PokemonBase base) {
        while (base.devoNumber >= 0) {
            base = get(base.devoNumber);
        }
        return base;
    }

    /**
     * Returns the pokemon whose name matches the candy type for the given pokemon
     *      e.g. getCandyPokemon(Machamp) -> Machop
     * @param pokemon the pokemon whose candy form is wanted
     * @return the pokemon that matches the candy type for the given pokemon
     */
    public Pokemon getCandyPokemon(Pokemon pokemon) {
        return getForm(pokemon.candyNameNumber);
    }

    /**
     * Returns all forms of all evolutions belonging to the pokemon.
     *
     * @param pokemon the pokemon to check the evolution line of
     * @return a list with pokemon, devolutions and evolutions and forms.
     */
    public ArrayList<Pokemon> getEvolutionForms(Pokemon pokemon) {
        ArrayList<Pokemon> list = new ArrayList<>();

        for (PokemonBase base : getEvolutionLine(pokemon.base)) {
            list.addAll(base.forms);
        }

        return list;
    }

    /**
     * Returns the evolution line of a pokemon.
     *
     * @param poke the pokemon to check the evolution line of
     * @return a list with pokemon, input pokemon plus its (d)evolutions
     */
    public ArrayList<Pokemon> getEvolutionLine(Pokemon poke) {
        ArrayList<Pokemon> list = new ArrayList<>();

        for (PokemonBase base : getEvolutionLine(poke.base)) {
            Pokemon form = base.getForm(poke);
            if (form != null) {
                list.add(form);
            }
        }

        return list;
    }

    /**
     * Returns the evolution line of a pokemon.
     *
     * @param base the pokemon to check the evolution line of
     * @return a list with pokemon, input pokemon plus its evolutions
     */
    public ArrayList<PokemonBase> getEvolutionLine(PokemonBase base) {
        base = getLowestEvolution(base);

        ArrayList<PokemonBase> list = new ArrayList<>();
        list.add(base);
        for (PokemonBase evolution2nd : base.evolutions) {
            list.add(evolution2nd);
            for (PokemonBase evolution3rd : evolution2nd.evolutions) {
                list.add(evolution3rd);
            }
        }

        return list;
    }

    /**
     * Get how much hp a pokemon will have at a certain level, including the stamina IV taken from the scan results.
     * If the prediction is not exact because of big possible variation in stamina IV, the average will be returnred.
     *
     * @param scanResult      Scan results which includes stamina ivs
     * @param selectedLevel   which level to get the hp for
     * @param selectedPokemon Which pokemon to get Hp for
     * @return An integer representing how much hp selectedpokemon with ivscanresult stamina ivs has at selectedlevel
     */
    public int getHPAtLevel(ScanResult scanResult, double selectedLevel, Pokemon selectedPokemon) {
        double lvlScalar = Data.getLevelCpM(selectedLevel);
        int highHp = (int) Math.max(
                Math.floor((selectedPokemon.baseStamina + scanResult.getIVStaminaHigh()) * lvlScalar), 10);
        int lowHp = (int) Math.max(
                Math.floor((selectedPokemon.baseStamina + scanResult.getIVStaminaLow()) * lvlScalar), 10);
        return Math.round((highHp + lowHp) / 2f);
    }
}
