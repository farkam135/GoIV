package com.kamron.pogoiv;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-08-18.
 *
 * A class which represents all possible iv combinations for a pokemon
 */
public class IVScanResult {
    public  int count=0;
    public  int highPercent = 0;
    public  int lowPercent=100;
    public  int lowAttack=15 ;
    public  int lowDefense=15;
    public  int lowStamina=15;
    public  int highAttack=0;
    public  int highDefense=0;
    public  int highStamina=0;
    public ArrayList<IVCombination> iVCombinations = new ArrayList<>();

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
    public IVScanResult() {    }



    /**
     * Calculates and returns the average % of the possible IVs
     * @return
     */
    public int getAveragePercent(){
        int averageSum = 0;
        for (IVCombination ivc : iVCombinations){
            averageSum += ivc.att + ivc.def + ivc.sta;
        }
        return (int) Math.round(((averageSum * 100 / (45.0 * count)))); // new;
    }

    /**
     * adds an IV possibility to the scan results
     * @param attackIV the attack iv
     * @param defenseIV the defense iv
     * @param staminaIV the stamina iv
     */
    public void addIVCombination(int attackIV, int defenseIV, int staminaIV) {
        count++;

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


        iVCombinations.add(new IVCombination(attackIV, defenseIV,staminaIV));
    }
}
