
package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.CPRange;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by johan on 2017-07-11.
 * <p>
 * A token which returns the predicted CP a monster will be missing compared to a perfect pokemon at level 40.
 */

public class CPMissingAtFourty extends ClipboardToken {

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CPMissingAtFourty(boolean maxEv) {
        super(maxEv);
    }

    @Override public int getMaxLength() {
        return 4;
    }

    @Override public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        //pokemon low high level
        Pokemon poke = getRightPokemon(scanResult.pokemon, pokeInfoCalculator);

        IVCombination perfectIV = new IVCombination(15, 15, 15);
        CPRange perfectPokemon = pokeInfoCalculator.getCpRangeAtLevel(poke, perfectIV, perfectIV, 40);
        CPRange thisPokemon = pokeInfoCalculator.getCpRangeAtLevel(poke, scanResult.getLowestIVCombination(),
                scanResult.getHighestIVCombination(), 40);

        return String.valueOf(perfectPokemon.getAvg() - thisPokemon.getAvg());
    }


    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation();
    }

    @Override public String getPreview() {
        return "133";
    }

    @Override public String getTokenName(Context context) {
        return "-" + context.getString(R.string.cp) + "40";
    }

    @Override public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_cpMis);
    }

    @Override public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override public boolean changesOnEvolutionMax() {
        return true;
    }
}
