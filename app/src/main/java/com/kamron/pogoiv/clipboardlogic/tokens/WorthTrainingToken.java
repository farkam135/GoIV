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
import java.util.concurrent.Semaphore;

import static java.lang.Math.max;

/**
 * Created by Danilo Pianini.
 * A token which returns a "how worth is training tier" in the 00-99 range, considering the Pokémon stats and the IV
 * values.
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
    public String getValue(ScanResult ivs, PokeInfoCalculator calc) {
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

    private static double normalize(final double v, final double min, final double max) {
        return (v - min) / (max - min);
    }

    @Override
    public String getPreview() {
        return "58";
    }

    @Override
    public String getTokenName(Context context) {
        return context.getString(R.string.token_msg_train, getType(context));
    }

    private String getType(Context context) {
        return best ? context.getString(R.string.token_msg_best) : context.getString(R.string.token_msg_worst);
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_worthTra_msg, getType(context));
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
