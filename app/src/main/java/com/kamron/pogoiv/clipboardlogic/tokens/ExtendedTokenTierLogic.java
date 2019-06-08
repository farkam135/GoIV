package com.kamron.pogoiv.clipboardlogic.tokens;

import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Danilo Pianini.
 * A class which translates a CP value to a tier string in the AA-ZZ range
 */
public final class ExtendedTokenTierLogic {

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

    private ExtendedTokenTierLogic() {
    }

    /**
     * Get a string representation of a pokemon rating, for example "AB" or "TG".
     *
     * @param combatPower the general combatPower to translate to a tier string.
     * @return A string of 2 characters between AA and ZZ.
     */
    public static String getRating(final double combatPower, final PokeInfoCalculator calc) {
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
        for (final Pokemon pokemon: calc.getPokedexForms()) {
            if (pokemon.baseAttack < maxAtt
                    && pokemon.baseDefense < maxDef
                    && pokemon.baseStamina < maxSta) {
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
