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

public class MaxEvolutionCPTierToken implements ClipboardToken {
    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        TokenTierLogic ttl = new TokenTierLogic();
        ArrayList<Pokemon> evLine = pokeInfoCalculator.getEvolutionLine(ivs.pokemon);
        Pokemon lastEv = evLine.get(evLine.size() - 1);
        int cp = pokeInfoCalculator.getAverageCPAtLevel(lastEv, ivs.lowAttack, ivs.lowDefense, ivs.lowStamina,
                ivs.highAttack, ivs.highDefense, ivs.highStamina, 40);

        return ttl.getRating(cp);
    }

    @Override
    public String getPreview() {
        return "A+";
    }

    @Override
    public String getStringRepresentation() {
        return ".PokemonTierMaxEv";
    }

    @Override
    public String getTokenName(Context context) {
        return "Pokemon tier for last evolution";
    }
}
