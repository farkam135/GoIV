package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;

import java.util.Locale;

/**
 * Created by Danilo Pianini.
 * A token which returns a "tier" based on the pokemon max cp, in the AA-ZZ range.
 */

public class IVSum extends ClipboardToken {

    private final boolean best;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param best true if the token should display the best combination, false for the worst.
     */
    public IVSum(boolean best) {
        super(false);
        this.best = best;
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        final IVCombination combination = best ? ivs.getHighestIVCombination() : ivs.getLowestIVCombination();
        if (combination == null) {
            return "??";
        }
        return String.format(Locale.ENGLISH, "%02d", combination.getTotal());
    }

    @Override
    public String getPreview() {
        return "34";
    }

    @Override
    public String getTokenName(Context context) {
        return "IV-" + getType() + "-sum";
    }

    private String getType() {
        return best ? "best" : "worst";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token returns the sum of the " + getType() + " possible IV stats for this monster"
                + ". Ranges in 00-45. Always returns two digits.";
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
