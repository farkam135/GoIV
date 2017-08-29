package com.kamron.pogoiv.clipboardlogic.tokens;

import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static java.lang.Math.max;

/**
 * Created by Danilo Pianini.
 * A class which translates a CP value to a tier string in the AA-ZZ range
 */

public final class ExtendedTokenTierLogic {

    private static final IVCombination MAXIVCOMB = new IVCombination(15, 15, 15);
    private static final int MAXLEVEL = 40;
    private static final Semaphore MUTEX = new Semaphore(1);
    private static final List<String> RATINGS;
    private static double MAX_IV;

    static {
        final char[] alphabet = "abcdefghijklmnopqrstuvwxyz"
                .toUpperCase(Locale.ENGLISH)
                .toCharArray();
        final List<String> ratings = new ArrayList<>();
        for (final char a: alphabet) {
            for (final char b: alphabet) {
                ratings.add(Character.toString(a) + Character.toString(b));
            }
        }
        RATINGS = Collections.unmodifiableList(ratings);
    }

    private ExtendedTokenTierLogic() {
    }

    /**
     * Get a string representation of a pokemon rating, for example "A" or "B+".
     *
     * @param combatPower the general combatPower to translate to a tier string.
     * @return A string S,A,B,C,D which might have a plus or minus after.
     */
    public static String getRating(final double combatPower, final PokeInfoCalculator calc) {
        MUTEX.acquireUninterruptibly();
        if (MAX_IV == 0) {
            for (final Pokemon pokemon: calc.getPokedex()) {
                MAX_IV = max(MAX_IV, calc.getCpRangeAtLevel(pokemon, MAXIVCOMB, MAXIVCOMB, MAXLEVEL).getFloatingAvg());
            }
        }
        MUTEX.release();
        final int ratingIndex = (int) Math.round(Math.max(combatPower, 1) * (RATINGS.size() - 1) / MAX_IV);
        return RATINGS.get(ratingIndex);
    }
}
