package com.kamron.pogoiv;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Johan on 2016-08-18.
 * <p/>
 * A class which represents all possible iv combinations for a pokemon
 */
public class IVScanResult {
    public int highPercent = 0;
    public int lowPercent = 100;
    public int lowAttack = 15;
    public int lowDefense = 15;
    public int lowStamina = 15;
    public int highAttack = 0;
    public int highDefense = 0;
    public int highStamina = 0;
    public ArrayList<IVCombination> iVCombinations = new ArrayList<>();
    public static ScanContainer scanContainer = new ScanContainer();
    public Pokemon pokemon = null;
    public double estimatedPokemonLevel;

    /**
     * creates a holder object for iv scan results
     * The object contains:
     * count - the amount of IV combinations the pokemon has
     * highpercent: Best case iv%
     * getAveragePercent: returns the average IV% of all alternativs
     * lowPercent: worst case IV%
     * low attack,defence,stamina - the value for the IV stat where the lowest % was found
     * high attack,defence,stamina - the value for hte IV stat where the highest % was found
     */
    public IVScanResult(Pokemon pokemon, double estimatedPokemonLevel) {
        scanContainer.addNewScan(this);
        this.pokemon = pokemon;
        this.estimatedPokemonLevel = estimatedPokemonLevel;
    }

    public int getCount() {
        return iVCombinations.size();
    }

    /**
     * Calculates and returns the average % of the possible IVs
     *
     * @return
     */
    public int getAveragePercent() {
        int averageSum = 0;
        for (IVCombination ivc : iVCombinations) {
            averageSum += ivc.att + ivc.def + ivc.sta;
        }
        return (int) (((averageSum * 100 / (45.0 * getCount()))) + 0.5); //
    }

    /**
     * adds an IV possibility to the scan results
     *
     * @param attackIV  the attack iv
     * @param defenseIV the defense iv
     * @param staminaIV the stamina iv
     */
    public void addIVCombination(int attackIV, int defenseIV, int staminaIV) {
        int sumIV = attackIV + defenseIV + staminaIV;
        int percentPerfect = (int) Math.round(((sumIV) / 45.0) * 100);

        if ((percentPerfect < lowPercent) || ((percentPerfect == lowPercent) && (attackIV < lowAttack))) { // check for same percentage but lower atk
            lowPercent = percentPerfect;
            //save worst combination for lower end cp range
            lowAttack = attackIV;
            lowDefense = defenseIV;
            lowStamina = staminaIV;
        }
        if ((percentPerfect > highPercent) || ((percentPerfect == highPercent) && (attackIV > highAttack))) { // check for same percentage but higher atk
            highPercent = percentPerfect;
            //save best combination for upper end cp range
            highAttack = attackIV;
            highDefense = defenseIV;
            highStamina = staminaIV;
        }


        iVCombinations.add(new IVCombination(attackIV, defenseIV, staminaIV));
    }


    /**
     * Compares the latest two pokemon scan results, and returns a list of which ivs the scans have in commomn
     * Useful when you power up a pokemon, and wanna see which combinations you can trash
     *
     * @return ArrayList of ivcombination that are present in both iv scans.
     */
    public ArrayList<IVCombination> getLatestIVIntersection() {
        return findIVIntersection(scanContainer.oneScanAgo, scanContainer.twoScanAgo);
    }

    /**
     * Compares two pokemon scan results, and returns a list of which ivs the scans have in commomn
     * Useful when you power up a pokemon, and wanna see which combinations you can trash
     *
     * @param poke1 the first pokemon scan
     * @param poke2 the second pokemon scan
     * @return ArrayList of ivcombination that are present in both iv scans.
     */
    public ArrayList<IVCombination> findIVIntersection(IVScanResult poke1, IVScanResult poke2) {
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
     * Checks if the previous scanned pokemon can be the same pokemon as the one scanned 2 scans ago
     * checks if newer scan has same or higher level, and same or better evolution. (because pokemon cant de-level or devolve)
     *
     * @return true if the pokemon can be same
     */
    public boolean are2LastScannedPokemonSame() {

        IVScanResult p1scan = scanContainer.oneScanAgo;
        IVScanResult p2scan = scanContainer.twoScanAgo;
        if (p1scan != null && p2scan != null) {
            Pokemon p1 = scanContainer.oneScanAgo.pokemon;
            Pokemon p2 = scanContainer.twoScanAgo.pokemon;

            if (p1scan.estimatedPokemonLevel >= p2scan.estimatedPokemonLevel) { //later scan must have higher or same level
                if (p1.number == p2.number || p1.isInNextEvolution(p2)) { // either same species, or 2 scans ago is an evolution of previous scan
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * @return get the IV combination which has the highest sum of att+def+sta, or tied to equal
     */
    public IVCombination getHighestIVCombination() {
        if (iVCombinations.size() == 0) return null;
        IVCombination max = iVCombinations.get(0);
        for (IVCombination ivCombination : iVCombinations) {
            if (ivCombination.getTotal() > max.getTotal()) {
                max = ivCombination;
            }
        }
        return max;
    }

    /**
     * @return get the IV combination which has the lowest sum of att+def+sta, or tied to equal
     */
    public IVCombination getLowestIVCombination() {
        if (iVCombinations.size() == 0) return null;
        IVCombination low = iVCombinations.get(0);
        for (IVCombination ivCombination : iVCombinations) {
            if (ivCombination.getTotal() < low.getTotal()) {
                low = ivCombination;
            }
        }
        return low;
    }

    /**
     * returns a string which is either the name of the previously scanned pokemon, or ""
     *
     * @return
     */
    public String getPrevScanName() {
        if (scanContainer.twoScanAgo != null) {
            return scanContainer.twoScanAgo.pokemon.name;
        } else {
            return "";
        }

    }

    /**
     * Removes all possible IV combinations where the boolean set to true stat isnt the highest
     * Several stats can be highest if they're equal
     *
     * @param attIsHighest
     * @param defIsHighest
     * @param staIsHighest
     */
    public void refineByHighest(boolean attIsHighest, boolean defIsHighest, boolean staIsHighest) {
        ArrayList<IVCombination> refinedList = new ArrayList<>();

        for (IVCombination comb : iVCombinations) {
            Boolean[] knownAttDefSta = {attIsHighest, defIsHighest, staIsHighest};
            if (Arrays.equals(comb.getHighestStatSignature(), knownAttDefSta)) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;
    }

}
