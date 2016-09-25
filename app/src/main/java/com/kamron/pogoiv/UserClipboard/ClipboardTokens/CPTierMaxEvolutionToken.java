package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-24.
 * A token representing the tier based on the max CP of the last evolution in a pokemon evolution line.
 */

public class CPTierMaxEvolutionToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        TokenTierLogic ttl = new TokenTierLogic();
        Pokemon lastEv;

        //If-else below exists to manage scenario where pokemon has multiple evolution possibilities, so for example
        //If you scan a vaporeon, it has no evolutions, so there's no need to go to the bottom of the evolution chain
        // and find jolteon.. or whatever eeveelution is last.
        if (ivs.pokemon.evolutions.size() != 0) {
            ArrayList<Pokemon> evLine = pokeInfoCalculator.getEvolutionLine(ivs.pokemon);
            lastEv = evLine.get(evLine.size() - 1);
        } else {
            lastEv = ivs.pokemon;
        }

        int cp = pokeInfoCalculator.getAverageCPAtLevel(lastEv, ivs.lowAttack, ivs.lowDefense, ivs.lowStamina,
                ivs.highAttack, ivs.highDefense, ivs.highStamina, 40);

        return ttl.getRating(cp);
    }

    @Override
    public String getPreview() {
        return "A+";
    }

    @Override
    public String getTokenName(Context context) {
        return "Pokemon tier for last evolution";
    }
}
