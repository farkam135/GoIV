package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

/**
 * Created by Johan on 2016-09-24.
 * A token which returns a "tier" based on the pokemon max cp.
 */

public class CpTierToken extends ClipboardToken {


    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CpTierToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        TokenTierLogic ttl = new TokenTierLogic();
        Pokemon poke = getRightPokemon(ivs.pokemon, pokeInfoCalculator);
        int cp = pokeInfoCalculator.getAverageCPAtLevel(poke, ivs.lowAttack, ivs.lowDefense, ivs.lowStamina,
                ivs.highAttack, ivs.highDefense, ivs.highStamina, 40);

        return ttl.getRating(cp);
    }

    @Override
    public String getPreview() {
        return "B-";
    }

    @Override
    public String getTokenName(Context context) {
        return "Pokemon tier";
    }
}
