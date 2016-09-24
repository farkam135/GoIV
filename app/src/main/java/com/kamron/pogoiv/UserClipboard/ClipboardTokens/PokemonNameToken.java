package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-24.
 * <p>
 * A token which returns the name of the scanned pokemon
 */

public class PokemonNameToken implements ClipboardToken {
    @Override
    public int getMaxLength() {
        return 12; // assume pokemon name can fill out entire name field
        //Currently "Fletchinder" is the longest name english pokemon at 11 chars, but
        //this can be different in other languages
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return ivScanResult.pokemon.name;
    }

    @Override
    public String getPreview() {
        return "Abra";
    }

    @Override
    public String getStringRepresentation() {
        return ".PokeName";
    }

    @Override
    public String getTokenName(Context context) {
        return "Pokemon name";
    }
}
