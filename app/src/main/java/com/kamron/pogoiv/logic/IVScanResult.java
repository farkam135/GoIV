package com.kamron.pogoiv.logic;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * A class which represents all possible iv combinations for a pokemon.
 * An object contains:
 * count - the amount of IV combinations the pokemon has
 * highpercent: Best case iv%
 * scannedCP: the cp scanned from the image
 * getAveragePercent: returns the average IV% of all alternativs
 * lowPercent: worst case IV%
 * low attack,defence,stamina - the value for the IV stat where the lowest % was found
 * high attack,defence,stamina - the value for hte IV stat where the highest % was found
 * <p>
 * The ivscanresult object has evolved (bloated) to incluide several other things not needed to calculate the ivs,
 * which are used by other methods, such as the scanned hp and an identifier for the pokemon.
 * <p/>
 * Created by Johan on 2016-08-18.
 */
public class IVScanResult {
    private int highPercent = 0;
    private int lowPercent = 100;
    public int lowAttack = 15;
    public int lowDefense = 15;
    public int lowStamina = 15;
    public int highAttack = 0;
    public int highDefense = 0;
    public int highStamina = 0;
    public final int scannedCP;
    public ArrayList<IVCombination> iVCombinations = new ArrayList<>();
    public Pokemon pokemon = null;
    public final double estimatedPokemonLevel;
    public int scannedHP = 0;

    /**
     * Creates a holder object for IV scan results.
     *
     * @param pokemon               which pokemon it is
     * @param pokemonCP             pokemon CP
     * @param estimatedPokemonLevel the estimated pokemon level (should be very low)
     */
    public IVScanResult(Pokemon pokemon, double estimatedPokemonLevel, int pokemonCP) {
        this.pokemon = pokemon;
        this.estimatedPokemonLevel = estimatedPokemonLevel;
        this.scannedCP = pokemonCP;
    }

    public int getCount() {
        return iVCombinations.size();
    }

    /**
     * Calculates and returns the average % of the possible IVs.
     */
    public int getAveragePercent() {
        int sum = 0;
        for (IVCombination ivc : iVCombinations) {
            sum += ivc.att + ivc.def + ivc.sta;
        }
        return Math.round(sum * 100f / (45f * getCount()));
    }

    public void sortCombinations() {
        Collections.sort(iVCombinations, new Comparator<IVCombination>() {
            @Override public int compare(IVCombination o1, IVCombination o2) {
                int comparePercent = o1.percentPerfect - o2.percentPerfect;
                if (comparePercent != 0) {
                    return comparePercent;
                }
                int compareAtt = o1.att - o2.att;
                if (compareAtt != 0) {
                    return compareAtt;
                }
                int compareDef = o1.def - o2.def;
                if (compareDef != 0) {
                    return compareDef;
                }
                int compareSta = o1.sta - o2.sta;
                return compareSta;
            }
        });
    }

    /**
     * Adds an IV possibility to the scan results.
     *
     * @param attackIV  the attack iv
     * @param defenseIV the defense iv
     * @param staminaIV the stamina iv
     */
    public void addIVCombination(int attackIV, int defenseIV, int staminaIV) {
        int sumIV = attackIV + defenseIV + staminaIV;
        int percentPerfect = Math.round(sumIV / 45f * 100);

        if ((percentPerfect < lowPercent)
                || (percentPerfect == lowPercent)
                && (attackIV < lowAttack)) { // check for same percentage but lower atk
            lowPercent = percentPerfect;
            //save worst combination for lower end cp range
            lowAttack = attackIV;
            lowDefense = defenseIV;
            lowStamina = staminaIV;
        }
        if ((percentPerfect > highPercent)
                || (percentPerfect == highPercent)
                && (attackIV > highAttack)) { // check for same percentage but higher atk
            highPercent = percentPerfect;
            //save best combination for upper end cp range
            highAttack = attackIV;
            highDefense = defenseIV;
            highStamina = staminaIV;
        }

        iVCombinations.add(new IVCombination(attackIV, defenseIV, staminaIV));
    }


    /**
     * Get the IV combination which has the highest sum of att+def+sta, or tied to equal.
     */
    public @Nullable IVCombination getHighestIVCombination() {
        if (iVCombinations.size() == 0) {
            return null;
        }
        return Collections.max(iVCombinations, IVCombination.totalComparator);
    }

