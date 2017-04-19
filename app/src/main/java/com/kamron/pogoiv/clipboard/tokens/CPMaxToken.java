package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.CPRange;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

/**
 * Created by johan on 2017-04-12.
 * <p>
 * A token which returns the predicted CP a monster will have at level 40.
 */

public class CPMaxToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CPMaxToken(boolean maxEv) {
        super(maxEv);
    }

    @Override public int getMaxLength() {
        return 4;
    }

    @Override public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        //pokemon low high level
        Pokemon poke = getRightPokemon(ivScanResult.pokemon, pokeInfoCalculator);
        CPRange r = pokeInfoCalculator.getCpRangeAtLevel(poke, ivScanResult.getLowestIVCombination(),
                ivScanResult.getHighestIVCombination(), 40);


        String val = String.valueOf(r.getAvg());
        return val;
    }

    @Override public String getPreview() {
        return "2192";
    }

    @Override public String getTokenName(Context context) {
        return "MaxCP";
    }

    @Override public String getLongDescription(Context context) {
        return "The CP this monster will have at level 40.";
    }

    @Override public String getCategory() {
        return "Basic Stats";
    }

    @Override public boolean changesOnEvolutionMax() {
        return false;
    }
}
