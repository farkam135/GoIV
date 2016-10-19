package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which represents the IV % that is possible considering what's known about the pokemon.
 * Depending on what's sent in in the constructor, it represents the minimum, average or max iv.
 */

public class IVPercentageToken extends ClipboardToken {
    private IVPercentageTokenMode mode;

    public IVPercentageToken(IVPercentageTokenMode mode) {
        super(false);
        this.mode = mode;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        if (mode == IVPercentageTokenMode.MIN) {
            IVCombination lowestIVCombination = ivScanResult.getLowestIVCombination();
            return lowestIVCombination != null ? String.valueOf(lowestIVCombination.percentPerfect) : "";
        } else if (mode == IVPercentageTokenMode.AVG) {
            return String.valueOf(ivScanResult.getAveragePercent());
        } else if (mode == IVPercentageTokenMode.MAX) {
            IVCombination highestIVCombination = ivScanResult.getHighestIVCombination();
            return highestIVCombination != null ? String.valueOf(highestIVCombination.percentPerfect) : "";
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getPreview() {
        int example = 98 + mode.ordinal();
        return String.valueOf(example);
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(mode);
    }

    @Override
    public String getTokenName(Context context) {
        if (mode == IVPercentageTokenMode.MIN) {
            return "min%";
        } else if (mode == IVPercentageTokenMode.AVG) {
            return "avg%";
        } else if (mode == IVPercentageTokenMode.MAX) {
            //mode 2 is max
            return "max%";
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getLongDescription(Context context) {
        String modeText;
        if (mode == IVPercentageTokenMode.MIN) {
            modeText = context.getString(R.string.token_iv_minimum);
        } else if (mode == IVPercentageTokenMode.AVG) {
            modeText = context.getString(R.string.token_iv_average);
        } else if (mode == IVPercentageTokenMode.MAX) {
            //mode 2 is max
            modeText = context.getString(R.string.token_iv_maximum);
        } else {
            throw new IllegalArgumentException();
        }

        String returner = String.format(context.getString(R.string.token_iv_percentage), modeText);
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
