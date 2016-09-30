package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

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
            return "This token represents the level of the Pokémon, as it was scanned, times 2. This avoids decimals," +
                    " which makes the text longer, but does not lose information like removing the decimal does. For " +
                    "example, if the Pokémon is level 10.5, this will return 21.";
        }
        if (mode == 1) {
            return "This token represents the level of the Pokémon, as it was scanned, but removes the decimal." +
                    " This potentially loses information. For example, a level 10.5 Pokémon will return 10.";
        }
        return "This token represents the level of the Pokémon, as it was scanned, including the decimal." +
                " This makes the output longer than the alternatives, but is very accurate. For example, a level 10.5" +
                " pokemon will return as 10.5.";
    }
}
