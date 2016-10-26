package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-10-06.
 * A token which says -compared to all possible iv combinations- how rare is an iv combination this good?
 */

public class CpPercentileToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public CpPercentileToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 4;
    }

    @Override
    public String getValue(IVScanResult isr, PokeInfoCalculator pokeInfoCalculator) {
        int thisResult = isr.lowAttack + isr.lowDefense + isr.lowStamina;
        int totalCombinations = 16 * 16 * 16; //3 ivs between 0 to 15.
        int[] countingArray = new int[46]; //Array which keeps track of how many combinations result in [i] total ivs.
        for (int att = 0; att <= 15; att++) {
            for (int def = 0; def <= 15; def++) {
                for (int sta = 0; sta <= 15; sta++) {
                    countingArray[att + def + sta]++;
                    if (att + def + sta > thisResult) { //stop counting higher if we know we found our result.
                        break;
                    }
                }
            }
        }
        int lowerCombinations = 0;
        for (int i = 0; i < thisResult; i++) {
            lowerCombinations += countingArray[i];
        }

        double percent = 1d - (double) lowerCombinations / (double) totalCombinations;
        percent = percent * 100; //convert example 0.01 to 10.0
        int roundedPercent = (int) (percent + 0.5);
        return String.valueOf(roundedPercent);
    }

    @Override
    public String getPreview() {
        return "5.1";
    }

    @Override
    public String getTokenName(Context context) {
        return "IV %top";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_cp_percentile);
    }

    @Override
    public String getCategory() {
        return "Evaluation Scores";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
