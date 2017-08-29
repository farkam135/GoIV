package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;

/**
 * Copied from UnicodeToken created by Johan on 2016-09-25.
 * MixedUnicodeToken created by TripSixes on 2017-01-12
 * Represents the lowest possible IV combination with ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮, but also
 * mixes the filled and non-filled characters depending on if there are multiple or single of
 * a given IV Result list.
 */

public class MixedUnicodeToken extends ClipboardToken {
    private final String[] unicode_0_15 = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦",
                                           "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮"};
    private final String[] unicode_0_15filled = {"⓿", "❶", "❷", "❸", "❹", "❺", "❻", "❼",
                                                 "❽", "❾", "❿", "⓫", "⓬", "⓭", "⓮", "⓯"};
    private final boolean filled;

    /**
     * Define which of the unicode character sets is the "default" set.  In this token, the default
     * set will be the one that is used when the stat value is comprised of only one value (ie: all attack stats are
     * 14).  The Non-default unicode character set will be used for multi-result values (ie: if defense ranges from
     * 7-10, the non-default character set will be used to show 7 for defense).
     * @param filled boolean indicating user preference for exact-match characters.
     */
    public MixedUnicodeToken(boolean filled) {
        super(false);
        this.filled = filled;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        //Initialize the lowest and highest of each stat
        int lowestAttackStat = 15;
        int lowestDefenseStat = 15;
        int lowestStaminaStat = 15;
        int highestAttackStat = 0;
        int highestDefenseStat = 0;
        int highestStaminaStat = 0;

        //Loop through all iVCombinations to find the lowest of each stat
        for (IVCombination ivc : ivScanResult.iVCombinations) {
            // Save the lowest and highest attackIV of any Combination
            if (ivc.att < lowestAttackStat) {
                lowestAttackStat = ivc.att;
            }
            if (ivc.att > highestAttackStat) {
                highestAttackStat = ivc.att;
            }
            // Save the lowest and highest defenseIV of any Combination
            if (ivc.def < lowestDefenseStat) {
                lowestDefenseStat = ivc.def;
            }
            if (ivc.def > highestDefenseStat) {
                highestDefenseStat = ivc.def;
            }
            // Save the lowest and highest staminaIV of any Combination
            if (ivc.sta < lowestStaminaStat) {
                lowestStaminaStat = ivc.sta;
            }
            if (ivc.sta > highestStaminaStat) {
                highestStaminaStat = ivc.sta;
            }
        }

        //Since each stat will have it's own unicode character set, we initialize one for each
        String[] attToUse;
        String[] defToUse;
        String[] staToUse;

        //If the user setting is set to filled, then use filled characters for exact matches,
        //otherwise, use the empty characters for exact matches.
        if (filled) {
            attToUse = (lowestAttackStat == highestAttackStat) ? unicode_0_15filled : unicode_0_15;
            defToUse = (lowestDefenseStat == highestDefenseStat) ? unicode_0_15filled : unicode_0_15;
            staToUse = (lowestStaminaStat == highestStaminaStat) ? unicode_0_15filled : unicode_0_15;
        } else {
            attToUse = (lowestAttackStat == highestAttackStat) ? unicode_0_15 : unicode_0_15filled;
            defToUse = (lowestDefenseStat == highestDefenseStat) ? unicode_0_15 : unicode_0_15filled;
            staToUse = (lowestStaminaStat == highestStaminaStat) ? unicode_0_15 : unicode_0_15filled;
        }

        //We still need to get thew lowest combination when showing the final result, but this time
        //the unicode character set to use is controlled by whether each stat is exactly known or multiple values
        IVCombination lowestIVCombination = ivScanResult.getLowestIVCombination();
        if (lowestIVCombination == null) {
            return "";
        }

        return attToUse[lowestIVCombination.att]
                + defToUse[lowestIVCombination.def]
                + staToUse[lowestIVCombination.sta];
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(filled);
    }

    @Override
    public String getPreview() {
        return filled ? "❾⑫①" : "⑨⓬❶";
    }

    @Override
    public String getTokenName(Context context) {
        return "UIV-mixed";
    }

    @Override
    public String getLongDescription(Context context) {
        String returner = "Similar to UIV, UNICODE circular numbers are used to represent IV.";

        if (filled) {
            returner += " This token uses filled characters to represent single values and empty characters "
                    + "to represent the lowest of multiple values. For example, ⓭ in the attack position would "
                    + "mean that all Attack values are 13, while ① in the defense or HP means there are "
                    + "multiple values possible and that 1 is the lowest.";
        } else {
            returner += " This token uses empty characters to represent single values and filled characters "
                    + "to represent the lowest of multiple values. For example, ⑪ in the attack position would "
                    + "mean that all Attack values are 11, while ❾ in the defense or HP means there are "
                    + "multiple values possible and that 9 is the lowest.";
        }
        return returner;
    }

    @Override
    public Category getCategory() {
        return Category.IV_INFO;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
