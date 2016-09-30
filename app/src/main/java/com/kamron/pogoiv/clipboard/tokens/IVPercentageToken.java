package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which represents the IV % that is possible considering what's known about the pokemon.
 * Depending on what's sent in in the constructor, it represents the minimum, average or max iv.
 */

public class IVPercentageToken extends ClipboardToken {

    private int mode;

    public IVPercentageToken(String mode) {
        super(false);
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
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(mode);
    }

    @Override
    public String getTokenName(Context context) {
        if (mode == 0) {
            return "min%";
        } else if (mode == 1) {
            return "avg%";
        }
        //mode 2 is max
        return "max%";
    }

    @Override
    public String getLongDescription(Context context) {
        String modeText;
        if (mode == 0) {
            modeText = "minimum%";
        } else if (mode == 1) {
            modeText = "average";
        }
        //mode 2 is max
        modeText = "maximum";

        String returner = "Get the " + modeText + " percent of the IV possibilities. If only one iv combination is "
                + "possible, minimum, average and maximum will be the same."
                + " For example, if the iv range is 55-75, the minimum will return 55, the average will return "
                + "something between 55 and 75, and the maximum will return 75.";
        return returner;
    }
}
