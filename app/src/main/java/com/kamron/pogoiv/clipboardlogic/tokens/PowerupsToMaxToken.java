package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2016-11-24.
 * <p>
 * A token which returns how many powerupts would be required to get a pokemon to level 40.
 */

public class PowerupsToMaxToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public PowerupsToMaxToken(boolean maxEv) {
        super(maxEv);
    }

    @Override public int getMaxLength() {
        return 2;
    }

    @Override public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        return ("" + (80 - (int) (scanResult.levelRange.min * 2)));
    }

    @Override public String getPreview() {
        return "55";
    }

    @Override public String getTokenName(Context context) {
        return context.getString(R.string.token_msg_PUpTo) + "40";
    }

    @Override public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_powUp);
    }

    @Override public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override public boolean changesOnEvolutionMax() {
        return false;
    }
}
