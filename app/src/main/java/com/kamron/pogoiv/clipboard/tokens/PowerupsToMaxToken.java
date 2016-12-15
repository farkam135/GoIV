package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

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

    @Override public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return ("" + (80 - (int) ((ivScanResult.estimatedPokemonLevel) * 2)));
    }

    @Override public String getPreview() {
        return "55";
    }

    @Override public String getTokenName(Context context) {
        return "PUpTo40";
    }

    @Override public String getLongDescription(Context context) {
        return context.getString(R.string.clipboard_token_poweruptomax_description);
    }

    @Override public String getCategory(Context context) {
        return context.getString(R.string.clipboard_token_category_basic_stats);
    }

    @Override public boolean changesOnEvolutionMax() {
        return false;
    }
}
