package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2018-09-29.
 */

public class CandyTo40 extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CandyTo40(boolean maxEv) {
        super(maxEv);
    }

    @Override public int getMaxLength() {
        return 3;
    }

    @Override public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        double lvl = scanResult.levelRange.min;
        int candy = pokeInfoCalculator.getUpgradeCost(40, lvl, scanResult.isLucky).candy;
        return candy+"";
    }

    @Override public String getPreview() {
        return "44";
    }

    @Override public String getTokenName(Context context) {
        return "CandyTo40";
    }

    @Override public String getLongDescription(Context context) {
        return "Get how much candy is needed to level the pokemon to level 40.";
    }

    @Override public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override public boolean changesOnEvolutionMax() {
        return false;
    }
}
