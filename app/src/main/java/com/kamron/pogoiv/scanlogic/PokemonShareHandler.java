package com.kamron.pogoiv.scanlogic;

import android.content.Context;
import android.content.Intent;

import com.kamron.pogoiv.Pokefly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by Johan on 2016-11-04.
 * An object which has the ability to broadcast an intent with a pokemon result.
 */

public class PokemonShareHandler {
    private static final String APPLICATION_POKEMON_STATS = "application/pokemon-stats";

    /**
     * Creates an intent to share the result of the pokemon scan as a string formated json blob.
     */
    public void spreadResultIntent(Context context) {
        JSONObject jsonPokemon = new JSONObject();
        try {
            jsonPokemon.put("PokemonId", Pokefly.scanResult.pokemon.number + 1);
            jsonPokemon.put("AtkMin", Pokefly.scanResult.getLowAttack());
            jsonPokemon.put("AtkMax", Pokefly.scanResult.getHighAttack());
            jsonPokemon.put("DefMin", Pokefly.scanResult.getLowDefense());
            jsonPokemon.put("DefMax", Pokefly.scanResult.getHighDefense());
            jsonPokemon.put("StamMin", Pokefly.scanResult.getLowStamina());
            jsonPokemon.put("StamMax", Pokefly.scanResult.getHighStamina());
            jsonPokemon.put("OverallPower", Pokefly.scanResult.getAveragePercent());
            jsonPokemon.put("Hp", Pokefly.scanResult.scannedHP);
            jsonPokemon.put("Cp", Pokefly.scanResult.scannedCP);
            jsonPokemon.put("uniquePokemon", Pokefly.scanData.getPokemonUniqueID());
            jsonPokemon.put("estimatedPokemonLevel", Pokefly.scanResult.estimatedPokemonLevel.min);
            jsonPokemon.put("estimatedPokemonLevelMax", Pokefly.scanResult.estimatedPokemonLevel.max);
            PokeInfoCalculator calc = PokeInfoCalculator.getInstance();
            jsonPokemon.put("candyName", calc.getEvolutionLine(Pokefly.scanResult.pokemon).get(0));

            JSONArray jsonCombinations = new JSONArray();
            for (int i = 0; i <  Pokefly.scanResult.getIVCombinationsCount(); i++) {
                IVCombination ivCombination = Pokefly.scanResult.getIVCombinationAt(i);
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
        context.startActivity(sendIntent);
    }


}
