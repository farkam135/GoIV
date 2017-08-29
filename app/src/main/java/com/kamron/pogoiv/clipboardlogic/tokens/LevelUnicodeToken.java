package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;

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
        return "This token shows the level of the monster as one or two unicode tokens, the first to show the whole "
                + "level such as ⑰, and if the monster is half-leveled, also adds a ½.";
    }

    @Override
    public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
