package com.kamron.pogoiv.scanlogic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kamron.pogoiv.pokeflycomponents.AutoAppraisal;
import com.kamron.pogoiv.utils.LevelRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

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
    public final LevelRange estimatedPokemonLevel;
    public int scannedHP = 0;
    public boolean rangeIVScan = false; //is this several levels worth of possible iv combinations?
    public Pokemon.Gender scannedGender;

    /**
     * Creates a holder object for IV scan results.
     *
     * @param pokemon        Which pokemon it is
     * @param estimatedLevel The estimated pokemon level
     * @param pokemonCP      Pokemon CP
     * @param gender         Pokemon gender
     */
    public IVScanResult(Pokemon pokemon, LevelRange estimatedLevel, int pokemonCP, Pokemon.Gender gender) {
        this.pokemon = pokemon;
        this.estimatedPokemonLevel = estimatedLevel;
        this.scannedCP = pokemonCP;
        this.scannedGender = gender;
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

    public void refineWithAvailableInfoFrom(@Nullable AutoAppraisal autoAppraisal) {
        if (autoAppraisal != null) {
            refineByStatModifiers(autoAppraisal.statModifiers);
            refineByHighest(autoAppraisal.highestStats);
            refineByAppraisalPercentageRange(autoAppraisal.appraisalIVPercentRange);
            refineByAppraisalIVRange(autoAppraisal.appraisalHighestStatValueRange);
        }
    }

    /**
     * Removes all possible IV combinations where the boolean set to true stat isn't the highest.
     * Several stats can be highest if they're equal.
     */
    public void refineByHighest(@NonNull HashSet<AutoAppraisal.HighestStat> highestStats) {
        if (highestStats.isEmpty()) {
            return;
        }

        Boolean[] knownAttDefSta = { highestStats.contains(AutoAppraisal.HighestStat.ATK),
                                     highestStats.contains(AutoAppraisal.HighestStat.DEF),
                                     highestStats.contains(AutoAppraisal.HighestStat.STA) };
        ArrayList<IVCombination> refinedList = new ArrayList<>();
        for (IVCombination comb : iVCombinations) {
            if (Arrays.equals(comb.getHighestStatSignature(), knownAttDefSta)) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;

        updateHighAndLowValues();
    }

    /**
     * Removes any iv combination that is outside the scope of the input percentage range.
     * @param range IV percent range
     */
    public void refineByAppraisalPercentageRange(AutoAppraisal.IVPercentRange range) {
        if (range == AutoAppraisal.IVPercentRange.UNKNOWN) {
            return;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();
        for (IVCombination comb : iVCombinations) {
            if (comb.percentPerfect >= range.minPercent && comb.percentPerfect <= range.maxPercent) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;

        updateHighAndLowValues();
    }

    /**
     * Removes any iv combination where the highest IV is outside the scope of he input range.
     * @param range Range of the highest stat IV value
     */
    public void refineByAppraisalIVRange(AutoAppraisal.IVValueRange range) {
        if (range == AutoAppraisal.IVValueRange.UNKNOWN) {
            return;
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();
        for (IVCombination comb : iVCombinations) {
            if (comb.getHighestStat() >= range.minValue && comb.getHighestStat() <= range.maxValue) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;

        updateHighAndLowValues();
    }

    /**
     * Removes any combination that has stats that are lower than a certain amount. Egg and raid pokemon cannot have
     * stats that are lower than 10, weather boosted can't be lower than 4.
     */
    public void refineByStatModifiers(@NonNull HashSet<AutoAppraisal.StatModifier> statModifiers) {
        if (statModifiers.isEmpty()) {
            return;
        }

        int minStat = 0;
        for (AutoAppraisal.StatModifier statModifier : statModifiers) {
            minStat = Math.max(minStat, statModifier.minStat);
        }

        ArrayList<IVCombination> refinedList = new ArrayList<>();
        for (IVCombination comb : iVCombinations) {
            if (comb.att >= minStat && comb.def >= minStat && comb.sta >= minStat) {
                refinedList.add(comb);
            }
        }
        iVCombinations = refinedList;

        updateHighAndLowValues();
    }

    public void addPossibilitiesFrom(IVScanResult ivs) {

        for (IVCombination ivc : ivs.iVCombinations) {
            if (iVCombinations.contains(ivc) == false) { //dont add duplicates
                addIVCombination(ivc.att, ivc.def, ivc.sta);
                rangeIVScan = true;
            }

        }

        updateHighAndLowValues();
    }
}
