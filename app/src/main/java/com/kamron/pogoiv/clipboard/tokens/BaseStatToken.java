package com.kamron.pogoiv.clipboard.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;

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
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon poke = getRightPokemon(ivScanResult.pokemon, pokeInfoCalculator);
        int bAtt = poke.baseAttack;
        int bDef = poke.baseDefense;
        int bSta = poke.baseStamina;
        int ivAtt = bAtt + ivScanResult.lowAttack;
        int ivDef = bDef + ivScanResult.lowDefense;
        int ivSta = bSta + ivScanResult.lowStamina;

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
                return "stat+i";
            } else if (mode == 1) {
                return "att+i";
            } else if (mode == 2) {
                return "def+i";
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
        String returner = context.getString(R.string.clipboard_token_basestat_description);
        if (includeIV) {
            returner = String.format(context.getString(R.string.clipboard_token_basestat_includeiv_description),
                    returner);
        }
        if (mode != 0) {
            returner = String.format(context.getString(R.string.clipboard_token_basestat_mode_description), returner);
        }
        return returner;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.clipboard_token_category_basic_stats);
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return true;
    }
}
