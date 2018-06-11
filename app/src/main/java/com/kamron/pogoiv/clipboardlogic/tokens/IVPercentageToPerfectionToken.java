package com.kamron.pogoiv.clipboardlogic.tokens;

import android.annotation.SuppressLint;
import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Token representing how far in IVs % your pokemon is to perfect.
 */

public class IVPercentageToPerfectionToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public IVPercentageToPerfectionToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @SuppressLint("DefaultLocale") @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        IVCombination combination = scanResult.getHighestIVCombination();
        if (combination != null) {
            int result = 100 - combination.percentPerfect;
            return String.format("%02d", result);
        } else {
            return "";
        }
    }

    @Override
    public String getPreview() {
        return "03";
    }


    @Override
    public String getTokenName(Context context) {
        return "IV% to top";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_iv_perc_to_top);
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
