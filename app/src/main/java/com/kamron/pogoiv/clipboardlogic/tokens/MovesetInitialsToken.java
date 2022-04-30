package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.LinkedHashSet;

public class MovesetInitialsToken extends ClipboardToken {

    private static final MovesetData BACKUP_MOVESET = new MovesetData("Razor Leaf", "Frenzy Pla");


    private int maxInitials;


    /**
     * Create a new MovesetInitialsToken given the max initials to use for each move.
     *
     * @param maxInitials How many move name words initials use for the output.
     */
    public MovesetInitialsToken(int maxInitials) {
        super(false);
        this.maxInitials = maxInitials;
    }

    @Override
    public int getMaxLength() {
        return maxInitials * 2;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        return computeInitials(scanResult.selectedMoveset);
    }

    @Override
    public @NonNull String getPreview() {
        LinkedHashSet<MovesetData> movesets = MovesetsManager.getMovesetsForDexNumber(2);
        if (movesets != null) {
            return computeInitials(movesets.iterator().next());
        } else {
            return computeInitials(BACKUP_MOVESET);
        }
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + String.valueOf(maxInitials);
    }

    @Override
    public String getTokenName(Context context) {
        return context.getString(R.string.token_moveset_initials) + maxInitials;
    }

    @Override
    public String getLongDescription(Context context) {
        LinkedHashSet<MovesetData> movesets = MovesetsManager.getMovesetsForDexNumber(2);

        final MovesetData moveset;
        if (movesets != null) {
            moveset = movesets.iterator().next();
        } else {
            moveset = BACKUP_MOVESET;
        }

        String result = computeInitials(moveset);

        return context.getString(R.string.token_msg_moveset_initials,
                maxInitials, moveset.getFast(), moveset.getCharge(), result);
    }

    @Override
    public Category getCategory() {
        return Category.MOVESET;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }

    private @NonNull String computeInitials(@Nullable MovesetData moveset) {
        if (moveset == null
                || Strings.isNullOrEmpty(moveset.getFast())
                || Strings.isNullOrEmpty(moveset.getCharge())) {
            return "";
        }

        StringBuilder resultBuilder = new StringBuilder(maxInitials * 2);

        String[] fastSplit = moveset.getFast().split(" ");
        for (int i = 0; i < maxInitials; i++) {
            if (i < fastSplit.length) {
                resultBuilder.append(Character.toUpperCase(fastSplit[i].charAt(0)));
            } else {
                resultBuilder.append('_');
            }
        }

        String[] chargeSplit = moveset.getCharge().split(" ");
        for (int i = 0; i < maxInitials; i++) {
            if (i < chargeSplit.length) {
                resultBuilder.append(Character.toUpperCase(chargeSplit[i].charAt(0)));
            } else {
                resultBuilder.append('_');
            }
        }

        return resultBuilder.toString();
    }

}
