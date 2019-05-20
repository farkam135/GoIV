package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Ryno on 2019-04-20.
 * Token representing the percentage the Pokemon's CP will be between 0% and 100%.
 */

public class CPPercentageToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CPPercentageToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(ScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(isr.pokemon, pokeInfoCalculator);
        double perfectIvCp = pokeInfoCalculator.getCpRangeAtLevel(poke,
                IVCombination.MAX, IVCombination.MAX, 40).getFloatingAvg();
        double worstIvCp = pokeInfoCalculator.getCpRangeAtLevel(poke,
                IVCombination.MIN, IVCombination.MIN, 40).getFloatingAvg();
        double thisCP = pokeInfoCalculator.getCpRangeAtLevel(poke,
                isr.getCombinationLowIVs(), isr.getCombinationHighIVs(), 40).getFloatingAvg();
        long roundedPerfection = Math.round(((thisCP - worstIvCp) / (perfectIvCp - worstIvCp)) * 100);
        return String.valueOf(roundedPerfection);
    }

    @Override
    public String getPreview() {
        return "93";
    }

    @Override
    public String getTokenName(Context context) {
        return "CP%";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_cp_percentage);
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
