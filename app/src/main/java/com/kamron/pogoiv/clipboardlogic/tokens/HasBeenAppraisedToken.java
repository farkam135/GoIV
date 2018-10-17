package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2018-09-29.
 */

public class HasBeenAppraisedToken extends ClipboardToken {


    private String appraiseSymbol, notAppraiseSymbol;
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public HasBeenAppraisedToken(boolean maxEv, String appraiseSymbol, String notAppraisedSymbol) {
        super(maxEv);
        this.appraiseSymbol = appraiseSymbol;
        this.notAppraiseSymbol=notAppraisedSymbol;
    }

    @Override public int getMaxLength() {
        return 1;
    }

    @Override public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        if (scanResult.getHasBeenAppraised()){
            return appraiseSymbol;
        }
        return notAppraiseSymbol;
    }

    @Override public String getPreview() {
        if (appraiseSymbol != null){
            return appraiseSymbol;
        }
        return "A";
    }

    @Override public String getTokenName(Context context) {
        return "IsAppr";
    }

    @Override public String getLongDescription(Context context) {
        return "A token which returns a symbol  indicating if the pokemon was evaluated with appraisal info."
                + " The pokemon is also considered appraised if only one IV combination is possible.";
    }

    @Override public Category getCategory() {
        return Category.IV_INFO;
    }

    @Override
    public String getStringRepresentation() {
        return "." + this.getClass().getSimpleName() + appraiseSymbol + notAppraiseSymbol;
    }

    @Override public boolean changesOnEvolutionMax() {
        return false;
    }
}
