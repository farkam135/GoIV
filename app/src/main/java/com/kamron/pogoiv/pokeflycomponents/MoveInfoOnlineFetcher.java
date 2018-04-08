package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;

import com.google.gson.stream.JsonReader;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.MovesetList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;

import timber.log.Timber;

/**
 * Created by Johan on 2018-02-25.
 * <p>
 * A class which retrieves information from an online source about movesets.
 * <p>
 * This class should be called on application start occasionally, locally cache the retrieved results, but also
 * directly communicate with the movesetfraction class which displays the information.
 */

public class MoveInfoOnlineFetcher {

    Pokefly pokefly;

    public MoveInfoOnlineFetcher(Pokefly pokefly) {
        this.pokefly = pokefly;
    }

    /**
     * Updates the local moveset database if neccessary.
     */
    public void updateIfNeccessary() {
        if (enoughTimeHasPassedForUpdate()) {
            forceLocalMovesetDatabaseUpdate();
        }
    }

    /**
     * Forces a redownload of the local moveset database.
     */
    public void forceLocalMovesetDatabaseUpdate() {
        //todo implement


        GoIVSettings settings = GoIVSettings.getInstance(null);
    }

    /**
     * Check if a certain time has passed so that we want to update the database.
     *
     * @return true if enough time has passed.
     */
    private boolean enoughTimeHasPassedForUpdate() {

        return true;
    }

    /**
     * Get all the possible movesets for a pokemon, and their attack/ defense score.
     * This method should read the local moveset database, which is updated by other methods in this class.
     *
     * @param ivScanResult The ivScanresult that contains the information about which pokemon is requested.
     * @return A list of all possible movesets and their attack & defense score.
     */
    public LinkedHashSet<MovesetData> getMovesetData(Context context, IVScanResult ivScanResult) {
        JsonReader jsonReader;

        // From network response
        //jsonReader = new JsonReader(stringReader);

        // From disk cache
        try {
            jsonReader = new JsonReader(
                    new InputStreamReader(context.getAssets().open("movesets/movesets.json")));
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }

        SparseArrayCompat<LinkedHashSet<MovesetData>> movesetLists = MovesetList.parseJson(context, jsonReader);

        LinkedHashSet<MovesetData> moves = movesetLists.get(ivScanResult.pokemon.number);
        //////////////////////////////////////Remove everything beneath this.///////////////////////////
        //Example move creation
        //MovesetData example = new MovesetData("Waterfall", "Hydro pump", false, false, 11, 10.8, "water",
        //         "water");

        return moves;
    }
}
