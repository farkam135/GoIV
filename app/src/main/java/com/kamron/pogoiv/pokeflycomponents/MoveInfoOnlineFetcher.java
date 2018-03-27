package com.kamron.pogoiv.pokeflycomponents;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.MovesetList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    public List<MovesetData> getMovesetData(IVScanResult ivScanResult) {

        String jsonData = loadJSONStringFromAsset("moveset.json");

        //System.out.println(jsonData);

        MovesetList[] movesetLists = MovesetList.parseJSON(jsonData);

        //The jsonString has pokemon with several possible moveset combos, so each pokemon should have a MovesetList

        List<MovesetData> moves = movesetLists[0].getList();
        //////////////////////////////////////Remove everything beneath this.///////////////////////////
        //Example move creation
        //MovesetData example = new MovesetData("Waterfall", "Hydro pump", false, false, 11, 10.8, "water",
        //         "water");

        return moves;

    }


    public String loadJSONStringFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = pokefly.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
