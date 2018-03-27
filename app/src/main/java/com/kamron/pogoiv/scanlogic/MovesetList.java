package com.kamron.pogoiv.scanlogic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 2018-03-26.
 */

public class MovesetList {

    String pokemonName;
    List<MovesetData> movesets;

    public MovesetList(){
        movesets = new ArrayList<>(1000);
    }

    public static MovesetList[] parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        MovesetList[] lists = gson.fromJson(response, MovesetList[].class);
        return lists;
    }

    public List<MovesetData> getList(){
        return movesets;
    }
}
