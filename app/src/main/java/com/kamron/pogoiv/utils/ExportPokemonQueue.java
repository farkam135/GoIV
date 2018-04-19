package com.kamron.pogoiv.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 2018-04-19.
 *
 * A class which keeps a list of pokemons for exporting.
 */

public class ExportPokemonQueue {
    private static final ExportPokemonQueue ourInstance = new ExportPokemonQueue();

    public static ExportPokemonQueue getInstance() {
        return ourInstance;
    }

    public List<String> stringList = new ArrayList();
    private ExportPokemonQueue() {
    }

    public String getExportString() {
        String returner = "pokemon,cp,level,attack,defense,stamina,fastmove,chargemove\n";

        for (String pokeRow:stringList){
            returner += pokeRow;
        }

        return returner;
    }
}
