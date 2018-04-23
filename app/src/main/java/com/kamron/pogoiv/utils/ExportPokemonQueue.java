package com.kamron.pogoiv.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 2018-04-19.
 * <p>
 * A class which keeps a list of pokemons for exporting.
 */

public class ExportPokemonQueue {

    private static final String EXPORT_HEADERS = "pokemon,cp,level,attack,defense,stamina,fastmove,chargemove";
    private static final ExportPokemonQueue ourInstance = new ExportPokemonQueue();

    public static ExportPokemonQueue getInstance() {
        return ourInstance;
    }

    public List<String> stringList = new ArrayList<>();

    private ExportPokemonQueue() {
    }

    public String getExportString() {
        StringBuilder returner = new StringBuilder(EXPORT_HEADERS);
        returner.append('\n');

        for (String pokeRow : stringList) {
            returner.append(pokeRow);
        }

        return returner.toString();
    }
}
