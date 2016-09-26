package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

/**
 * Created by Johan on 2016-09-26.
 * A token representing the hp of the pokemon.
 */

public class HpToken extends ClipboardToken {
    private boolean currentLevel; //whether the user wants to know hp for a level 40 pokemon, or the current level.

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public HpToken(boolean maxEv, boolean currentLevel) {
        super(maxEv);
        this.currentLevel = currentLevel;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(ivScanResult.pokemon, pokeInfoCalculator);
        double level = currentLevel ? ivScanResult.estimatedPokemonLevel : 40;
        int hp = pokeInfoCalculator.getHPAtLevel(ivScanResult, level, poke);
        return String.valueOf(hp);
    }

    @Override
    public String getPreview() {
        int hp = currentLevel ? 30 : 70;
        if (maxEv) {
            hp = hp * 2;
        }
        return String.valueOf(hp);
    }

    @Override
    public String getTokenName(Context context) {
        return currentLevel ? "HP" : "Max level Hp";
    }
}
