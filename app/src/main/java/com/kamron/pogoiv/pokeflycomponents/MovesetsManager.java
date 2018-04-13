package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;

import com.google.gson.stream.JsonReader;
import com.kamron.pogoiv.GoIVSettings;
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

public class MovesetsManager {

    // This is a map that matches each pokemon dex number with a list of its movesets
    private static SparseArrayCompat<LinkedHashSet<MovesetData>> movesets = new SparseArrayCompat<>();


    private MovesetsManager() {
    }

    public static void init(final @NonNull Context context) {
        // Parse the moveset json and the translation json and store them to the static variable
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() { // Execute on background to avoid blocking the caller
                JsonReader jsonReader;

                // From network response. Take the HTTP response body as String and put it in a StringReader
                //jsonReader = new JsonReader(stringReader);

                // From disk cache
                try {
                    jsonReader = new JsonReader(
                            new InputStreamReader(context.getAssets().open("movesets/movesets.json")));
                    movesets = MovesetList.parseJson(context, jsonReader);
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        });
    }

    /**
     * Updates the local moveset database if necessary.
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
     *
     * @param pokedexNumber The pokedex number of the pokemon whose movesets are requested.
     * @return A list of all possible movesets and their attack & defense score.
     */
    public static @Nullable LinkedHashSet<MovesetData> getMovesetsForDexNumber(int pokedexNumber) {
        return movesets.get(pokedexNumber);
    }
}
