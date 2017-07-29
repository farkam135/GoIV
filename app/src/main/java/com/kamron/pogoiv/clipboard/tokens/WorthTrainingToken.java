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
import java.util.concurrent.Semaphore;

import static java.lang.Math.max;

/**
 * Created by Danilo Pianini.
 * A token which returns a "how worth is training tier" in the 00-99 range, considering the Pokémon stats and the IV values.
 */
public class WorthTrainingToken extends ClipboardToken {

    private static double MAX_DEF;
    private static double MAX_ATK;
    private static double MAX_HP;
    private static double BEST;
    private static final Semaphore MUTEX = new Semaphore(1);

    private final boolean best;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should search for the max evolution monster.
     * @param best true if the token should display the best combination, false for the worst.
     */
    public WorthTrainingToken(boolean maxEv, boolean best) {
        super(maxEv);
        this.best = best;
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivs, PokeInfoCalculator calc) {
        MUTEX.acquireUninterruptibly();
        if (MAX_HP == 0) {
            for (final Pokemon pokemon : calc.getPokedex()) {
                MAX_DEF = max(MAX_DEF, pokemon.baseDefense * 15);
                MAX_ATK = max(MAX_ATK, pokemon.baseAttack * 15);
                MAX_HP = max(MAX_HP, pokemon.baseStamina * 15);
            }
            for (final Pokemon pokemon : calc.getPokedex()) {
                BEST = max(BEST, formula(pokemon.baseAttack * 15, pokemon.baseDefense * 15, pokemon.baseStamina * 15));
            }
        }
        MUTEX.release();
        final IVCombination combination = best ? ivs.getHighestIVCombination() : ivs.getLowestIVCombination();
        if (combination == null) {
            return "??";
        }
        final int value = maxEv
                ? bestInEvolutionChain(ivs.pokemon, combination)
                : normalizedResult(ivs.pokemon, combination);
        return Integer.toString(value);
    }

    private static int normalizedResult(Pokemon p, IVCombination combination) {
        return (int) Math.round(normalize(
                        formula(p.baseAttack * combination.att,
                                p.baseDefense * combination.def,
                                p.baseStamina * combination.sta),
                        0, BEST) * 99);
    }

    private static double formula(final double a, final double d, final double h) {
        return Math.cbrt(normalize(a, 0, MAX_ATK)
                * normalize(d, 0, MAX_DEF)
                * normalize(h, 0, MAX_HP));
    }

    private static int bestInEvolutionChain(Pokemon pkm, IVCombination iv) {
        final Deque<Pokemon> toVisit = new LinkedList<>();
        toVisit.push(Objects.requireNonNull(pkm));
        int max = Integer.MIN_VALUE;
        while (!toVisit.isEmpty()) {
            final Pokemon pokemon = toVisit.pop();
            max = Math.max(max, normalizedResult(pokemon, iv));
            toVisit.addAll(pokemon.evolutions);
        }
        return max;
    }


    private static double normalize(final double v, final double min, final double max) { return (v - min) / (max -min); }

    @Override
    public String getPreview() {
        return "58";
    }

    @Override
    public String getTokenName(Context context) {
        return "Train-" + getType();
    }

    private String getType() {
        return best ? "best" : "worst";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This token returns an evaluation of how worth it is to train this monster, based both" +
                "on the base stats and the " + getType() + " possible IV stats." +
                "For instance, a 96% IV Alakazam will get a score higher than a 20% Tyranitar," +
                "regardless the fact that the maximum possible IV for the latter is higher:" +
                "since the probability of getting better Tyranitar is higher than the one of" +
                "getting better Alakazams, the second represents a much better stardust investment." +
                "Ranges in 00-99. Always returns two digits.";
    }

    @Override
    public String getCategory() {
        return "IV Info";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }

}
