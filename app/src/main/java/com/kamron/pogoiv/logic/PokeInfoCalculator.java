package com.kamron.pogoiv.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johan Swanberg on 2016-08-18.
 * A class which interprets pokemon information
 */
public class PokeInfoCalculator {
    private static PokeInfoCalculator instance;

    private ArrayList<Pokemon> pokedex = null;
    private HashMap<String, Pokemon> pokemap = null;

    public static PokeInfoCalculator getInstance(String[] namesArray, int[] attackArray, int[] defenceArray,
                                                 int[] staminaArray, int[] devolutionArray,
                                                 int[] evolutionCandyCostArray) {
        if (instance == null) {
            instance = new PokeInfoCalculator(namesArray, attackArray, defenceArray, staminaArray, devolutionArray,
                    evolutionCandyCostArray);
        }
        return instance;
    }

    /**
     * Creates a pokemon info calculator with the pokemon as argument.
     *
     * @param namesArray      array of all pokemon names
     * @param attackArray     array of all pokemon base attack stat
     * @param defenceArray    array of all pokemon base def stat
     * @param staminaArray    array of all pokemon base stam stat
     * @param devolutionArray array of what the pokemon evolved from, -1 if no devolution
     */
    private PokeInfoCalculator(String[] namesArray, int[] attackArray, int[] defenceArray, int[] staminaArray,
                               int[] devolutionArray, int[] evolutionCandyCostArray) {
        populatePokemon(namesArray, attackArray, defenceArray, staminaArray, devolutionArray, evolutionCandyCostArray);
    }

    public List<Pokemon> getPokedex() {
        return Collections.unmodifiableList(this.pokedex);
    }

    /**
     * Returns a pokemon which corresponds to the number sent in.
     *
     * @param number the number which this application internally uses to identify pokkemon
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

    /**
     * Fills the list "pokemon" with the information of all pokemon by reading the
     * arrays in integers.xml and the names from the strings.xml resources.
     */
    private void populatePokemon(String[] names, int[] attack, int[] defense, int[] stamina, int[] devolution,
                                 int[] evolutionCandyCost) {
        pokedex = new ArrayList<>();
        pokemap = new HashMap<>();

        int pokeListSize = names.length;
        for (int i = 0; i <= pokeListSize - 1; i++) {
            Pokemon p = new Pokemon(names[i], i, attack[i], defense[i], stamina[i], devolution[i],
                    evolutionCandyCost[i]);
            pokedex.add(p);
            pokemap.put(names[i].toLowerCase(), p);
        }

        for (int i = 0; i <= pokeListSize - 1; i++) {
            if (devolution[i] != -1) {
                Pokemon devo = pokedex.get(devolution[i]);
                devo.evolutions.add(pokedex.get(i));
            }
        }
    }

    /**
     * getUpgradeCost
     * Gets the needed required candy and stardust to hit max level (relative to trainer level)
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
     * and fills the information in an IVScanResult, which is returned.
     *
     * @param estimatedPokemonLevel The estimated pokemon level
     * @param pokemonHP             THe pokemon hp
     * @param pokemonCP             The pokemonCP
     * @return An IVScanResult which contains the information calculated about the pokemon, or null if there are too
     * many possibilities.
     */
    public IVScanResult getIVPossibilities(Pokemon selectedPokemon, double estimatedPokemonLevel, int pokemonHP,
                                           int pokemonCP) {
        int baseAttack = selectedPokemon.baseAttack;
        int baseDefense = selectedPokemon.baseDefense;
        int baseStamina = selectedPokemon.baseStamina;

        double lvlScalar = Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)];
        double lvlScalarPow2 = Math.pow(lvlScalar, 2) * 0.1; // instead of computing again in every loop
        //IV vars for lower and upper end cp ranges


        IVScanResult returner;
        //It's safe to proceed if *one* is not 10, though it takes a bit longer.
        if (pokemonHP != 10 || pokemonCP != 10) {
            returner = ScanContainer.createIVScanResult(selectedPokemon, estimatedPokemonLevel, pokemonCP, false);
            for (int staminaIV = 0; staminaIV < 16; staminaIV++) {
                int hp = (int) Math.max(Math.floor((baseStamina + staminaIV) * lvlScalar), 10);
                if (hp == pokemonHP) {
                    double lvlScalarStamina = Math.sqrt(baseStamina + staminaIV) * lvlScalarPow2;
                    //Possible STA IV
                    //System.out.println("Checking sta: " + staminaIV + ", gives " + hp);
                    for (int defenseIV = 0; defenseIV < 16; defenseIV++) {
                        for (int attackIV = 0; attackIV < 16; attackIV++) {
                            int cp = (int) Math.floor(
                                    (baseAttack + attackIV) * Math.sqrt(baseDefense + defenseIV) * lvlScalarStamina);
                            if (cp == pokemonCP) {
                                returner.addIVCombination(attackIV, defenseIV, staminaIV);
                            }
                        }
                    }
                } else if (hp > pokemonHP) {
                    break;
                }
            }
        } else {
            returner = ScanContainer.createIVScanResult(selectedPokemon, estimatedPokemonLevel, pokemonCP, true);
        }
        return returner;
    }


    /**
     * getCpAtRangeLeve
     * Used to calculate CP ranges for a species at a specific level based on the lowest and highest
     * IV combination.
     * <p/>
     * Returns a string on the form of "\n CP at lvl X: A - B" where x is the pokemon level, A is minCP and B is maxCP
     *
     * @param pokemon     the index of the pokemon species within the pokemon list (sorted)
     * @param lowAttack   attack IV of the lowest combination
     * @param lowDefense  defense IV of the lowest combination
     * @param lowStamina  stamina IV of the lowest combination
     * @param highAttack  attack IV of the highest combination
     * @param highDefense defense IV of the highest combination
     * @param highStamina stamina IV of the highest combination
     * @param level       pokemon level for CP calculation
     * @return String containing the CP range including the specified level.
     */
    public CPRange getCpRangeAtLevel(Pokemon pokemon, int lowAttack, int lowDefense, int lowStamina, int highAttack,
                                     int highDefense, int highStamina, double level) {
        int baseAttack = pokemon.baseAttack;
        int baseDefense = pokemon.baseDefense;
        int baseStamina = pokemon.baseStamina;
        double lvlScalar = Data.CpM[(int) (level * 2 - 2)];
        int cpMin = (int) Math.floor(
                (baseAttack + lowAttack) * Math.sqrt(baseDefense + lowDefense) * Math.sqrt(baseStamina + lowStamina)
                        * Math.pow(lvlScalar, 2) * 0.1);
        int cpMax = (int) Math.floor((baseAttack + highAttack) * Math.sqrt(baseDefense + highDefense)
                * Math.sqrt(baseStamina + highStamina) * Math.pow(lvlScalar, 2) * 0.1);
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
     * Returns the evolution line of a pokemon.
     *
     * @param poke the pokemon to check the evolution line of
     * @return a list with pokemon, input pokemon plus its evolutions
     */
    public ArrayList<Pokemon> getEvolutionLine(Pokemon poke) {
        poke = getLowestEvolution(poke);

        ArrayList<Pokemon> list = new ArrayList<>();
        list.add(poke); //add self
        list.addAll(poke.evolutions); //add all immediate evolutions
        for (Pokemon evolution : poke.evolutions) {
            list.addAll(evolution.evolutions);
        }

        return list;
    }
}
