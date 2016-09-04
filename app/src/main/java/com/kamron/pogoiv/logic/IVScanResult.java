package com.kamron.pogoiv.logic;

import java.util.ArrayList;
import java.util.Arrays;

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
 * <p/>
 * Created by Johan on 2016-08-18.
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
    public final int scannedCP;
    public boolean tooManyPossibilities = false; //flag that gets set to true if user tries to scan 10 hp 10 cp pokemon
    public ArrayList<IVCombination> iVCombinations = new ArrayList<>();
    public Pokemon pokemon = null;
    public final double estimatedPokemonLevel;

    /**
     * Creates a holder object for IV scan results
     *
     * @param pokemon               which pokemon it is
     * @param pokemonCP             pokemon CP
     * @param estimatedPokemonLevel the estimated pokemon level (should be very low)
     * @param tooManyPossibilities  true if there are too many possibilities
     */
    IVScanResult(Pokemon pokemon, double estimatedPokemonLevel, int pokemonCP, boolean tooManyPossibilities) {
        this.pokemon = pokemon;
        this.estimatedPokemonLevel = estimatedPokemonLevel;
        this.scannedCP = pokemonCP;
        this.tooManyPossibilities = tooManyPossibilities;
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

        if ((percentPerfect < lowPercent) ||
                ((percentPerfect == lowPercent) && (attackIV < lowAttack))) { // check for same percentage but lower atk
            lowPercent = percentPerfect;
            //save worst combination for lower end cp range
            lowAttack = attackIV;
            lowDefense = defenseIV;
            lowStamina = staminaIV;
        }
        if ((percentPerfect > highPercent) || ((percentPerfect == highPercent) &&
                (attackIV > highAttack))) { // check for same percentage but higher atk
            highPercent = percentPerfect;
            //save best combination for upper end cp range
            highAttack = attackIV;
            highDefense = defenseIV;
            highStamina = staminaIV;
        }


        iVCombinations.add(new IVCombination(attackIV, defenseIV, staminaIV));
    }


    /**
     * @return get the IV combination which has the highest sum of att+def+sta, or tied to equal
     */
    public IVCombination getHighestIVCombination() {
        if (iVCombinations.size() == 0)
            return null;
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
        if (iVCombinations.size() == 0)
            return null;
        IVCombination low = iVCombinations.get(0);
        for (IVCombination ivCombination : iVCombinations) {
            if (ivCombination.getTotal() < low.getTotal()) {
                low = ivCombination;
            }
        }
        return low;
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

    /**
     * Removes any iv combination that is outside the scope of the input percentage range
     * 1: 82.2-100%
     * 2: 66.7-80%
     * 3: 51.1-64.4%
     * 4: 0-48.9%
     * @param selectedItemPosition a number between 1 to 4 as detailed above
     */
    public void refineByAppraisalPercentageRange(int selectedItemPosition) {
        float lowest = 0;
        float highest =100;

        if(selectedItemPosition == 1){
            lowest = 82;
            highest = 100;
        }
        if(selectedItemPosition == 2){
            lowest = 66;
            highest = 80;
        }
        if(selectedItemPosition == 3){
            lowest = 51;
            highest = 65;
        }
        if(selectedItemPosition == 4){
            lowest = 0;
            highest = 50;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();

        for (IVCombination comb:iVCombinations){

            if (comb.percentPerfect >= lowest && comb.percentPerfect <= highest){
                refinedList.add(comb);
            }
        }

        iVCombinations = refinedList;
    }

    /**
     * Removes any iv combination where the highest iv is outside the scope of he input range
     * input:
     * 1: 15
     * 2: 13-14
     * 3: 8-12
     * 4: 0-7
     * @param selectedItemPosition a number between 1 to 4 as detailed above
     */
    public void refineByAppraisalIVRange(int selectedItemPosition) {
        int lowest = 0;
        int highest =100;

        if(selectedItemPosition == 1){
            lowest = 15;
            highest = 15;
        }
        if(selectedItemPosition == 2){
            lowest = 13;
            highest = 14;
        }
        if(selectedItemPosition == 3){
            lowest = 8;
            highest = 12;
        }
        if(selectedItemPosition == 4){
            lowest = 0;
            highest = 7;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();

        for (IVCombination comb:iVCombinations){

            if (comb.getHighestStat() >= lowest && comb.getHighestStat() <= highest){
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;
    }
}
