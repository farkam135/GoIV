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
            jsonPokemon.put("AtkMin", Pokefly.scanResult.getIVAttackLow());
            jsonPokemon.put("AtkMax", Pokefly.scanResult.getIVAttackHigh());
            jsonPokemon.put("DefMin", Pokefly.scanResult.getIVDefenseLow());
            jsonPokemon.put("DefMax", Pokefly.scanResult.getIVDefenseHigh());
            jsonPokemon.put("StamMin", Pokefly.scanResult.getIVStaminaLow());
            jsonPokemon.put("StamMax", Pokefly.scanResult.getIVStaminaHigh());
            jsonPokemon.put("fastMove", Pokefly.scanResult.selectedMoveset.getFast());
            jsonPokemon.put("chargeMove", Pokefly.scanResult.selectedMoveset.getCharge());
            jsonPokemon.put("OverallPower", Pokefly.scanResult.getIVPercentAvg());
            jsonPokemon.put("Hp", Pokefly.scanResult.hp);
            jsonPokemon.put("Cp", Pokefly.scanResult.cp);
            jsonPokemon.put("uniquePokemon", Pokefly.scanData.getPokemonUniqueID());
            jsonPokemon.put("estimatedPokemonLevel", Pokefly.scanResult.levelRange.min);
            jsonPokemon.put("estimatedPokemonLevelMax", Pokefly.scanResult.levelRange.max);
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
