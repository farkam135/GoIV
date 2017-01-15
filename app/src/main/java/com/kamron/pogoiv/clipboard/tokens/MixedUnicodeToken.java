package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Copied from UnicodeToken created by Johan on 2016-09-25.
 * MixedUnicodeToken created by TripSixes on 2017-01-12
 * Represents the lowest possible IV combination with ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮, but also
 * mixes the filled and non-filled characters depending on if there are multiple or single of
 * a given IV Result list.
 */

public class MixedUnicodeToken extends ClipboardToken {
    private String[] unicode_0_15 = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮"};
    private String[] unicode_0_15filled = {"⓿", "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "❿", "⓫", "⓬", "⓭", "⓮", "⓯"};
    private boolean filled;

    /**
     * Define which of the unicode character sets is the "default" set.  In this token, the default
     * set will be the one that is used for single-result values.  The Non-default unicode character
     * set will be used for multi-result values (showing lowest)
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
        //Get the absolute lowest stats for each att/def/sta
        IVCombination lowest = ivScanResult.getAbsoluteLowestStats();
        //Get the absolute highest stats for each att/def/sta
        IVCombination highest = ivScanResult.getAbsoluteHighestStats();

        if ((lowest == null) || (highest == null)) {
            return "";
        }

        String[] attToUse;
        String[] defToUse;
        String[] staToUse;

        if (filled) {
            attToUse = (lowest.att == highest.att) ? unicode_0_15filled : unicode_0_15;
            defToUse = (lowest.def == highest.def) ? unicode_0_15filled : unicode_0_15;
            staToUse = (lowest.sta == highest.sta) ? unicode_0_15filled : unicode_0_15;
        }
        else {
            attToUse = (lowest.att == highest.att) ? unicode_0_15 : unicode_0_15filled;
            defToUse = (lowest.def == highest.def) ? unicode_0_15 : unicode_0_15filled;
            staToUse = (lowest.sta == highest.sta) ? unicode_0_15 : unicode_0_15filled;
        }

        //We still need to get thew lowest combination when showing the final result, but this time
        //each unicode character will be filled or empty depending on whether multiple or exact values
        //were calculated for each stat.
        IVCombination lowestIVCombination = ivScanResult.getLowestIVCombination();
        if (lowestIVCombination == null) {
            return "";
        }

        return attToUse[lowestIVCombination.att] +
                defToUse[lowestIVCombination.def] +
                staToUse[lowestIVCombination.sta];

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
    public String getCategory() {
        return "IV Info";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
