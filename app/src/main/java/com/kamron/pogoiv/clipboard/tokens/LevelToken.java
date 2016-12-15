package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

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
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        if (mode == 0) {
            return String.valueOf((int) ivScanResult.estimatedPokemonLevel * 2);
        }
        if (mode == 1) {
            return String.valueOf((int) ivScanResult.estimatedPokemonLevel);
        }
        DecimalFormat df = new DecimalFormat("##.#");
        return df.format(ivScanResult.estimatedPokemonLevel);
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
            return "Lvl2";
        }
        if (mode == 1) {
            return "Lvl";
        }
        return "Lv.5";
    }

    @Override
    public String getLongDescription(Context context) {
        if (mode == 0) {
            return context.getString(R.string.clipboard_token_level_mode0_description);
        }
        if (mode == 1) {
            return context.getString(R.string.clipboard_token_level_mode1_description);
        }
        return context.getString(R.string.clipboard_token_level_default_description);
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.clipboard_token_category_basic_stats);
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
