package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which represents the IV % that is possible considering what's known about the pokemon.
 * Depending on what's sent in in the constructor, it represents the minimum, average or max iv.
 */

public class IVPercentageToken extends ClipboardToken {
    private static final char[] SUP_DIGITS = new char[] {'⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'};

    private IVPercentageTokenMode mode;

    public IVPercentageToken(IVPercentageTokenMode mode) {
        super(false);
        this.mode = mode;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        Integer percent = null;
        IVCombination combination = null;
        switch (mode) {
            case MIN:
            case MIN_SUP:
                combination = ivScanResult.getLowestIVCombination();
                break;
            case AVG:
            case AVG_SUP:
                percent = ivScanResult.getAveragePercent();
                break;
            case MAX:
            case MAX_SUP:
                combination = ivScanResult.getHighestIVCombination();
                break;
            default:
                throw new IllegalArgumentException();
        }

        if (combination != null) {
            percent = combination.percentPerfect;
        }

        if (percent == null) {
            return "";
        }

        final String percentString = String.valueOf(percent);
        if (mode.isSuperscript()) {
            return toSuperscript(percentString);
        } else {
            return percentString;
        }
    }

    @Override
    public String getPreview() {
        final String example = String.valueOf(95 + mode.ordinal());

        if (mode.isSuperscript()) {
            return toSuperscript(example);
        } else {
            return example;
        }
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(mode);
    }

    @Override
    public String getTokenName(Context context) {
        switch (mode) {
            case MIN:
                return "min%";
            case MIN_SUP:
                return "min%sup";
            case AVG:
                return "avg%";
            case AVG_SUP:
                return "avg%sup";
            case MAX:
                return "max%";
            case MAX_SUP:
                return "max%sup";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getLongDescription(Context context) {
        final String modeText;
        switch (mode) {
            case MIN:
                modeText = "minimum";
                break;
            case MIN_SUP:
                modeText = "superscript minimum";
                break;
            case AVG:
                modeText = "average";
                break;
            case AVG_SUP:
                modeText = "superscript average";
                break;
            case MAX:
                modeText = "maximum";
                break;
            case MAX_SUP:
                modeText = "superscript maximum";
                break;
            default:
                throw new IllegalArgumentException();
        }

        return "Get the " + modeText + " percent of the IV possibilities. If only one iv combination is "
                + "possible, minimum, average and maximum will be the same."
                + " For example, if the iv range is 55-75, the minimum will return 55, the average will return "
                + "something between 55 and 75, and the maximum will return 75.";
    }

    @Override
    public String getCategory() {
        return "IV Info";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }

    private String toSuperscript(String percent) {
        final StringBuilder resultBuilder = new StringBuilder(3);
        for (int i = 0; i < percent.length(); i++) {
            resultBuilder.append(SUP_DIGITS[percent.charAt(i) - '0']);
        }
        return resultBuilder.toString();
    }
}
