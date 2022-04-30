package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.collection.SparseArrayCompat;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Johan on 2018-02-25.
 * <p>
 * A class which retrieves information from a source about movesets.
 * <p>
 */

public class MovesetsManager {

    private static final String LANGUAGE_DE = new Locale("de").getLanguage();
    private static final String LANGUAGE_ES = new Locale("es").getLanguage();
    private static final String LANGUAGE_FR = new Locale("fr").getLanguage();
    private static final String LANGUAGE_IT = new Locale("it").getLanguage();
    private static final String LANGUAGE_JP = new Locale("jp").getLanguage();
    private static final String LANGUAGE_KO = new Locale("ko").getLanguage();
    private static final String LANGUAGE_PT = new Locale("pt").getLanguage();
    private static final String LANGUAGE_RU = new Locale("ru").getLanguage();
    private static final String LANGUAGE_ZH = new Locale("zh").getLanguage();

    // This is a map that matches each pokemon dex number with a list of its movesets
    private static SparseArrayCompat<LinkedHashSet<MovesetData>> movesets = new SparseArrayCompat<>();

    private static final Object initLock = new Object();
    private static Boolean initialized = false;


    private MovesetsManager() {
    }

    public static void init(final @NonNull Context context) {
        synchronized (initLock) {
            if (initialized) {
                return;
            }
            initialized = true;
        }

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
                    movesets = parseJson(context, jsonReader);
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        });
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

    private static SparseArrayCompat<LinkedHashSet<MovesetData>> parseJson(final Context context,
                                                                           JsonReader jsonReader) {

        // Get lowercase english names
        String[] enMonNamesArray = PokeInfoCalculator.getPokemonNamesArray(context.getResources());
        ArrayList<String> enMonNamesList = new ArrayList<>();
        for (int i = 0; i < enMonNamesArray.length; i++) {
            final String upperCaseName;
            switch (i) {
                case 28: // Nidoran♀
                    upperCaseName = "NIDORAN_FEMALE";
                    break;
                case 31: // Nidoran♂
                    upperCaseName = "NIDORAN_MALE";
                    break;
                default:
                    upperCaseName = enMonNamesArray[i].trim().toUpperCase();
                    break;
            }
            enMonNamesList.add(upperCaseName.replaceAll("[^A-Z0-9]+", "_"));
        }

        Pair<HashMap<String, String>, HashMap<String, String>> translations = getTranslations(context);
        HashMap<String, String> translatedMoveNames = translations.first;
        HashMap<String, String> translatedTypeNames = translations.second;

        // Init result object
        SparseArrayCompat<LinkedHashSet<MovesetData>> result = new SparseArrayCompat<>();

        // Parse all the moveset JSON
        Gson gson = new GsonBuilder().create();
        LinkedTreeMap<String, Object> movesetListsByMonsterName = gson.fromJson(jsonReader, Object.class);

        for (String monName : movesetListsByMonsterName.keySet()) {
            if (monName.endsWith("_FORM")) {
                // This is a special form of a particular species. Since we don't handle forms yet, add all the
                // movesets to the "generic" species.
                int underscoreIndex = monName.lastIndexOf("_", monName.length() - 6);
                monName = monName.substring(0, underscoreIndex);
            }

            int dexIndex = enMonNamesList.indexOf(monName);
            if (dexIndex < 0) {
                Timber.d("Can't find monster named %s", monName);
                continue;
            }

            //noinspection unchecked
            ArrayList<LinkedTreeMap<String, Object>> jsonMovesets
                    = (ArrayList<LinkedTreeMap<String, Object>>) movesetListsByMonsterName.get(monName);

            LinkedHashSet<MovesetData> movesetList;
            if (result.indexOfKey(dexIndex) < 0) {
                movesetList = new LinkedHashSet<>(jsonMovesets.size());
            } else {
                // This might happen for different forms of the same species. Add the movesets to the list of the
                // general species. The linked hash set preserve insertion order and avoid duplicates.
                // This last statement relies on a correct implementation of MovesetData.hashCode().
                movesetList = result.get(dexIndex);
            }
            for (LinkedTreeMap<String, Object> jsonMoveset : jsonMovesets) {
                String fastMove = translatedMoveNames.get(jsonMoveset.get("fast"));
                String chargeMove = translatedMoveNames.get(jsonMoveset.get("charge"));

                if (Strings.isNullOrEmpty(fastMove)) {
                    Timber.w("Missing fast move " + jsonMoveset.get("fast")
                            + " translation in " + getLanguage(context.getResources()));
                    continue;
                }
                if (Strings.isNullOrEmpty(chargeMove)) {
                    Timber.w("Missing charge move " + jsonMoveset.get("charge")
                            + " translation in " + getLanguage(context.getResources()));
                    continue;
                }

                //noinspection SuspiciousMethodCalls
                MovesetData movesetData = new MovesetData(
                        (String) jsonMoveset.get("fast"),
                        (String) jsonMoveset.get("charge"),
                        fastMove,
                        chargeMove,
                        translatedTypeNames.get("POKEMON_TYPE_" + jsonMoveset.get("fastMoveType")),
                        translatedTypeNames.get("POKEMON_TYPE_" + jsonMoveset.get("chargeMoveType")),
                        (Boolean) jsonMoveset.get("fastIsLegacy"),
                        (Boolean) jsonMoveset.get("chargeIsLegacy"),
                        (Double) jsonMoveset.get("atkScore"),
                        (Double) jsonMoveset.get("defScore"));
                movesetList.add(movesetData);
            }

            result.append(dexIndex, movesetList);
        }

        return result;
    }

    private static Pair<HashMap<String, String>, HashMap<String, String>> getTranslations(@NonNull Context context) {
        final String language = getLanguage(context.getResources());

        final String assetPath;
        if (language.equals(LANGUAGE_DE)) {
            assetPath = "movesets/de/constants.json";

        } else if (language.equals(LANGUAGE_ES)) {
            assetPath = "movesets/es/constants.json";

        } else if (language.equals(LANGUAGE_FR)) {
            assetPath = "movesets/fr/constants.json";

        } else if (language.equals(LANGUAGE_IT)) {
            assetPath = "movesets/it/constants.json";

        } else if (language.equals(LANGUAGE_JP)) {
            assetPath = "movesets/jp/constants.json";

        } else if (language.equals(LANGUAGE_KO)) {
            assetPath = "movesets/ko/constants.json";

        } else if (language.equals(LANGUAGE_PT)) {
            assetPath = "movesets/pt/constants.json";

        } else if (language.equals(LANGUAGE_RU)) {
            assetPath = "movesets/ru/constants.json";

        } else if (language.equals(LANGUAGE_ZH)) {
            assetPath = "movesets/zh/constants.json";

        } else {
            assetPath = "movesets/en/constants.json";
        }

        JsonReader jsonReader;
        try {
            jsonReader = new JsonReader(new InputStreamReader(context.getAssets().open(assetPath)));
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }

        Gson gson = new GsonBuilder().create();
        LinkedTreeMap<String, Object> translations = gson.fromJson(jsonReader, Object.class);

        Pair<HashMap<String, String>, HashMap<String, String>> result
                = new Pair<>(new HashMap<String, String>(), new HashMap<String, String>());

        for (String attributeName : translations.keySet()) {
            if (attributeName.equals("moves")) {
                //noinspection unchecked
                LinkedTreeMap<String, Object> moves = (LinkedTreeMap<String, Object>) translations.get(attributeName);
                for (String move : moves.keySet()) {
                    //noinspection ConstantConditions
                    result.first.put(move, (String) moves.get(move));
                }

            } else if (attributeName.equals("types")) {
                //noinspection unchecked
                LinkedTreeMap<String, Object> types = (LinkedTreeMap<String, Object>) translations.get(attributeName);
                for (String type : types.keySet()) {
                    //noinspection ConstantConditions
                    result.second.put(type, (String) types.get(type));
                }
            }

        }
        return result;
    }

    private static @NonNull String getLanguage(@NonNull Resources res) {
        if (!res.getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            Locale originalLocale; // Save original locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                originalLocale = res.getConfiguration().getLocales().get(0);
            } else {
                originalLocale = res.getConfiguration().locale;
            }
            return originalLocale.getLanguage();
        } else {
            return Locale.ENGLISH.getLanguage();
        }
    }

}
