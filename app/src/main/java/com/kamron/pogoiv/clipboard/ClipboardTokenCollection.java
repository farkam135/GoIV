package com.kamron.pogoiv.clipboard;

import com.kamron.pogoiv.clipboard.tokens.BaseStatToken;
import com.kamron.pogoiv.clipboard.tokens.CpPercentileToken;
import com.kamron.pogoiv.clipboard.tokens.CpTierToken;
import com.kamron.pogoiv.clipboard.tokens.HexIVToken;
import com.kamron.pogoiv.clipboard.tokens.HpToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageTokenMode;
import com.kamron.pogoiv.clipboard.tokens.LevelToken;
import com.kamron.pogoiv.clipboard.tokens.PerfectionCPPercentageToken;
import com.kamron.pogoiv.clipboard.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;
import com.kamron.pogoiv.clipboard.tokens.UnicodeToken;

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


        //Pokemon NAME//////////////////////////////////////
        tokens.add(new PokemonNameToken(false, 12)); //pokemon name max 12 characters
        tokens.add(new PokemonNameToken(true, 12)); //pokemon max evolution name max 12 characters
        tokens.add(new PokemonNameToken(false, 3)); //pokemon name max 3 characters
        tokens.add(new PokemonNameToken(true, 3)); //pokemon max evolution name max 3 characters
        tokens.add(new PokemonNameToken(false, 5)); //pokemon name max 5 characters
        tokens.add(new PokemonNameToken(true, 5));//pokemon max evolution name max 5 characters
        ////////////////////////////////////////////////


        //Basic stats///////////////////////////////////////////

        //level tokens
        tokens.add(new LevelToken(false, 0)); //level *2 representation of pokemon ex: 23
        tokens.add(new LevelToken(false, 1)); //level representation of pokemon no decimal ex: 11
        tokens.add(new LevelToken(false, 2)); //level  representation of pokemon ex: 11.5

        tokens.add(new HpToken(true, true));  //HP on max evolution, current level
        tokens.add(new HpToken(true, false)); //hp on max evolution, level 40
        tokens.add(new HpToken(false, true)); // hp on current evolution, current level
        tokens.add(new HpToken(false, false)); //hp on current evolution, level 40


        //stat tokens
        tokens.add(new BaseStatToken(false, 0, false)); //base evolution, all stats, dont invlude iv
        tokens.add(new BaseStatToken(false, 0, true)); //base evolution, all stats,  invlude iv
        tokens.add(new BaseStatToken(true, 0, false)); //max evolution, all stats, dont invlude iv
        tokens.add(new BaseStatToken(true, 0, true)); //max evolution, all stats,  invlude iv
        //////////////////////////////////////////////////////////////


        // Evaluating scores////////////////////////////////
        tokens.add(new CpTierToken(true)); //Pokemon max evolution  max level CP tier
        tokens.add(new CpTierToken(false)); //pokemon max level cp tier

        tokens.add(new CpPercentileToken(false));

        tokens.add(new PerfectionCPPercentageToken(true)); //how close your poke max evolved on lvl 40 cp is to 100% iv
        tokens.add(new PerfectionCPPercentageToken(false));//how close your poke on lvl 40 cp is to 100% iv
        ////////////////////////////////////////////////////////////

        //IV Info/////////////////////////////////////////////////////
        //Percentage
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MIN));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MAX));

        //Unicode iv representations
        tokens.add(new UnicodeToken(false)); //Unicode iv circled numbers not filled in ex ⑦⑦⑦
        tokens.add(new UnicodeToken(true));//Unicode iv circled numbers  filled in black ex ⓿⓿⓿
        tokens.add(new HexIVToken()); //hex representation of iv (ex A4B)
        /////////////////////////////////////////////////////////


        //Separators
        tokens.add(new SeparatorToken(" "));
        tokens.add(new SeparatorToken(","));
        tokens.add(new SeparatorToken("-"));
        tokens.add(new SeparatorToken("_"));
        tokens.add(new SeparatorToken("⚔"));
        tokens.add(new SeparatorToken("⛨"));
        tokens.add(new SeparatorToken("❤"));
        tokens.add(new SeparatorToken("IV"));
        tokens.add(new SeparatorToken("L"));
        tokens.add(new SeparatorToken("Lv"));
        tokens.add(new SeparatorToken("☢"));
        tokens.add(new SeparatorToken("%"));
        tokens.add(new SeparatorToken("( ͡° ͜ʖ ͡°)"));
        tokens.add(new SeparatorToken("r"));

        return tokens;
    }

}
