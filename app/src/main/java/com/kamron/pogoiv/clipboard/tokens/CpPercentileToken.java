package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

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
        return 4;
    }

    private static int compositionLookup(int total) {
        int[] lookup = new int[] {100, 100, 100, 100, 100, 99, 99, 98, 97, 96, 95, 93, 91, 89, 86, 83, 80, 76, 72, 68,
                64, 59, 55, 50, 45, 41, 36, 32, 28, 24, 20, 17, 14, 11, 9, 7, 5, 4, 3, 2, 1, 1, 0, 0, 0, 0};
        return lookup[total];
    }

    @Override
    public String getValue(IVScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        int thisResult = isr.lowAttack + isr.lowDefense + isr.lowStamina;
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
        return "Returns a percentage expressing how many other possible permutations of IVs are better than this "
                + "monster's IVs. The smaller the result is, the better it is.";
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
