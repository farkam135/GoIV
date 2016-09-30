package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

/**
 * Created by Johan on 2016-09-25.
 * Token representing how close your pokemon is in max CP compared to if the pokemon had perfect IVs.
 */

public class PerfectionCPPercentageToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public PerfectionCPPercentageToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(isr.pokemon, pokeInfoCalculator);
        double perfectIvCp = pokeInfoCalculator.getAverageCPAtLevel(poke, 15, 15, 15, 15, 15, 15, 40);
        double thisCP =
                pokeInfoCalculator.getAverageCPAtLevel(poke, isr.lowAttack, isr.lowDefense, isr.lowStamina,
                        isr.highAttack, isr.highDefense, isr.highStamina, 40);
        int roundedPerfection = (int) ((thisCP / perfectIvCp) * 100);
        return String.valueOf(roundedPerfection);
    }

    @Override
    public String getPreview() {
        return "97";
    }


    @Override
    public String getTokenName(Context context) {
        return "mIV%";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token calculates how close your Pokémon is to its max potential, measured by CP. For example, if" +
                " a Pokémon with max IVs maxes out at 2000cp, but your specific pokemon maxes out at 1900, then your " +
                "pokemon perfection is 95%, so this token returns 95.";
    }
}
