package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonBase;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * Created by Danilo Pianini.
 * A token which returns a "tier" based on the pokemon max cp, in the AA-ZZ range.
 */

public class ExtendedCpTierToken extends ClipboardToken {

    private static final IVCombination IV_PERFECT = new IVCombination(15, 15, 15);
    private static final Semaphore MUTEX = new Semaphore(1);
    private static final List<String> RATINGS;
    private static double CP_MAX = -1;

    static {
        final List<String> ratings = new ArrayList<>();
        for (char a = 'A'; a <= 'Z'; a++) {
            for (char b = 'A'; b <= 'Z'; b++) {
                ratings.add("" + a + b);
            }
        }
        RATINGS = Collections.unmodifiableList(ratings);
    }

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
                .getCpRangeAtLevel(pokemon, iv, iv, Data.MAXIMUM_POKEMON_LEVEL)
                .getFloatingAvg();
    }

    private static double computeMaxEvolvedCP(Pokemon pkm, IVCombination iv, PokeInfoCalculator pokeInfoCalculator) {
        final Deque<Pokemon> toVisit = new LinkedList<>();
        toVisit.push(Objects.requireNonNull(pkm));
        double max = Double.NEGATIVE_INFINITY;
        while (!toVisit.isEmpty()) {
            final Pokemon pokemon = toVisit.pop();
            max = Math.max(max, computeBestCP(pokemon, iv, pokeInfoCalculator));
            toVisit.addAll(pokemon.getEvolutions());
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
        return getRating(cp, pokeInfoCalculator);
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

    private static String getRating(final double combatPower, final PokeInfoCalculator calc) {
        MUTEX.acquireUninterruptibly();
        if (CP_MAX == -1) {
            initCPMax(calc);
        }
        MUTEX.release();
        final int ratingIndex = (int) Math.floor(Math.max(combatPower, 1) * (RATINGS.size() - 1) / CP_MAX);
        return RATINGS.get(ratingIndex);
    }

    private static void initCPMax(final PokeInfoCalculator calc) {
        int maxAtt = 0;
        int maxDef = 0;
        int maxSta = 0;
        for (final PokemonBase pokemonBase: calc.getPokedex()) {
            for (final Pokemon pokemon: pokemonBase.forms) {
                if (pokemon.baseAttack <= maxAtt
                        && pokemon.baseDefense <= maxDef
                        && pokemon.baseStamina <= maxSta) {
                    continue; // Skip this Pokémon since it can't have higher CP than the current computed max
                }
                double currentCP = calc
                        .getCpRangeAtLevel(pokemon, IV_PERFECT, IV_PERFECT, Data.MAXIMUM_POKEMON_LEVEL)
                        .getFloatingAvg();
                if (currentCP > CP_MAX) {
                    CP_MAX = currentCP;
                    maxAtt = pokemon.baseAttack;
                    maxDef = pokemon.baseDefense;
                    maxSta = pokemon.baseStamina;
                }
            }
        }
    }
}
