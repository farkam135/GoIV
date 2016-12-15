package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-11-19.
 * <p>
 * A token which represents the pokemon level as unicode plus half unicode symbols
 */

public class LevelUnicodeToken extends ClipboardToken {

    String half = "½";
    private String[] unicodes = {"0", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪",
            "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳", "㉑", "㉒", "㉓", "㉔", "㉕", "㉖", "㉗", "㉘", "㉙",
            "㉚", "㉛", "㉜", "㉝", "㉞", "㉟", "㊱", "㊲", "㊳", "㊴", "㊵", "㊶", "㊷", "㊸", "㊹", "㊺", "㊻", "㊼",
            "㊽", "㊾", "㊿"};

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public LevelUnicodeToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        String returner = unicodes[(int) ivScanResult.estimatedPokemonLevel];
        if (ivScanResult.estimatedPokemonLevel % 1 == 0.5) {
            returner += half;
        }
        return returner;
    }

    @Override
    public String getPreview() {
        return "㉒½";
    }

    @Override
    public String getTokenName(Context context) {
        return "UniLevel";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.clipboard_token_levelunicode_description);
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.clipboard_token_category_basic_stats);
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
