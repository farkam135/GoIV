package com.kamron.pogoiv.pokeflycomponents;

import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 2018-02-25.
 * <p>
 * A class which retrieves information from an online source about movesets.
 */

public class MoveInfoOnlineFetcher {


    public MoveInfoOnlineFetcher() {

    }

    /**
     * Get all the possible movesets for a pokemon, and their attack/ defense score.
     *
     * @param ivScanResult The ivScanresult that contains the information about which pokemon is requested.
     * @return A list of all possible movesets and their attack & defense score.
     */
    public List<MovesetData> getMovesetData(IVScanResult ivScanResult) {

        List<MovesetData> moves = new ArrayList<>();


        //////////////////////////////////////Remove everything beneath this.///////////////////////////
        //Example move creation
        MovesetData example = new MovesetData("Waterfall", "Hydro pump", false, false, 11.0, 10.8, "water",
                "water");


        return moves;

    }
}
