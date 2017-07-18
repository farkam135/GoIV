package com.kamron.pogoiv.logic;

import android.content.Intent;
import timber.log.Timber;
import com.kamron.pogoiv.Pokefly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Johan on 2016-11-04.
 * An object which has the ability to broadcast an intent with a pokemon result.
 */

public class PokemonShareHandler {
    public static final String APPLICATION_POKEMON_STATS = "application/pokemon-stats";

    /**
     * Creates an intent to share the result of the pokemon scan as a string formated json blob.
     */
    public void spreadResultIntent(Pokefly pokefly, IVScanResult ivScan,String uniquePokemonID) {
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
            jsonPokemon.put("uniquePokemon", uniquePokemonID);
            jsonPokemon.put("estimatedPokemonLevel", ivScan.estimatedPokemonLevel);
            PokeInfoCalculator calc = PokeInfoCalculator.getInstance();
            jsonPokemon.put("candyName", calc.getEvolutionLine(ivScan.pokemon).get(0));

            JSONArray jsonCombinations = new JSONArray();
            for (IVCombination ivCombination : ivScan.iVCombinations) {
                JSONObject jsonCombination = new JSONObject();
                jsonCombination.put("Atk", ivCombination.att);
                jsonCombination.put("Def", ivCombination.def);
                jsonCombination.put("Stam", ivCombination.sta);
                jsonCombination.put("Percent", ivCombination.percentPerfect);
                jsonCombinations.put(jsonCombination);
            }
            jsonPokemon.put("ivCombinations", jsonCombinations);
        } catch (JSONException e) {
            Timber.e("Error when generating jsonPokemon after clicking the share button");
            Timber.e(e);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, jsonPokemon.toString());
        sendIntent.setType(APPLICATION_POKEMON_STATS);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pokefly.startActivity(sendIntent);
    }


}
