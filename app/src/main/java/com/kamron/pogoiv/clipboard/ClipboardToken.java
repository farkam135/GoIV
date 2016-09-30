package com.kamron.pogoiv.clipboard;

import android.content.Context;

import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-24.
 * An interface which lists the methods required for tokens that users can use to build custom clipboard snippets on
 * scan results.
 * If you implement a concrete version of a token, remember to add it to the ClipboardTokenCollection class. This is
 * not an ideal implementation, so if anyone's got time for a cleaner implementation, feel free to discuss it on github.
 */

public abstract class ClipboardToken {

    public boolean maxEv; // if the token should change to accomodate to the last in the evolution line

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv true if the token should change its logic to pretending the pokemon is fully evolved.
     */
    public ClipboardToken(boolean maxEv) {
        this.maxEv = maxEv;
    }

    /**
     * What's the longest possible output that this token can produce?
     * For example, if the token outputs a number between 0 and 150, the maximum length would be 3.
     *
     * @return An integer representing the biggest possible size of the output.
     */
    public abstract int getMaxLength();

    /**
     * Get the result as interpreted by this clipboardtoken
     *
     * @param ivScanResult       Information that can be used to calculate the output for the token.
     * @param pokeInfoCalculator Information that can be used to calculate the output for the token.
     * @return A string representing the value as interpreted by the clipboardToken
     */
    public abstract String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator);

    /**
     * Get a String which represents an example of normal output from this token. For example if the token returns a
     * random number between 0 and 150, "133" would be a good normal output.
     *
     * @return An example output that the token could produce, used to preview how the token info would look.
     */
    public abstract String getPreview();

    /**
     * Get the string representation of how this token is saved in the persistent memory.
     * The string must be unique for each token, and start with a "."
     * No period is allowed to be in the string other than the initial sign.
     *
     * @return A string representing this token as saved in persistent memory setting.
     */
    public String getStringRepresentation() {
        String forEvolution = maxEv ? "MaxEv" : "Base";
        return "." + this.getClass().getSimpleName() + forEvolution;
    }

    /**
     * Get the last evolution in an evolution chain of a pokemon, unless the pokemon does not have an evolution.
     * Handles edge case Vaporeon etc by not checking for last evolution index if the pokemon does not have an
     * evolution.
     *
     * @param poke               Poke to base logic on
     * @param pokeInfoCalculator The calculator used to get the evolution line.
     * @return The last pokemon in an evolution line.
     */
    private Pokemon getLastEv(Pokemon poke, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon lastEv;
        //If-else below exists to manage scenario where pokemon has multiple evolution possibilities, so for example
        //If you scan a vaporeon, it has no evolutions, so there's no need to go to the bottom of the evolution chain
        // and find jolteon.. or whatever eeveelution is last.
        if (poke.evolutions.size() != 0) {
            ArrayList<Pokemon> evLine = pokeInfoCalculator.getEvolutionLine(poke);
            lastEv = evLine.get(evLine.size() - 1);
        } else {
            lastEv = poke;
        }

        return lastEv;
    }

    /**
     * Get a pokemon that is either the scanned pokemon, or the last evolution in the line, depending on the token
     * setting.
     *
     * @param poke The pokemon to use, or to find the final evolution of.
     * @param pic  The calculator which can find the last evolution of a pokemon.
     * @return Either the same pokemon sent in, or the last evolution, depending on the token setting.
     */
    public Pokemon getRightPokemon(Pokemon poke, PokeInfoCalculator pic) {
        return maxEv ? getLastEv(poke, pic) : poke;
    }

    /**
     * Get what the short name of the token is, for example "Name".
     *
     * @param context used to reach the string resources
     * @return The short name of the token
     */
    public abstract String getTokenName(Context context);

    /**
     * Get the long description which the users sees when he's selected a token.
     *
     * @param context used to get translation string resources
     * @return A string which explains the token, with an example.
     */
    public abstract String getLongDescription(Context context);

}
