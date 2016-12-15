package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * Represents the lowest possible IV combination with ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮
 */

public class UnicodeToken extends ClipboardToken {
    String[] unicode_0_15 = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮"};
    String[] unicode_0_15filled = {"⓿", "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "❿", "⓫", "⓬", "⓭", "⓮", "⓯"};
    boolean filled;

    public UnicodeToken(boolean filled) {
        super(false);
        this.filled = filled;
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        IVCombination lowestIVCombination = ivScanResult.getLowestIVCombination();
        if (lowestIVCombination == null) {
            return "";
        }
        int att = lowestIVCombination.att;
        int def = lowestIVCombination.def;
        int sta = lowestIVCombination.sta;
        String[] toUse = filled ? unicode_0_15filled : unicode_0_15;

        return toUse[att] + toUse[def] + toUse[sta];
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(filled);
    }

    @Override
    public String getPreview() {
        return filled ? "❾⓬❶" : "⑨⑫①";
    }

    @Override
    public String getTokenName(Context context) {
        return "UIV";
    }

    @Override
    public String getLongDescription(Context context) {
        String returner = context.getString(R.string.clipboard_token_unicode_description);

        if (filled) {
            returner = String.format(context.getString(R.string.clipboard_token_unicode_filled_description), returner);
        } else {
            returner = String.format(context.getString(R.string.clipboard_token_unicode_empty_description), returner);
        }
        return returner;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.clipboard_token_category_iv_info);
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
