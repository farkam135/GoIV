package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

/**
 * Created by Danilo Pianini.
 * A token which returns a "tier" based on the pokemon max cp, in the AA-ZZ range.
 */

public class ExtendedCpTierToken extends ClipboardToken {

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public ExtendedCpTierToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        final Pokemon poke = getRightPokemon(ivs.pokemon, pokeInfoCalculator);
        final IVCombination comb = ivs.getHighestIVCombination();
        if (comb == null) {
            return "??";
        }
        double cp = pokeInfoCalculator
                        .getCpRangeAtLevel(poke, comb, ivs.getHighestIVCombination(), 40)
                        .getFloatingAvg();
        return ExtendedTokenTierLogic.getRating(cp, pokeInfoCalculator);
    }

    @Override
    public String getPreview() {
        return "KJ";
    }

    @Override
    public String getTokenName(Context context) {
        return "ExtCPTier";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token gives you an idea of how powerful this pokemon can become, by measuring the maximum "
                + "possible CP the pokemon can obtain (considering the maximum possible IV from the scan) and "
                + "confronting it to the maximum CP of the most powerful IV 100 Pokemon. Values are provided in the "
                + "AA-ZZ range.";
    }

    @Override
    public String getCategory() {
        return "Evaluation Scores";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }
}
