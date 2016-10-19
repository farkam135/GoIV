package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * A token which just represents a static non changing result
 */

public class SeparatorToken extends ClipboardToken {
    private String string;

    public SeparatorToken(String s) {
        super(false);
        this.string = s;
    }

    @Override
    public int getMaxLength() {
        return string.length();
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        return string;
    }

    @Override
    public String getPreview() {
        return string;
    }

    @Override
    public String getTokenName(Context context) {
        return string;
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_separator) + " " + string;
    }

    @Override
    public String getCategory() {
        return "Separators";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }

    @Override
    public String getStringRepresentation() {
        if (string.contains(".")) {
            return ".DotSeparator"; //edge case where the string contains something that'd break the way tokens are
            // stored and retrieved from memory.
        }
        //normal case
        return "." + this.getClass().getSimpleName() + string;
    }
}
