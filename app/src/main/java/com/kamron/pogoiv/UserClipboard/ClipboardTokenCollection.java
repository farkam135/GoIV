package com.kamron.pogoiv.UserClipboard;

import com.kamron.pogoiv.UserClipboard.ClipboardTokens.CpTierToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.MaxEvolutionCPTierToken;
import com.kamron.pogoiv.UserClipboard.ClipboardTokens.PokemonNameToken;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-24.
 *
 * A class which keeps track of all the clipboardtokens that have been created
 *
 * Whenever a ClipboardToken is edited, the change needs to be reflected here.
 */

public class ClipboardTokenCollection {

    public static ArrayList<ClipboardToken> getSamples(){
        ArrayList<ClipboardToken> tokens = new ArrayList<>();
        tokens.add(new PokemonNameToken());
        tokens.add(new CpTierToken());
        tokens.add(new MaxEvolutionCPTierToken());

        return tokens;
    }

}
