package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which represents the IV % that is possible considering what's known about the pokemon.
 * Depending on what's sent in in the constructor, it represents the minimum, average or max iv.
 */

public class IVPercentageToken extends ClipboardToken {

    private int mode;
    private String modeText;

    public IVPercentageToken(String mode){
        super(false);
        modeText = mode;
        if (mode.equals("Minimum")) {
            this.mode = 0;
        } else if (mode.equals("Average")) {
            this.mode = 1;
        } else { //max
            this.mode = 2;
        }
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        if (mode == 0) { //minimum
            return String.valueOf(ivScanResult.getLowestIVCombination().percentPerfect);
        } else if (mode == 1) { //average
            return String.valueOf(ivScanResult.getAveragePercent());
        }
        //max
        return String.valueOf(ivScanResult.getHighestIVCombination().percentPerfect);
    }

    @Override
    public String getPreview() {
        int example = 98 + mode;
        return String.valueOf(example);
    }

    @Override
    public String getTokenName(Context context) {
        return modeText + " IV percentage";
    }
}
