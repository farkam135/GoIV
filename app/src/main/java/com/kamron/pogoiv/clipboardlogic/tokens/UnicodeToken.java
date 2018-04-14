package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

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
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        IVCombination lowestIVCombination = scanResult.getLowestIVCombination();
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
        return filled ? "UIV-❻" : "UIV-②";
    }

    @Override
    public String getLongDescription(Context context) {
        String returner = context.getString(R.string.token_msg_uniToken_msg1);

        if (filled) {
            returner += context.getString(R.string.token_msg_uniToken_msg2);
        } else {

            returner += context.getString(R.string.token_msg_uniToken_msg3);
        }
        return returner;
    }

    @Override
    public Category getCategory() {
        return Category.IV_INFO;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
