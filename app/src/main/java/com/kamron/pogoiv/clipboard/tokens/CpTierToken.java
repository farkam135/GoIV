package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
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
        int cp = (int) pokeInfoCalculator.getAverageCPAtLevel(poke, ivs.lowAttack, ivs.lowDefense, ivs.lowStamina,
                ivs.highAttack, ivs.highDefense, ivs.highStamina, 40);

        return ttl.getRating(cp);
    }

    @Override
    public String getPreview() {
        return "B-";
    }

    @Override
    public String getTokenName(Context context) {
        return "CPTier";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token gives you an idea of how powerful this pokemon can become, by measuring the maximum " +
                "possible CP the pokemon can obtain. So for example, A lapras can max out at 2980 CP, while a dugtrio" +
                " would max out at 1168 CP. So lapras would get A while dugrio would get E-. This tier is adjusted " +
                "based on the IV of your pokemon.";

    }
}
