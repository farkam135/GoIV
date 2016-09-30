package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-26.
 * Get the pokemon iv as a hex representation.
 */

public class HexIVToken extends ClipboardToken {

    String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public HexIVToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        int att = ivScanResult.getLowestIVCombination().att;
        int def = ivScanResult.getLowestIVCombination().def;
        int sta = ivScanResult.getLowestIVCombination().sta;

        return hex[att] + hex[def] + hex[sta];
    }

    @Override
    public String getPreview() {
        return "9A3";
    }

    @Override
    public String getTokenName(Context context) {
        return "HexIV";
    }

    @Override
    public String getLongDescription(Context context) {
        return "Represents the Pokémon IVs in a base 16 number system. This means that each IV can be represented by " +
                "a single character. So for example, a pokemon with IVs 10, 13, 3 would be represented as AD3.";
    }
}
