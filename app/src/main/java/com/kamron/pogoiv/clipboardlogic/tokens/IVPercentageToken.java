package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

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
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        Integer percent = null;
        IVCombination combination = null;
        switch (mode) {
            case MIN:
            case MIN_SUP:
                combination = scanResult.getLowestIVCombination();
                break;
            case AVG:
            case AVG_SUP:
                percent = scanResult.getIVPercentAvg();
                break;
            case MAX:
            case MAX_SUP:
                combination = scanResult.getHighestIVCombination();
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
                modeText = context.getString(R.string.token_msg_ivPerc_min);
                break;
            case MIN_SUP:
                modeText = context.getString(R.string.token_msg_ivPerc_minSup);
                break;
            case AVG:
                modeText = context.getString(R.string.token_msg_ivPerc_avg);
                break;
            case AVG_SUP:
                modeText = context.getString(R.string.token_msg_ivPerc_avgSup);
                break;
            case MAX:
                modeText = context.getString(R.string.token_msg_ivPerc_max);
                break;
            case MAX_SUP:
                modeText = context.getString(R.string.token_msg_ivPerc_maxSup);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return context.getString(R.string.token_msg_ivPerc_msg1) + modeText + context.getString(R.string
                .token_msg_ivPerc_msg2);
    }

    @Override
    public Category getCategory() {
        return Category.IV_INFO;
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
