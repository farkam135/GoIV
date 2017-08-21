package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
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
        double perfectIvCp = pokeInfoCalculator.getCpRangeAtLevel(poke,
                IVCombination.MAX, IVCombination.MAX, 40).getFloatingAvg();
        double thisCP = pokeInfoCalculator.getCpRangeAtLevel(poke,
                isr.getCombinationLowIVs(), isr.getCombinationHighIVs(), 40).getFloatingAvg();
        long roundedPerfection = Math.round(thisCP * 100.0 / perfectIvCp);
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
        return "This token calculates how close your monster is to its max potential, measured by CP. For example, if"
                + " a monster with max IVs maxes out at 2000cp, but your specific monster maxes out at 1900, then your "
                + "monster perfection is 95%, so this token returns 95.";
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
