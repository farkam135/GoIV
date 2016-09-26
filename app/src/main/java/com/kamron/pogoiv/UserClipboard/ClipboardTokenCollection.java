package com.kamron.pogoiv.UserClipboard;

import com.kamron.pogoiv.UserClipboard.ClipboardTokens.CpTierToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.HexIVToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.HpToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.IVPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PerfectionCPPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PokemonNameToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.SeperatorToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.UnicodeToken;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-24.
 *
 * <p>A class which keeps track of all the clipboardtokens that have been created
 *
 * <p>Whenever a ClipboardToken is edited, the change needs to be reflected here.
 */

public class ClipboardTokenCollection {

    public static ArrayList<ClipboardToken> getSamples() {
        ArrayList<ClipboardToken> tokens = new ArrayList<>();

        tokens.add(new PokemonNameToken(false, 12));
        tokens.add(new PokemonNameToken(false, 5));
        tokens.add(new PokemonNameToken(true, 5));
        tokens.add(new PokemonNameToken(true, 12));

        tokens.add(new CpTierToken(true));
        tokens.add(new CpTierToken(false));

        tokens.add(new HpToken(true, true));
        tokens.add(new HpToken(true, false));
        tokens.add(new HpToken(false, true));
        tokens.add(new HpToken(false, false));

        //Percentage
        tokens.add(new IVPercentageToken("Minimum"));
        tokens.add(new IVPercentageToken("Average"));
        tokens.add(new IVPercentageToken("Max"));


        tokens.add(new PerfectionCPPercentageToken(true));
        tokens.add(new PerfectionCPPercentageToken(false));

        //Unicode iv representations
        tokens.add(new UnicodeToken(false, false));
        tokens.add(new UnicodeToken(false, true));
        tokens.add(new HexIVToken(false));

        //Seperators
        tokens.add(new SeperatorToken(" "));
        tokens.add(new SeperatorToken(","));
        tokens.add(new SeperatorToken("-"));
        tokens.add(new SeperatorToken("_"));
        tokens.add(new SeperatorToken("%"));
        tokens.add(new SeperatorToken("( ͡° ͜ʖ ͡°)"));
        tokens.add(new SeperatorToken("r"));

        return tokens;
    }

}
