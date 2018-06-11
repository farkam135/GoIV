package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

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

    private static double computeBestCP(Pokemon pokemon, IVCombination iv, PokeInfoCalculator pokeInfoCalculator) {
        return pokeInfoCalculator
                .getCpRangeAtLevel(pokemon, iv, iv, 40)
                .getFloatingAvg();
    }

    private static double computeMaxEvolvedCP(Pokemon pkm, IVCombination iv, PokeInfoCalculator pokeInfoCalculator) {
        final Deque<Pokemon> toVisit = new LinkedList<>();
        toVisit.push(Objects.requireNonNull(pkm));
        double max = Double.NEGATIVE_INFINITY;
        while (!toVisit.isEmpty()) {
            final Pokemon pokemon = toVisit.pop();
            max = Math.max(max, computeBestCP(pokemon, iv, pokeInfoCalculator));
            toVisit.addAll(pokemon.evolutions);
        }
        return max;
    }

    @Override
    public String getValue(ScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
        final IVCombination bestCombination = Objects.requireNonNull(ivs).getHighestIVCombination();
        if (bestCombination == null) {
            return "??";
        }
        final double cp = maxEv
                ? computeMaxEvolvedCP(ivs.pokemon, bestCombination, pokeInfoCalculator)
                : computeBestCP(ivs.pokemon, bestCombination, pokeInfoCalculator);
        return ExtendedTokenTierLogic.getRating(cp, pokeInfoCalculator);
    }

    @Override
    public String getPreview() {
        return "KJ";
    }

    @Override
    public String getTokenName(Context context) {
        return "Ext" + context.getString(R.string.cp) + context.getString(R.string.token_msg_tier);
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_extendedCP);
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
