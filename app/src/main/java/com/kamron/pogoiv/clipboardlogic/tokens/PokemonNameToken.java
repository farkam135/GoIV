package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2016-09-24.
 * A token which returns the name of the scanned pokemon
 */

public class PokemonNameToken extends ClipboardToken {

    private boolean evolvedPreview;
    private int maxLength;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv     true if the token should change its logic to pretending the pokemon is fully evolved.
     * @param maxLength How long the poke name is allowed to be before it's cut off.
     */
    public PokemonNameToken(boolean maxEv, int maxLength) {
        super(maxEv);
        evolvedPreview = maxEv;
        this.maxLength = maxLength;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(scanResult.pokemon, pokeInfoCalculator);
        return getCappedLength(poke.name);
    }

    /**
     * Get the capped pokemon name. For example, blastoise with cap 4 would return blas.
     * If input string is shorted than cap, returns input string.
     *
     * @param s Which string to cap.
     * @return A string which might have become shorter.
     */
    private String getCappedLength(String s) {
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength);
    }

    @Override
    public String getPreview() {
        //Long strings so users can see full preview of how long the text strings can be.
        //Might not be neccessary as the longest name an english pokemon currently has is 11 characters. (Fletchinder)
        //However this should not be assumed because english is not the only language.
        return evolvedPreview ? getCappedLength("MachampWearsPantsToHideHisEnormousBellyButton") :
                getCappedLength("MachopHasATailButHisEvolutionsDoesNotForSomeReason");
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(evolvedPreview) + "_" + String.valueOf(maxLength);
    }

    @Override
    public String getTokenName(Context context) {
        if (maxLength < 12) {
            return context.getString(R.string.token_pokemonname) + maxLength;
        }
        return context.getString(R.string.token_pokemonname);
    }

    @Override
    public String getLongDescription(Context context) {
        String num = maxLength + "";
        String pokeName = maxEv ? "Dragonite" : "Dratini";
        String returner = context.getString(R.string.token_msg_pkmName_msg1) + getCappedLength(pokeName) + ".";

        if (maxLength < 12) {
            returner += context.getString(R.string.token_msg_pkmName_msg2) + num + context.getString(R.string
                    .token_msg_pkmName_msg3);
        }
        return returner;
    }

    @Override
    public Category getCategory() {
        return Category.NAME;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }
}
