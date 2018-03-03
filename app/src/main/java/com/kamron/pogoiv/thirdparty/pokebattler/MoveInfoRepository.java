package com.kamron.pogoiv.thirdparty.pokebattler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by celan on 3/2/2018.
 */

public class MoveInfoRepository {
    public MoveInfoRepository() {

    }
    public void init() throws IOException {
        String json = null;
//        try (InputStream is =  getActivity().getAssets().open("thirdparty/pokebattler/pokemonMovesetData.json")) {
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        }

        //FIXME
//TODO:        read json into map of pokemon name -> List
        // make methods to lookup based on pokemon name
        // make an interface
        // wire this into MoveInfoOnlineFetcher

    }
}
