package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-24.
 * <p>
 * <p>A token which returns a "tier" based on the pokemon max cp.
 */

public class CpTierToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        TokenTierLogic ttl = new TokenTierLogic();
        int cp = pokeInfoCalculator.getAverageCPAtLevel(ivs.pokemon, ivs.lowAttack, ivs.lowDefense, ivs.lowStamina,
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
