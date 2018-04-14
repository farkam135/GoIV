package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

/**
 * Created by Johan on 2016-09-28.
 * Token which represents a pokemons stats.
 */
public class BaseStatToken extends ClipboardToken {

    private final boolean includeIV;
    private final int mode;

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv     true if the token should change its logic to pretending the pokemon is fully evolved.
     * @param mode      0 for all stats, 1 for attack, 2 for defence, 3 for stamina
     * @param includeIV whether to include the iv with the stat or not
     */
    public BaseStatToken(boolean maxEv, int mode, boolean includeIV) {
        super(maxEv);
        this.mode = mode;
        this.includeIV = includeIV;
    }

    @Override
    public int getMaxLength() {
        if (mode == 0) {
            return 11;
        }
        return 3;
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(mode) + String.valueOf(includeIV);
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(scanResult.pokemon, pokeInfoCalculator);
        int bAtt = poke.baseAttack;
        int bDef = poke.baseDefense;
        int bSta = poke.baseStamina;
        int ivAtt = bAtt + scanResult.getIVAttackLow();
        int ivDef = bDef + scanResult.getIVDefenseLow();
        int ivSta = bSta + scanResult.getIVStaminaLow();

        if (includeIV) {
            if (mode == 0) {
                return ivAtt + " " + ivDef + " " + ivSta;
            } else if (mode == 1) {
                return ivAtt + "";
            } else if (mode == 2) {
                return ivDef + "";
            }
            return ivSta + "";
        } else {
            if (mode == 0) {
                return bAtt + " " + bDef + " " + bSta;
            } else if (mode == 1) {
                return bAtt + "";
            } else if (mode == 2) {
                return bDef + "";
            }
            return bSta + "";
        }

    }

    @Override
    public String getPreview() {
        if (includeIV) {
            if (mode == 0) {
                return "131 205 139";
            } else if (mode == 1) {
                return "131";
            } else if (mode == 2) {
                return "205";
            }
            return "139";
        } else {
            if (mode == 0) {
                return "130 200 130";
            } else if (mode == 1) {
                return "190";
            } else if (mode == 2) {
                return "210";
            }
            return "150";
        }

    }

    @Override
    public String getTokenName(Context context) {
        if (includeIV) {
            if (mode == 0) {
                return "stat+iv";
            } else if (mode == 1) {
                return "att+iv";
            } else if (mode == 2) {
                return "def+iv";
            }
            return "Base sta + iv";
        } else {
            if (mode == 0) {
                return "stats";
            } else if (mode == 1) {
                return "att";
            } else if (mode == 2) {
                return "def";
            }
            return "sta";
        }

    }

    @Override
    public String getLongDescription(Context context) {
        String returner = context.getString(R.string.token_msg_basStat_msg1);
        if (includeIV) {
            returner += context.getString(R.string.token_msg_basStat_msg2);
        }
        if (mode != 0) {
            returner += context.getString(R.string.token_msg_basStat_msg3);
        }
        return returner;
    }

    @Override
    public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }
}
