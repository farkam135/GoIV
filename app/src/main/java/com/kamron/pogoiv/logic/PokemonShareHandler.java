package com.kamron.pogoiv.logic;

import android.content.Intent;

import com.kamron.pogoiv.Pokefly;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Johan on 2016-11-04.
 * An object which has the ability to broadcast an intent with a pokemon result.
 */

public class PokemonShareHandler {

    /**
     * Creates an intent to share the result of the pokemon scan as a string formated json blob.
     */
    public void spreadResultIntent(Pokefly pokefly, IVScanResult ivScan) {
        JSONObject jsonPokemon = new JSONObject();
        try {
            jsonPokemon.put("PokemonId", ivScan.pokemon.number + 1);
            jsonPokemon.put("AtkMin", ivScan.lowAttack);
            jsonPokemon.put("AtkMax", ivScan.highAttack);
            jsonPokemon.put("DefMin", ivScan.lowDefense);
            jsonPokemon.put("DefMax", ivScan.highDefense);
            jsonPokemon.put("StamMin", ivScan.lowStamina);
            jsonPokemon.put("StamMax", ivScan.highStamina);
            jsonPokemon.put("OverallPower", ivScan.getAveragePercent());
            jsonPokemon.put("Hp", ivScan.scannedHP);
            jsonPokemon.put("Cp", ivScan.scannedCP);
            jsonPokemon.put("uniquePokemon", ivScan.uniquePokemonID);
            jsonPokemon.put("estimatedPokemonLevel", ivScan.estimatedPokemonLevel);
            jsonPokemon.put("candyName", ivScan.pokemon.evolutions.get(0).name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, jsonPokemon.toString());
        sendIntent.setType("application/pokemon-stats");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pokefly.startActivity(sendIntent);
    }


}