    /**
     * Get the IV combination which has the lowest sum of att+def+sta, or tied to equal.
     */
    public @Nullable IVCombination getLowestIVCombination() {
        if (iVCombinations.size() == 0) {
            return null;
        }
        return Collections.min(iVCombinations, IVCombination.totalComparator);
    }

    /**
     * Get IVCombination of highest IVs. This is not the combination with the highest total, and is probably not a
     * combination of possible IVs; see getHighestIVCombination() for that.
     */
    public IVCombination getCombinationHighIVs() {
        return new IVCombination(highAttack, highDefense, highStamina);
    }

    /**
     * Get IVCombination of lowest IVs. This is not the combination with the lowest total, and is probably not a
     * combination of possible IVs; see getLowestIVCombination() for that.
     */
    public IVCombination getCombinationLowIVs() {
        return new IVCombination(lowAttack, lowDefense, lowStamina);
    }


    /**
     * Readjusts the low and high instance variables by looping through all the combinations again and re-checking them.
     */
    private void updateHighAndLowValues() {
        lowAttack = 15;
        lowDefense = 15;
        lowStamina = 15;
        highAttack = 0;
        highDefense = 0;
        highStamina = 0;
        highPercent = 0;
        lowPercent = 100;

        for (IVCombination ivc : iVCombinations) {
            int sumIV = ivc.att + ivc.def + ivc.sta;
            int percentPerfect = Math.round(sumIV / 45f * 100);

            if (ivc.att < lowAttack) {
                lowAttack = ivc.att;
            }
            if (ivc.def < lowDefense) {
                lowDefense = ivc.def;
            }
            if (ivc.sta < lowStamina) {
                lowStamina = ivc.sta;
            }

            if (ivc.att > highAttack) {
                highAttack = ivc.att;
            }
            if (ivc.def > highDefense) {
                highDefense = ivc.def;
            }
            if (ivc.sta > highStamina) {
                highStamina = ivc.sta;
            }
            if (percentPerfect > highPercent) {
                highPercent = percentPerfect;
            }
            if (percentPerfect < lowPercent) {
                lowPercent = percentPerfect;
            }
        }

    }

    /**
     * Removes all possible IV combinations where the boolean set to true stat isnt the highest.
     * Several stats can be highest if they're equal.
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
        updateHighAndLowValues();
    }

    /**
     * Removes any iv combination that is outside the scope of the input percentage range.
     * 1: 81-100%
     * 2: 66-80%
     * 3: 51-65%
     * 4: 0-50%
     *
     * @param selectedItemPosition a number between 1 to 4 as detailed above
     */
    public void refineByAppraisalPercentageRange(int selectedItemPosition) {
        int lowest;
        int highest;

        switch (selectedItemPosition) {
            case 1:
                lowest = 81;
                highest = 100;
                break;
            case 2:
                lowest = 66;
                highest = 80;
                break;
            case 3:
                lowest = 51;
                highest = 65;
                break;
            case 4:
                lowest = 0;
                highest = 50;
                break;
            default:
                lowest = 0;
                highest = 100;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();

        for (IVCombination comb : iVCombinations) {

            if (comb.percentPerfect >= lowest && comb.percentPerfect <= highest) {
                refinedList.add(comb);
            }
        }

        iVCombinations = refinedList;
        updateHighAndLowValues();
    }

    /**
     * Removes any iv combination where the highest IV is outside the scope of he input range.
     * Input:
     * 1: 15
     * 2: 13-14
     * 3: 8-12
     * 4: 0-7
     *
     * @param selectedItemPosition a number between 1 to 4 as detailed above
     */
    public void refineByAppraisalIVRange(int selectedItemPosition) {
        int lowest;
        int highest;

        switch (selectedItemPosition) {
            case 1:
                lowest = 15;
                highest = 15;
                break;
            case 2:
                lowest = 13;
                highest = 14;
                break;
            case 3:
                lowest = 8;
                highest = 12;
                break;
            case 4:
                lowest = 0;
                highest = 7;
                break;
            default:
                lowest = 0;
                highest = 15;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();

        for (IVCombination comb : iVCombinations) {

            if (comb.getHighestStat() >= lowest && comb.getHighestStat() <= highest) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;

        updateHighAndLowValues();
    }
}
