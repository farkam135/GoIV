package com.kamron.pogoiv.logic;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by NightMadness on 11/21/2016.
 * This class helps GoIV find the lowest and highest values for Appraisal and Calculates overlap
 */
public final class AppraisalHelper {

    /**
     * This function calculates the Lowest and Highest IV ranges possible based on selected appraisals.
     *
     * @param appraisalPercentageRangeSelected Appraisal Percentage Selected
     * @param howManyAppraisalChecked          how many checkboxes are checked
     * @param appraisalIvRangeSelected         Appraisal IV Selected
     * @return AppraisalPercentPrefect which contains Low, Ave, High values or null if none found
     */
    public static @Nullable AppraisalPercentPrefect calculateAppraisalPercentPrefect(
            final int appraisalPercentageRangeSelected,
            final int howManyAppraisalChecked,
            final int appraisalIvRangeSelected) {
        boolean appraisalEvaluated = false;
        ArrayList<IVPercentPrefectPair> appraisalList = new ArrayList<>();

        //Check first Percent Appraisal
        if (appraisalPercentageRangeSelected != 0) {
            AppraisalPercentageRange appraisalPercentageRange = new AppraisalPercentageRange(
                    appraisalPercentageRangeSelected);
            int lowPR = appraisalPercentageRange.getLowest();
            int highPR = appraisalPercentageRange.getHighest();
            appraisalEvaluated = true;
            appraisalList.add(new IVPercentPrefectPair(lowPR, highPR));
        }

        //Check second IV Appraisal
        if (appraisalIvRangeSelected != 0 || howManyAppraisalChecked > 0) {
            AppraisalIVRange appraisalIVRange = new AppraisalIVRange(appraisalIvRangeSelected);

            int lowPR = appraisalIVRange.getLowestPossiblePercent(howManyAppraisalChecked);
            int highPR = appraisalIVRange.getHighestPossiblePercent(howManyAppraisalChecked);
            appraisalEvaluated = true;
            appraisalList.add(new IVPercentPrefectPair(lowPR, highPR));
        }
        //Check if we have evaluated any Appraisal
        if (appraisalEvaluated) {
            //Tries to overlap the low and the high, if its not able to overlap appraisalEvaluated is false
            Integer currentLow = null;
            Integer currentHigh = null;

            for (IVPercentPrefectPair appraisal : appraisalList) {
                int lowPercent = appraisal.lowest;
                int highPercent = appraisal.highest;
                if (currentLow == null || currentLow < lowPercent) {
                    currentLow = lowPercent;
                }
                if (currentHigh == null || currentHigh > highPercent) {
                    currentHigh = highPercent;
                }
            }
            //Try to overlap both high and low,
            //its possible user has selected invalid combination that does not overlap
            if (currentHigh >= currentLow) {
                // values overlap
                int low = currentLow;
                int high = currentHigh;
                int ave = Math.round((low + high) / 2f);
                return new AppraisalPercentPrefect(low, ave, high);
            }
        }
        //seems user has selected selected nothing or selected an invalid combination.
        return null;
    }

    /**
     * This class calculates the Appraisal Percentage Range,
     * will calculated the lowest and highest on the constrictor.
     */
    public static class AppraisalPercentageRange extends IVPercentPrefectPair {
        //Inherited Lowest and Highest from IVPercentPrefectPair.

        public AppraisalPercentageRange(final int selectedItemPosition) {
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
        }
    }

    /**
     * This class calculates the Appraisal IV Range,
     * will calculated the lowest and highest on the constrictor.
     */
    public static class AppraisalIVRange extends IVPercentPrefectPair {
        //Inherited Lowest and Highest from IVPercentPrefectPair.

        public AppraisalIVRange(final int selectedItemPosition) {
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
        }

        //Highest IV'S (Highest + Highest + Highest) out of possible 45
        // = (Highest * 3) / 45
        public final int getHighestPossiblePercent(int howManyChecked) {
            if (howManyChecked == 0) {
                //assume best case all are checked
                howManyChecked = 3;
            }
            return Math.round(((highest * 3 - (1 * (3 - howManyChecked))) / 45f) * 100f);
        }

        //Lowest IV'S (lowest + 0 + 0) out of possible 45
        // = (Lowest) / 45
        public final int getLowestPossiblePercent(int howManyChecked) {
            if (howManyChecked == 0) {
                //assume worst case only one is checked
                howManyChecked = 1;
            }
            return Math.round((lowest * howManyChecked / 45f) * 100f);
        }
    }

    /**
     * This class holds the only the Low and High
     * used when we calculate possible overlap.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE) //used in AppraisalPercentageRange and AppraisalIVRange
    @AllArgsConstructor(access = AccessLevel.PRIVATE) //used in overlap calculation
    private static class IVPercentPrefectPair {
        //variables are protected so they can be used in extend class
        @Getter protected int lowest;
        @Getter protected int highest;
    }

    /**
     * This class holds the Appraisal Percent Prefect
     * it is made public for the test cases.
     * Low, Ave, High Values.
     */
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AppraisalPercentPrefect {
        @Getter private int low;
        @Getter private int ave;
        @Getter private int high;
    }


}
