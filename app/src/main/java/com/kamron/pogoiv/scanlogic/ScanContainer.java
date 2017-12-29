package com.kamron.pogoiv.scanlogic;

import com.kamron.pogoiv.utils.LevelRange;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-08-19.
 * <p/>
 * A class which keeps the 2 most recent IV scans in memory
 */
public class ScanContainer {
    public static final ScanContainer scanContainer = new ScanContainer();
    public IVScanResult prevScan = null;
    public IVScanResult currScan = null;

    /**
     * Pushes the 3 scan ago out of memory, and remembers the two latest scans.
     */
    private void addNewScan(IVScanResult res) {
        prevScan = currScan;
        currScan = res;
    }

    /**
     * Create a new IVScanResult and updates the scanContainer singleton.
     */
    public static IVScanResult createIVScanResult(Pokemon pokemon, LevelRange estimatedPokemonLevel, int pokemonCP,
                                                  Pokemon.Gender pokemonGender) {
        IVScanResult res = new IVScanResult(pokemon, estimatedPokemonLevel, pokemonCP, pokemonGender);
        scanContainer.addNewScan(res);
        return res;
    }


    /**
     * Compares two pokemon scan results, and returns a list of which ivs the scans have in commomn
     * Useful when you power up a pokemon, and wanna see which combinations you can trash
     *
     * @param poke1 the first pokemon scan
     * @param poke2 the second pokemon scan
     * @return ArrayList of ivcombination that are present in both iv scans.
     */
    private static ArrayList<IVCombination> findIVIntersection(IVScanResult poke1, IVScanResult poke2) {
        ArrayList<IVCombination> intersection = new ArrayList<>();


        if (poke1 != null && poke2 != null) {
            ArrayList<IVCombination> p1IVs = poke1.iVCombinations;
            ArrayList<IVCombination> p2IVs = poke2.iVCombinations;
            for (IVCombination p1IV : p1IVs) {
                for (IVCombination p2IV : p2IVs) {
                    if (p1IV.equals(p2IV)) {
                        intersection.add(p1IV);
                    }
                }
            }
        }

        return intersection;
    }

    /**
     * Checks if the last scanned pokemon can be the same pokemon as the previous one, and if there's any
     * evolution/level-up and it is hence worth enabling result refinement.
     *
     * @return true if the pokemon can be same
     */
    public boolean isScanRefinable() {
        if (currScan != null && prevScan != null) {
            Pokemon currPokemon = currScan.pokemon;
            Pokemon prevPokemon = prevScan.pokemon;

            /* Since pokemon can de-evolve, level down or change species, we must check that
             * both species and level are greater or equal, and at least one is strictly greater.
             */
            boolean higherLevel = currScan.estimatedPokemonLevel.min > prevScan.estimatedPokemonLevel.min;
            boolean sameOrHigherLevel = currScan.estimatedPokemonLevel.min >= prevScan.estimatedPokemonLevel.min;
            boolean evolved = currPokemon.isNextEvolutionOf(prevPokemon);
            boolean sameOrEvolved = currPokemon.number == prevPokemon.number || evolved;

            return (higherLevel || evolved) && sameOrHigherLevel && sameOrEvolved;
        }

        return false;
    }

    /**
     * Compares the latest two pokemon scan results, and returns a list of which ivs the scans have in commomn
     * Useful when you power up a pokemon, and wanna see which combinations you can trash
     *
     * @return ArrayList of ivcombination that are present in both iv scans.
     */
    public ArrayList<IVCombination> getLatestIVIntersection() {
        return findIVIntersection(currScan, prevScan);

    }

    /**
     * Returns a string which is either the name of the previously scanned pokemon, or "".
     */
    public String getPrevScanName() {
        if (prevScan != null) {
            return prevScan.pokemon.toString();
        } else {
            return "";
        }

    }
}
