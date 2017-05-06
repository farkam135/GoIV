package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

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
    public String getValue(IVScanResult ivs, PokeInfoCalculator pokeInfoCalculator) {
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
        return "ExtCPTier";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token gives you an idea of how powerful this monster can become, by measuring the maximum "
                + "possible CP the monster can obtain (considering the maximum possible IV from the scan) and "
                + "confronting it to the maximum CP of the most powerful IV 100 monster. Values are provided in the "
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
