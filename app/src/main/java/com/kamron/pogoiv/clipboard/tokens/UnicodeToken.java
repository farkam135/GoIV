package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * Represents the lowest possible IV combination with ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮
 */

public class UnicodeToken extends ClipboardToken {
    String[] unicode_0_15 = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮"};
    String[] unicode_0_15filled = {"⓿", "❶", "➋", "➌", "➍", "➎", "➏", "➐", "➑", "➒", "➓", "⓫", "⓬", "⓭", "⓮", "⓯"};
    boolean filled;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public UnicodeToken(boolean maxEv, boolean filled) {
        super(maxEv);
        this.filled = filled;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {

        String returner = "";
        int att = ivScanResult.getLowestIVCombination().att;
        int def = ivScanResult.getLowestIVCombination().def;
        int sta = ivScanResult.getLowestIVCombination().sta;
        String[] toUse = filled ? unicode_0_15filled : unicode_0_15;

        returner += toUse[att];
        returner += toUse[def];
        returner += toUse[sta];
        return returner;
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(filled);
    }

    @Override
    public String getPreview() {
        return filled ? "➒⓬❶" : "⑨⑫①";
    }

    @Override
    public String getTokenName(Context context) {
        return "UIV";
    }

    @Override
    public String getLongDescription(Context context) {
        String returner = "This token gives you a representation fo your pokemon Ivs, but using UNICODE special " +
                "characters which" +
                " allows you to show numbers like 10 as a single character, like ⑪. This saves space.";

        if (filled) {
            returner += " This token is the filled version, which has a black inside for the numbers, like ⓭.";
        } else {

            returner += " This token is the empty version, which has a white inside for the numbers, like ⑪.";
        }
        return returner;
    }
}
