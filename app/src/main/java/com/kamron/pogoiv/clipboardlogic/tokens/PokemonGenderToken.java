package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;

/**
 * Created by Mattia on 2017-12-15.
 * A token which returns the gender of the scanned pokemon
 */

public class PokemonGenderToken extends ClipboardToken {

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     */
    public PokemonGenderToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 1;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return ivScanResult.scannedGender.isPresent() ? ivScanResult.scannedGender.get() : "";
    }

    @Override
    public String getPreview() {
        return "⚤";
    }

    @Override
    public String getTokenName(Context context) {
        return context.getString(R.string.token_pokemon_gender);
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_gender);
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
