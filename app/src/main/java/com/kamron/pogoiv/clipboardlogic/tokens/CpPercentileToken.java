package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2016-10-06.
 * A token which says -compared to all possible iv combinations- how rare is an iv combination this good?
 */

public class CpPercentileToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CpPercentileToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    private static int compositionLookup(int total) {
        int[] lookup = new int[] {100, 100, 100, 100, 100, 99, 99, 98, 97, 96, 95, 93, 91, 89, 86, 83, 80, 76, 72, 68,
                64, 59, 55, 50, 45, 41, 36, 32, 28, 24, 20, 17, 14, 11, 9, 7, 5, 4, 3, 2, 1, 1, 0, 0, 0, 0};
        return lookup[total];
    }

    @Override
    public String getValue(ScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        int thisResult = isr.getIVAttackLow() + isr.getIVDefenseLow() + isr.getIVStaminaLow();
        return String.valueOf(compositionLookup(thisResult));
    }

    @Override
    public String getPreview() {
        return "5";
    }

    @Override
    public String getTokenName(Context context) {
        return "IV %top";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_cpPerc);
    }

    @Override
    public Category getCategory() {
        return Category.EVALUATION;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
