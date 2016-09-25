package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * Iv token representing the average IV % perfection over all possible iv combinations in the ivscanresult.
 */

public class IVAvgPercentageToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return String.valueOf(ivScanResult.getAveragePercent());
    }

    @Override
    public String getPreview() {
        return "100";
    }


    @Override
    public String getTokenName(Context context) {
        return "Average IV percent";
    }
}
