package com.kamron.pogoiv.clipboardlogic;

import com.kamron.pogoiv.clipboardlogic.tokens.BaseStatToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CPMaxToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CPMissingAtFourty;
import com.kamron.pogoiv.clipboardlogic.tokens.CPPercentageToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CandyTo40;
import com.kamron.pogoiv.clipboardlogic.tokens.CpPercentileToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CpTierToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomAppraiseSign;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomSeparatorToken;
import com.kamron.pogoiv.clipboardlogic.tokens.ExtendedCpTierToken;
import com.kamron.pogoiv.clipboardlogic.tokens.HasBeenAppraisedToken;
import com.kamron.pogoiv.clipboardlogic.tokens.HexIVToken;
import com.kamron.pogoiv.clipboardlogic.tokens.HpToken;
import com.kamron.pogoiv.clipboardlogic.tokens.IVPercentageToPerfectionToken;
import com.kamron.pogoiv.clipboardlogic.tokens.IVPercentageToken;
import com.kamron.pogoiv.clipboardlogic.tokens.IVPercentageTokenMode;
import com.kamron.pogoiv.clipboardlogic.tokens.IVSum;
import com.kamron.pogoiv.clipboardlogic.tokens.LevelToken;
import com.kamron.pogoiv.clipboardlogic.tokens.LevelUnicodeToken;
import com.kamron.pogoiv.clipboardlogic.tokens.MixedUnicodeToken;
import com.kamron.pogoiv.clipboardlogic.tokens.MovesetInitialsToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PerfectionCPPercentageToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PokeDexNumberToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PokemonGenderToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PowerupsToMaxToken;
import com.kamron.pogoiv.clipboardlogic.tokens.SeparatorToken;
import com.kamron.pogoiv.clipboardlogic.tokens.UnicodeToken;
import com.kamron.pogoiv.clipboardlogic.tokens.WorthTrainingToken;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-24.
 * <p>
 * <p>A class which keeps track of all the clipboardtokens that have been created
 * <p>
 * <p>Whenever a ClipboardToken is edited, the change needs to be reflected here.
 */

public class ClipboardTokenCollection {

    public static ArrayList<ClipboardToken> getSamples() {
        ArrayList<ClipboardToken> tokens = new ArrayList<>();


        //Pokemon NAME//////////////////////////////////////
        tokens.add(new CustomNameToken(true));
        tokens.add(new CustomNameToken(false));

        tokens.add(new PokeDexNumberToken(true));
        tokens.add(new PokeDexNumberToken(false));
        ////////////////////////////////////////////////


        //Basic stats///////////////////////////////////////////

        //level tokens
        tokens.add(new LevelToken(false, 0)); //level *2 representation of pokemon ex: 23
        tokens.add(new LevelToken(false, 1)); //level representation of pokemon no decimal ex: 11
        tokens.add(new LevelToken(false, 2)); //level  representation of pokemon ex: 11.5

        tokens.add(new LevelUnicodeToken(false));//level representation ex: ㉒½

        tokens.add(new PowerupsToMaxToken(false));//Powerups left to pokemon level 40

        tokens.add(new PokemonGenderToken(false, PokemonGenderToken.Type.SYMBOL)); // Gender symbol
        tokens.add(new PokemonGenderToken(false, PokemonGenderToken.Type.LETTER)); // Gender letter

        tokens.add(new HpToken(true, true));  //HP on max evolution, current level
        tokens.add(new HpToken(true, false)); //hp on max evolution, level 40
        tokens.add(new HpToken(false, true)); // hp on current evolution, current level
        tokens.add(new HpToken(false, false)); //hp on current evolution, level 40

        tokens.add(new CPMaxToken(true, true)); ///cp on max evolution, current level
        tokens.add(new CPMaxToken(true, false)); //cp on max evolution, level 40
        tokens.add(new CPMaxToken(false, true)); // cp on current evolution, current level
        tokens.add(new CPMaxToken(false, false)); //cp on current evolution, level 40


        tokens.add(new CPMissingAtFourty(true)); // cp missing on level 40 compared to perfect iv
        tokens.add(new CPMissingAtFourty(false)); // cp missing on level 40 compared to perfect iv


        //stat tokens
        tokens.add(new BaseStatToken(false, 0, false)); //base evolution, all stats, dont invlude iv
        tokens.add(new BaseStatToken(false, 0, true)); //base evolution, all stats,  invlude iv
        tokens.add(new BaseStatToken(true, 0, false)); //max evolution, all stats, dont invlude iv
        tokens.add(new BaseStatToken(true, 0, true)); //max evolution, all stats,  invlude iv

        tokens.add(new CandyTo40(false));
        //////////////////////////////////////////////////////////////


        // Evaluating scores////////////////////////////////
        tokens.add(new CpTierToken(true)); //Pokemon max evolution  max level CP tier
        tokens.add(new CpTierToken(false)); //pokemon max level cp tier
        tokens.add(new ExtendedCpTierToken(false)); // Max AA-ZZ cp tier
        tokens.add(new ExtendedCpTierToken(true)); // Same as above, max evolution
        tokens.add(new WorthTrainingToken(false, true)); // Max 00-99 stat evaluation
        tokens.add(new WorthTrainingToken(true, true)); // As above, max evolution

        tokens.add(new CpPercentileToken(false));

        tokens.add(new IVPercentageToPerfectionToken(false));

        tokens.add(new PerfectionCPPercentageToken(true)); //how close your poke max evolved on lvl 40 cp is to 100% iv
        tokens.add(new PerfectionCPPercentageToken(false));//how close your poke on lvl 40 cp is to 100% iv
        tokens.add(new CPPercentageToken(true)); //CP percent between 0% and 100% IV when max evolved at lvl 40
        tokens.add(new CPPercentageToken(false)); //CP percent between 0% and 100% IV at lvl 40
        ////////////////////////////////////////////////////////////

        //IV Info/////////////////////////////////////////////////////
        //Percentage
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MIN));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MAX));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MIN_SUP));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG_SUP));
        tokens.add(new IVPercentageToken(IVPercentageTokenMode.MAX_SUP));

        tokens.add(new CustomAppraiseSign());

        //Sum
        tokens.add(new IVSum(true));

        //Unicode iv representations
        tokens.add(new UnicodeToken(false)); //Unicode iv circled numbers not filled in ex ⑦⑦⑦
        tokens.add(new UnicodeToken(true));//Unicode iv circled numbers  filled in black ex ⓿⓿⓿
        tokens.add(new MixedUnicodeToken(false)); //Mixed Unicode IV, empty exact, filled multiple ex ⑦⓿⑦
        tokens.add(new MixedUnicodeToken(true)); //Mixed Unicode IV, filled exact, empty multiple ex ⓿⓿⑦
        tokens.add(new HexIVToken()); //hex representation of iv (ex A4B)
        /////////////////////////////////////////////////////////


        //Moveset//////////////////////////////////////
        tokens.add(new MovesetInitialsToken(1));
        tokens.add(new MovesetInitialsToken(2));


        //Separators
        tokens.add(new SeparatorToken("⚔"));
        tokens.add(new SeparatorToken("⛨"));
        tokens.add(new SeparatorToken("❤"));
        tokens.add(new SeparatorToken("☢"));
        tokens.add(new SeparatorToken("."));
        tokens.add(new CustomSeparatorToken());

        return tokens;
    }

}
