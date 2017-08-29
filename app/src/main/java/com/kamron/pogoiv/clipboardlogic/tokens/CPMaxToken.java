package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.CPRange;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;

/**
 * Created by johan on 2017-04-12.
 * <p>
 * A token which returns the predicted CP a monster will have at level 40 or the current level.
 */

public class CPMaxToken extends ClipboardToken {

    private boolean currentLevel; //whether the user wants to know cp for a level 40 pokemon, or the current level.

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CPMaxToken(boolean maxEv, boolean currentLevel) {
        super(maxEv);
        this.currentLevel = currentLevel;
    }

    @Override public int getMaxLength() {
        return 4;
    }

    @Override public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        //pokemon low high level
        Pokemon poke = getRightPokemon(ivScanResult.pokemon, pokeInfoCalculator);

        double level = currentLevel ? ivScanResult.estimatedPokemonLevel : 40;
        CPRange r = pokeInfoCalculator.getCpRangeAtLevel(poke, ivScanResult.getLowestIVCombination(),
                ivScanResult.getHighestIVCombination(), level);


        String val = String.valueOf(r.getAvg());
        return val;
    }


    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(currentLevel);
    }

    @Override public String getPreview() {
        int cp = currentLevel ? 230 : 440;
        if (maxEv) {
            cp = cp * 2;
        }
        return String.valueOf(cp);
    }

    @Override public String getTokenName(Context context) {
        return currentLevel ? "Cp" : "Cp+";
    }

    @Override public String getLongDescription(Context context) {
        if (currentLevel) {
            return "Get how much CP the monster has at the current level.";
        }
        return "Get how much CP the monster will have at max level.";
    }

    @Override public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override public boolean changesOnEvolutionMax() {
        return true;
    }
}
