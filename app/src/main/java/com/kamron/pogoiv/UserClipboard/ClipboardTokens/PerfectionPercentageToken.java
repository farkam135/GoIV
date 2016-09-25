package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * Token representing how close your pokemon is in max CP compared to if the pokemon had perfect IVs.
 */

public class PerfectionPercentageToken extends ClipboardToken {
    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        double perfectIVCP = pokeInfoCalculator.getAverageCPAtLevel(isr.pokemon, 15, 15, 15, 15, 15, 15, 40);
        double thisCP =
                pokeInfoCalculator.getAverageCPAtLevel(isr.pokemon, isr.lowAttack, isr.lowDefense, isr.lowStamina,
                        isr.highAttack, isr.highDefense, isr.highStamina, 40);
        int roundedPerfection = (int) ((thisCP / perfectIVCP) + 0.5);
        return String.valueOf(roundedPerfection);
    }

    @Override
    public String getPreview() {
        return "97";
    }


    @Override
    public String getTokenName(Context context) {
        return "Perfection compared to perfect IV";
    }
}
