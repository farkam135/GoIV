package com.kamron.pogoiv.utils;

import androidx.annotation.NonNull;

import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.ArrayList;

/**
 * Created by Johan on 2018-04-19.
 * <p>
 * A class which keeps a list of pokemons for exporting.
 */

public class ExportPokemonQueue {

    private static final String EXPORT_HEADERS = "pokemon,cp,level,attack,defense,stamina,fastmove,chargemove";
    private static final ArrayList<String> EXPORT_ROWS = new ArrayList<>();

    private ExportPokemonQueue() {
    }

    public static void add(@NonNull ScanResult scanResult) {
        EXPORT_ROWS.add(scanResult.pokemon + ","
                + scanResult.cp + ","
                + scanResult.levelRange.min + ","
                + scanResult.getIVAttackLow() + ","
                + scanResult.getIVDefenseLow() + ","
                + scanResult.getIVStaminaLow() + ","
                + (scanResult.selectedMoveset != null
                ? scanResult.selectedMoveset.getFastKey() : "") + ","
                + (scanResult.selectedMoveset != null
                ? scanResult.selectedMoveset.getChargeKey() : "")
                + "\n");
    }

    public static void clear() {
        EXPORT_ROWS.clear();
    }

    public static int size() {
        return EXPORT_ROWS.size();
    }

    public static @NonNull String getExportString() {
        StringBuilder returner = new StringBuilder(EXPORT_HEADERS);
        returner.append('\n');

        for (String pokeRow : EXPORT_ROWS) {
            returner.append(pokeRow);
        }

        return returner.toString();
    }
}
