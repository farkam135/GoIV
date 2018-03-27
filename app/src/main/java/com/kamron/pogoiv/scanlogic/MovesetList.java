package com.kamron.pogoiv.scanlogic;

import android.content.res.Resources;
import android.support.v4.util.SparseArrayCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Johan on 2018-03-26.
 */

public class MovesetList {

    private MovesetList() {
    }

    public static SparseArrayCompat<ArrayList<MovesetData>> parseJSON(final Resources res, String response) {

        // Get lowercase english names
        String[] enMonNamesArray = PokeInfoCalculator.getPokemonNamesArray(res);
        ArrayList<String> enMonNamesList = new ArrayList<>();
        for (String name : enMonNamesArray) {
            enMonNamesList.add(name.trim().toUpperCase().replace("[^A-Z0-9]", "_"));
        }

        // Init result object
        SparseArrayCompat<ArrayList<MovesetData>> result = new SparseArrayCompat<>();

        // Parse all the moveset JSON
        Gson gson = new GsonBuilder().create();
        //noinspection unchecked
        LinkedTreeMap<String, Object> movesetListsByMonsterName
                = (LinkedTreeMap<String, Object>) gson.fromJson(response, Object.class);

        for (String monName : movesetListsByMonsterName.keySet()) {
            int dexIndex = enMonNamesList.indexOf(monName);
            if (dexIndex < 0) {
                Timber.d("Can't find monster named %s", monName);
                continue;
            }

            //noinspection unchecked
            ArrayList<LinkedTreeMap<String, Object>> jsonMovesets
                    = (ArrayList<LinkedTreeMap<String, Object>>) movesetListsByMonsterName.get(monName);

            ArrayList<MovesetData> movesetList = new ArrayList<>(jsonMovesets.size());
            for (LinkedTreeMap<String, Object> jsonMoveset : jsonMovesets) {
                MovesetData movesetData = new MovesetData(
                        (String) jsonMoveset.get("quick"),
                        (String) jsonMoveset.get("charge"),
                        (Boolean) jsonMoveset.get("quickIsLegacy"),
                        (Boolean) jsonMoveset.get("chargeIsLegacy"),
                        (Double) jsonMoveset.get("atkScore"),
                        (Double) jsonMoveset.get("defScore"),
                        (String) jsonMoveset.get("chargeMoveType"),
                        (String) jsonMoveset.get("quickMoveType"));
                movesetList.add(movesetData);
            }

            result.append(dexIndex, movesetList);
        }

        return result;
    }

}
