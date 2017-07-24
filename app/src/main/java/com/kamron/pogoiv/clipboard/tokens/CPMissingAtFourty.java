
package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.CPRange;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

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

    @Override public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        //pokemon low high level
        Pokemon poke = getRightPokemon(ivScanResult.pokemon, pokeInfoCalculator);

        IVCombination perfectIV = new IVCombination(15, 15, 15);
        CPRange perfectPokemon = pokeInfoCalculator.getCpRangeAtLevel(poke, perfectIV, perfectIV, 40);
        CPRange thisPokemon = pokeInfoCalculator.getCpRangeAtLevel(poke, ivScanResult.getLowestIVCombination(),
                ivScanResult.getHighestIVCombination(), 40);


        String val = String.valueOf(perfectPokemon.getAvg() - thisPokemon.getAvg());
        return val;
    }


    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation();
    }

    @Override public String getPreview() {

        return "133";
    }

    @Override public String getTokenName(Context context) {
        return "-Cp40";
    }

    @Override public String getLongDescription(Context context) {
        return "Get how much CP the monster will be missing compared to a perfect IV level 40 variant";
    }

    @Override public String getCategory() {
        return "Basic Stats";
    }

    @Override public boolean changesOnEvolutionMax() {
        return true;
    }
}
