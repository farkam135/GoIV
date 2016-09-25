package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-25.
 * <p>
 * Token representing how close your pokemon is in max co compared to if the pokemon had perfect IVs in the last
 * evolution. Does not take edge case where pokemon has multiple last evolutions into account.
 */

public class PerfectionLastEvPercentageToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon lastEv;
        //If-else below exists to manage scenario where pokemon has multiple evolution possibilities, so for example
        //If you scan a vaporeon, it has no evolutions, so there's no need to go to the bottom of the evolution chain
        // and find jolteon.. or whatever eeveelution is last.
        if (isr.pokemon.evolutions.size() != 0) {
            ArrayList<Pokemon> evLine = pokeInfoCalculator.getEvolutionLine(isr.pokemon);
            lastEv = evLine.get(evLine.size() - 1);
        } else {
            lastEv = isr.pokemon;
        }


        double perfectIVCP = pokeInfoCalculator.getAverageCPAtLevel(lastEv, 15, 15, 15, 15, 15, 15, 40);
        double thisCP =
                pokeInfoCalculator.getAverageCPAtLevel(lastEv, isr.lowAttack, isr.lowDefense, isr.lowStamina,
                        isr.highAttack, isr.highDefense, isr.highStamina, 40);
        int roundedPerfection = (int) (100 * (thisCP / perfectIVCP) + 0.5);
        return String.valueOf(roundedPerfection);
    }

    @Override
    public String getPreview() {
        return "96";
    }


    @Override
    public String getTokenName(Context context) {
        return "Last evolution perfection compared to perfect IV";
    }
}
