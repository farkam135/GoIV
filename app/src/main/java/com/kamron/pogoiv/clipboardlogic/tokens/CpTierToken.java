package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

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
    public String getValue(ScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        TokenTierLogic ttl = new TokenTierLogic();
        Pokemon poke = getRightPokemon(ivs.pokemon, pokeInfoCalculator);
        int cp = pokeInfoCalculator.getCpRangeAtLevel(poke,
                ivs.getCombinationLowIVs(), ivs.getCombinationHighIVs(), 40).getAvg();

        return ttl.getRating(cp);
    }

    @Override
    public String getPreview() {
        return "B-";
    }

    @Override
    public String getTokenName(Context context) {
        return context.getString(R.string.cp) + context.getString(R.string.token_msg_tier);
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_cpTier);
    }

    @Override
    public Category getCategory() {
        return Category.EVALUATION;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }
}
