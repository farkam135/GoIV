package com.kamron.pogoiv.UserClipboard;

import com.kamron.pogoiv.UserClipboard.ClipboardTokens.CpTierToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.IVAvgPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.IVMaxPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.IVMinPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.CPTierMaxEvolutionToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PerfectionLastEvPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PerfectionPercentageToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PokemonNameToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.SeperatorToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.UnicodeLowIVNumberToken;

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
        tokens.add(new PokemonNameToken());
        tokens.add(new CpTierToken());
        tokens.add(new CPTierMaxEvolutionToken());
        tokens.add(new IVMaxPercentageToken());
        tokens.add(new IVMinPercentageToken());
        tokens.add(new IVAvgPercentageToken());
        tokens.add(new PerfectionPercentageToken());
        tokens.add(new PerfectionLastEvPercentageToken());
        tokens.add(new UnicodeLowIVNumberToken());
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
