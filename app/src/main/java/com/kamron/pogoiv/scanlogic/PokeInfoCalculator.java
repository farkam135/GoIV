package com.kamron.pogoiv.scanlogic;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Johan Swanberg on 2016-08-18.
 * A class which interprets pokemon information
 */
public class PokeInfoCalculator {
    private static PokeInfoCalculator instance;

    private ArrayList<Pokemon> pokedex = new ArrayList<>();
    private String[] typeNamesArray;

    /**
     * Pokemons that aren't evolutions of any other one.
     */
    private ArrayList<Pokemon> basePokemons = new ArrayList<>();

    /**
     * Pokemons who's name appears as a type of candy.
     * For most, this is the basePokemon (ie: Pidgey candies)
     * For some, this is an original Gen1 Pokemon (ie: Magmar candies, instead of Magby candies)
     */
    private ArrayList<Pokemon> candyPokemons = new ArrayList<>();

    private HashMap<String, Pokemon> pokemap = new HashMap<>();


    public static synchronized @NonNull PokeInfoCalculator getInstance(@NonNull Context context) {
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
     * @param settings  Settings instance
     * @param res       System resources
     */
    private PokeInfoCalculator(@NonNull GoIVSettings settings, @NonNull Resources res) {
        populatePokemon(settings, res);
        this.typeNamesArray = res.getStringArray(R.array.typeName);
    }

    public List<Pokemon> getPokedex() {
        return Collections.unmodifiableList(pokedex);
    }

    public List<Pokemon> getBasePokemons() {
        return Collections.unmodifiableList(basePokemons);
    }

    /**
     * Returns the full list of possible candy names.
     *
     * @return List of all candy names that exist in Pokemon Go
     */
    public List<Pokemon> getCandyPokemons() {
        return Collections.unmodifiableList(candyPokemons);
    }

    /**
     * Returns a pokemon which corresponds to the number sent in.
     *
     * @param number the number which this application internally uses to identify pokemon
     * @return The pokemon if valid number, null if no pokemon found.
     */
    public Pokemon get(int number) {
        if (number >= 0 && number < pokedex.size()) {
            return pokedex.get(number);
        }
        return null;
    }

    public Pokemon get(String name) {
        return pokemap.get(name.toLowerCase());
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
        for (int i = 0; i < pokeListSize; i++) {
            Pokemon p = new Pokemon(names[i], displayNames[i], i, attack[i], defense[i], stamina[i], devolution[i],
                    evolutionCandyCost[i]);
            pokedex.add(p);
            pokemap.put(names[i].toLowerCase(), p);
            if (!names[i].equals(displayNames[i])) {
                pokemap.put(displayNames[i], p);
            }
        }

        for (int i = 0; i < pokeListSize; i++) {
            if (devolution[i] != -1) {
                Pokemon devo = pokedex.get(devolution[i]);
                devo.evolutions.add(pokedex.get(i));
            } else {
                candyPokemons.add(pokedex.get(candyNamesArray[i]));
                basePokemons.add(pokedex.get(i));
            }

            if (formsCountIndex[i] != -1) {
                int[] formsCount = res.getIntArray(R.array.formsCount);
                int formsStartIndex = 0;

                for (int j = 0; j < formsCountIndex[i]; j++) {
                    formsStartIndex += formsCount[j];
                }

                for (int j = 0; j < formsCount[formsCountIndex[i]]; j++) {
                    pokedex.get(i).forms.add(new Pokemon(
                        String.format("%s,%s",
                            pokedex.get(i).name, res.getStringArray(R.array.formNames)[formsStartIndex + j]),
                        String.format("%s,%s",
                            pokedex.get(i).toString(), res.getStringArray(R.array.formNames)[formsStartIndex + j]),
                        i,
                        res.getIntArray(R.array.formAttack)[formsStartIndex + j],
                        res.getIntArray(R.array.formDefense)[formsStartIndex + j],
                        res.getIntArray(R.array.formStamina)[formsStartIndex + j],
                        devolution[i],
                        evolutionCandyCost[i])
                    );
                }
            }
        }
    }

    /**
     * Gets the needed required candy and stardust to hit max level (relative to trainer level).
     *
     * @param goalLevel             The level to reach
     * @param estimatedPokemonLevel The estimated level of hte pokemon
     * @return The text that shows the amount of candy and stardust needed.
     */
    public UpgradeCost getUpgradeCost(double goalLevel, double estimatedPokemonLevel) {
        int neededCandy = 0;
        int neededStarDust = 0;
        while (estimatedPokemonLevel != goalLevel) {
            int rank = 5;
            if ((estimatedPokemonLevel % 10) >= 1 && (estimatedPokemonLevel % 10) <= 2.5) {
                rank = 1;
            } else if ((estimatedPokemonLevel % 10) > 2.5 && (estimatedPokemonLevel % 10) <= 4.5) {
                rank = 2;
            } else if ((estimatedPokemonLevel % 10) > 4.5 && (estimatedPokemonLevel % 10) <= 6.5) {
                rank = 3;
            } else if ((estimatedPokemonLevel % 10) > 6.5 && (estimatedPokemonLevel % 10) <= 8.5) {
                rank = 4;
            }

            if (estimatedPokemonLevel <= 10.5) {
                neededCandy++;
                neededStarDust += rank * 200;
            } else if (estimatedPokemonLevel > 10.5 && estimatedPokemonLevel <= 20.5) {
                neededCandy += 2;
                neededStarDust += 1000 + (rank * 300);
            } else if (estimatedPokemonLevel > 20.5 && estimatedPokemonLevel <= 25.5) {
                neededCandy += 3;
                neededStarDust += 2500 + (rank * 500);
            } else if (estimatedPokemonLevel > 25.5 && estimatedPokemonLevel <= 30.5) {
                neededCandy += 4;
                neededStarDust += 2500 + (rank * 500);
            } else if (estimatedPokemonLevel > 30.5 && estimatedPokemonLevel <= 32.5) {
                neededCandy += 6;
                neededStarDust += 5000 + (rank * 1000);
            } else if (estimatedPokemonLevel > 32.5 && estimatedPokemonLevel <= 34.5) {
                neededCandy += 8;
                neededStarDust += 5000 + (rank * 1000);
            } else if (estimatedPokemonLevel > 34.5 && estimatedPokemonLevel <= 36.5) {
                neededCandy += 10;
                neededStarDust += 5000 + (rank * 1000);
            } else if (estimatedPokemonLevel > 36.5 && estimatedPokemonLevel <= 38.5) {
                neededCandy += 12;
                neededStarDust += 5000 + (rank * 1000);
            } else if (estimatedPokemonLevel > 38.5) {
                neededCandy += 15;
                neededStarDust += 5000 + (rank * 1000);
            }

            estimatedPokemonLevel += 0.5;
        }
        return new UpgradeCost(neededStarDust, neededCandy);
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
     * many possibilities.
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
        Pokemon devolution = get(end.devoNumber);
        Pokemon dedevolution = null;
        if (devolution != null) { //devolution must exist for there to be a devolution of the devolution
            dedevolution = get(devolution.devoNumber);
        }

        boolean isEndReallyAfterStart = (devolution == start)
                || dedevolution == start; //end must be devolution or devolution of devolution of start
        int cost = 0;
        if (isInSameEvolutionChain(start, end) && isEndReallyAfterStart) {
            while (start != end) { //move backwards from end until you've reached start
                Pokemon beforeEnd = get(end.devoNumber);
                cost += beforeEnd.candyEvolutionCost;
                end = beforeEnd;
            }
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
        ArrayList<Pokemon> evolutionLine = getEvolutionLine(p1);
        for (Pokemon poke : evolutionLine) {
            if (poke.number == p2.number) {
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

        Pokemon devoPoke = get(poke.devoNumber);
        while (devoPoke.devoNumber >= 0) { //while devol
            devoPoke = get(devoPoke.devoNumber);
        }
        return devoPoke;
    }

    /**
     * Get all the pokemon forms for a pokemon.
     *
     * @param poke a pokemon, example Exeggutor
     * @return all the pokemon forms, in the example would return Exeggutor normal and Exeggutor Alola
     */
    private ArrayList<Pokemon> getForms(Pokemon poke) {
        ArrayList<Pokemon> list = new ArrayList<>();
        Pokemon normalFormPokemon = pokedex.get(poke.number);
        list.add(normalFormPokemon);

        if (normalFormPokemon.forms.isEmpty()) {
            return list; // normal form only
        }

        list.addAll(normalFormPokemon.forms);
        return list;
    }

    /**
     * Returns the evolution line of a pokemon.
     *
     * @param poke the pokemon to check the evolution line of
     * @return a list with pokemon, input pokemon plus its evolutions
     */
    public ArrayList<Pokemon> getEvolutionLine(Pokemon poke) {
        poke = getLowestEvolution(poke);

        ArrayList<Pokemon> list = new ArrayList<>();
        list.addAll(getForms(poke));
        for (Pokemon evolution2nd : poke.evolutions) {
            list.addAll(getForms(evolution2nd));
            for (Pokemon evolution3rd : evolution2nd.evolutions) {
                list.addAll(getForms(evolution3rd));
            }
        }

        return list;
    }

    /**
     * Get how much hp a pokemon will have at a certain level, including the stamina IV taken from the scan results.
     * If the prediction is not exact because of big possible variation in stamina IV, the average will be returnred.
     *
     * @param scanResult    Scan results which includes stamina ivs
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

    public String getTypeName(int typeNameNum) {
        return typeNamesArray[typeNameNum];
    }
}
