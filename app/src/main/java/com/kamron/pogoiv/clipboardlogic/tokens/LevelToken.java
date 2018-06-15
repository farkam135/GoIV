package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.text.DecimalFormat;

/**
 * Created by Johan on 2016-09-26.
 * A token which returns the scanned level of a pokemon.
 */

public class LevelToken extends ClipboardToken {
    private final int mode;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     * @param mode  0 for 0-80, 1 for 0-40 no decimal, rounded down, 2 for 0-40 with decimal (ex 10.5)
     */
    public LevelToken(boolean maxEv, int mode) {
        super(maxEv);
        this.mode = mode;
    }

    @Override
    public int getMaxLength() {
        if (mode == 2) {
            return 4;
        }
        return 2;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        if (mode == 0) {
            return String.valueOf((int) (scanResult.levelRange.min * 2));
        }
        if (mode == 1) {
            return String.valueOf((int) scanResult.levelRange.min);
        }
        DecimalFormat df = new DecimalFormat("##.#");
        return df.format(scanResult.levelRange.min);
    }

    @Override
    public String getPreview() {
        if (mode == 0) {
            return "41";
        }
        if (mode == 1) {
            return "20";
        }
        return "20.5";
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(mode);
    }

    @Override
    public String getTokenName(Context context) {
        if (mode == 0) {
            return context.getString(R.string.token_msg_lvl) + "x2";
        }
        if (mode == 1) {
            return context.getString(R.string.token_msg_lvl);
        }
        return context.getString(R.string.token_msg_lvl) + ".5";
    }

    @Override
    public String getLongDescription(Context context) {
        if (mode == 0) {
            return context.getString(R.string.token_msg_lvl_msg1);
        }
        if (mode == 1) {
            return context.getString(R.string.token_msg_lvl_msg2);
        }
        return context.getString(R.string.token_msg_lvl_msg3);
    }

    @Override
    public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
