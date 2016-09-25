package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which represents the lowest IV % that is possible considering what's known about the pokemon
 */

public class IVMinPercentageToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return String.valueOf(ivScanResult.getLowestIVCombination().percentPerfect);
    }

    @Override
    public String getPreview() {
        return "99";
    }


    @Override
    public String getTokenName(Context context) {
        return "Min IV percentage";
    }
}
