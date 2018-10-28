package com.kamron.pogoiv.thirdparty;

import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.thirdparty.pokebattler.PokemonId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


/**
 * A class for generating the moveset database json by querying pokebattler.
 * Run the generateMovesetList test to update the json in
 * app/src/main/assets/thirdparty/pokebattler//pokemonMovesetData.json.
 */
public class MovesetFetchUtil {
//    private static final String BASE_URL = "https://fight.pokebattler.com";
//    private static final String BASE_URL = "http://localhost:8001";
    private static final String BASE_URL = "https://fight.pokebattler.com";
    OkHttpClient httpClient;
    public MovesetFetchUtil() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

    }

    /**
     * This "test" generates a json of all pokemon move ratings by querying the pokebattler database.
     */
    @Ignore
    @Test
    public void buildFailsIfThisIsAMain() throws Exception {
//    public static final void main(String... args) throws Exception {
//        public static void main(String... args) throws Exception {

//        Timber.plant(new Timber.DebugTree()); This throws exceptions in unit tests
        MovesetFetchUtil fetcher = new MovesetFetchUtil();
        Map<String, List<MovesetData>> pokemon = fetcher.fetchAllPokemon();
        JSONObject toDump = new JSONObject(pokemon);
        try (FileWriter writer = new FileWriter(new File
                ("app/src/main/assets/movesets/movesets.json")

        )) {
            writer.write(toDump.toString(2));
            System.out.println(toDump.toString(2));
        }

    }

    public String getAttackURL(String pokemon) {
        return BASE_URL
                + "/rankings/attackers/levels/30/defenders/levels/30/strategies/CINEMATIC_ATTACK_WHEN_POSSIBLE"
                + "/DEFENSE_RANDOM_MC"
                + "?sort=OVERALL&dodgeStrategy=DODGE_REACTION_TIME&weatherCondition=NO_WEATHER&filterType"
                + "=TOP_DEFENDER&filterValue="
                + pokemon;
    }

    public String getDefenseURL(String pokemon) {
        return BASE_URL
                + "/rankings/defenders/levels/30/attackers/levels/30/strategies/DEFENSE_RANDOM_MC"
                + "/CINEMATIC_ATTACK_WHEN_POSSIBLE"
                + "?sort=OVERALL&dodgeStrategy=DODGE_REACTION_TIME&weatherCondition=NO_WEATHER&filterType=POKEMON"
                + "&filterValue="
                + pokemon;
    }


    /**
     * Get a list of all attack and defence ratings for all movesets for all pokemon from an online database.
     *
     * @return A map, which has the pokemon names as keys, and a list of movesetdata as values.
     */
    public Map<String, List<MovesetData>> fetchAllPokemon() {
        Map<String, List<MovesetData>> allPokemon = new TreeMap<>();
        for (PokemonId pokemon : PokemonId.values()) {
            if (pokemon == PokemonId.MISSINGNO || pokemon == PokemonId.UNRECOGNIZED || pokemon.name().endsWith
                    ("NORMAL_FORM")) {
                continue;
            }

            try {
                allPokemon.put(pokemon.name(), fetchPokemonOnlineMovesets(pokemon.name()));
                Timber.i("Finished fetching %s", pokemon.name());
                //FIXME: The above doesnt properly log in unit tests
                System.out.println("Finished fetching " + pokemon.name());
            } catch (Exception e) {
                Timber.e("Unexpected error with %s", pokemon.name());
                System.err.println("Unexpected error: " + e);
                e.printStackTrace(System.err);
            }
        }
        return allPokemon;
    }

    /**
     * Get the moveset evaluation from online for a specific pokemon.
     *
     * @param pokemon The pokemon to search for.
     * @return A list of moveset data for that specific pokemon.
     */
    public List<MovesetData> fetchPokemonOnlineMovesets(String pokemon) {
        if (pokemon.startsWith("UNOWN") && pokemon.endsWith("FORM")) {
            // skip unown forms
            return Collections.emptyList();
        }
        TreeMap<MovesetData.Key, Double> attackScores = fetchPokemonScoreMap(getAttackURL(pokemon));
        if (attackScores == null) {
            System.err.println("Unexpected null attack scores for " + pokemon);
            return Collections.emptyList();
        }
        TreeMap<MovesetData.Key, Double> defenseScores = fetchPokemonScoreMap(getDefenseURL(pokemon));
        if (defenseScores == null) {
            System.err.println("Unexpected null defense scores for " + pokemon);
            return Collections.emptyList();
        }
        List<MovesetData> retval = new ArrayList<>(attackScores.size());
        // add all the good attack scores first
        for (Map.Entry<MovesetData.Key, Double> attackScoreEntry : attackScores.entrySet()) {
            MovesetData.Key key = attackScoreEntry.getKey();
            Double defenseScore = defenseScores.get(key);
            //TODO merge with https://fight.pokebattler.com/pokemon and https://fight.pokebattler.com/moves
            retval.add(new MovesetData(key.getQuick(), key.getCharge(), false, false, attackScoreEntry.getValue(),
                    defenseScore, "UNKNOWN", "UNKNOWN"));
        }
        // then add moves that are only good on defense
        for (Map.Entry<MovesetData.Key, Double> defenseScoreEntry : defenseScores.entrySet()) {
            MovesetData.Key key = defenseScoreEntry.getKey();
            if (attackScores.containsKey(key)) {
                continue;
            }
            retval.add(new MovesetData(key.getQuick(), key.getCharge(), false, false, null,
                    defenseScoreEntry.getValue(), "UNKNOWN", "UNKNOWN"));
        }
        return retval;
    }

    /**
     * Get the score for all movesets, in either defence or attack score from an online database.
     *
     * @param url Either the attackURL or defenceURL for a pokemon.
     * @return A treemap containing all possible movesets as keys, and the moveset score as value.
     */
    private TreeMap<MovesetData.Key, Double> fetchPokemonScoreMap(String url) {
        TreeMap<MovesetData.Key, Double> scores;
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject pokemonInfo = new JSONObject(response.body().string());
                scores = parseMovesetJson(pokemonInfo);
            } else {
                scores = null;
            }
        } catch (Exception e) {
            Timber.e("Could not fetch file");
            Timber.e(e);
            // just die
            throw new RuntimeException(e);
        }
        if (scores == null) {
            throw new RuntimeException("Could not fetch url: " + url);
        }
        return scores;
    }

    /**
     * Interprets the json response from the Pokebattler server into a treemap where each key is a moveset
     * combination, and the value is the score of the moveset.
     *
     * @param jsonResponse The json response from pokebattler.
     * @return A treemap with moveset keys and score values.
     * @throws IOException
     */
    private TreeMap<MovesetData.Key, Double> parseMovesetJson(JSONObject jsonResponse) throws IOException {
        TreeMap<MovesetData.Key, Double> scores = new TreeMap<>();
        try {
            JSONArray moveRankings = jsonResponse.getJSONArray("attackers").getJSONObject(0).getJSONArray("byMove");
            double maxScore = moveRankings.getJSONObject(0).getJSONObject("total").getDouble("overallRating");
            for (int i = 0; i < moveRankings.length(); i++) {
                JSONObject move = moveRankings.getJSONObject(i);
                double score = move.getJSONObject("total").getDouble("overallRating") / maxScore;
                MovesetData.Key key = new MovesetData.Key(move.getString("move1"), move.getString("move2"));
                scores.put(key, score);
            }
        } catch (JSONException je) {
            Timber.e("Exception thrown while checking for update");
            Timber.e(je);
        }
        return scores;
    }

}
