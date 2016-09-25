package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

import android.content.Context;

import com.kamron.pogoiv.UserClipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-25.
 * Represents the lowest possible IV combination with ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮
 */

public class UnicodeLowIVNumberToken extends ClipboardToken {
    String[] unicodeSymbol = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮"};

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        String returner = "";
        int att = ivScanResult.getLowestIVCombination().att;
        int def = ivScanResult.getLowestIVCombination().def;
        int sta = ivScanResult.getLowestIVCombination().sta;

        returner += unicodeSymbol[att];
        returner += unicodeSymbol[def];
        returner += unicodeSymbol[sta];
        return returner;
    }

    @Override
    public String getPreview() {
        return "⑨⑫①";
    }

    @Override
    public String getTokenName(Context context) {
        return "Compresssed Lowest IV score";
    }
}
