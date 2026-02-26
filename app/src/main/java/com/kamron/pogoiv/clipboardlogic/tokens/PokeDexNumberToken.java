package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2018-09-29.
 */

public class PokeDexNumberToken  extends ClipboardToken{


    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public PokeDexNumberToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(scanResult.pokemon, pokeInfoCalculator);
        String dexNumber = poke.number+1+"";
        return dexNumber;
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation();
    }

    @Override
    public String getPreview() {
        int dexNum = 60;
        if (maxEv) {
            dexNum = 63;
        }
        return String.valueOf(dexNum);
    }

    @Override
    public String getTokenName(Context context) {
        return "Dex#";
    }

    @Override
    public String getLongDescription(Context context) {
        String returner = "The pokemons pokedex number. For example, bulbasaur is 1. Pikachu is 25.";
        if (maxEv){
            returner += " This is the max ev variant, which return the pokedex number of one of the max evolutions "
                    + "for the pokemon.";
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
