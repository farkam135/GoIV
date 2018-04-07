package com.kamron.pogoiv.scanlogic;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.kamron.pogoiv.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Johan on 2018-03-26.
 */

public class MovesetList {

    private static String LANGUAGE_DE = new Locale("de").getLanguage();
    private static String LANGUAGE_ES = new Locale("es").getLanguage();
    private static String LANGUAGE_FR = new Locale("fr").getLanguage();
    private static String LANGUAGE_IT = new Locale("it").getLanguage();
    private static String LANGUAGE_JP = new Locale("jp").getLanguage();
    private static String LANGUAGE_KO = new Locale("ko").getLanguage();
    private static String LANGUAGE_PT = new Locale("pt").getLanguage();
    private static String LANGUAGE_RU = new Locale("ru").getLanguage();
    private static String LANGUAGE_ZH = new Locale("zh").getLanguage();



    private MovesetList() {
    }

    public static SparseArrayCompat<ArrayList<MovesetData>> parseJSON(final Context context, JsonReader jsonReader) {

        // Get lowercase english names
        String[] enMonNamesArray = PokeInfoCalculator.getPokemonNamesArray(context.getResources());
        ArrayList<String> enMonNamesList = new ArrayList<>();
        for (String name : enMonNamesArray) {
            enMonNamesList.add(name.trim().toUpperCase().replace("[^A-Z0-9]", "_"));
        }

        Pair<HashMap<String, String>, HashMap<String, String>> translations = getTranslations(context);
        HashMap<String, String> translatedMoveNames = translations.first;
        HashMap<String, String> translatedTypeNames = translations.second;

        // Init result object
        SparseArrayCompat<ArrayList<MovesetData>> result = new SparseArrayCompat<>();

        // Parse all the moveset JSON
        Gson gson = new GsonBuilder().create();
        LinkedTreeMap<String, Object> movesetListsByMonsterName = gson.fromJson(jsonReader, Object.class);

        for (String monName : movesetListsByMonsterName.keySet()) {
            int dexIndex = enMonNamesList.indexOf(monName);
            if (dexIndex < 0) {
                Timber.d("Can't find monster named %s", monName);
                continue;
            }

            //noinspection unchecked
            ArrayList<LinkedTreeMap<String, Object>> jsonMovesets
                    = (ArrayList<LinkedTreeMap<String, Object>>) movesetListsByMonsterName.get(monName);

            ArrayList<MovesetData> movesetList = new ArrayList<>(jsonMovesets.size());
            for (LinkedTreeMap<String, Object> jsonMoveset : jsonMovesets) {
                //noinspection SuspiciousMethodCalls
                MovesetData movesetData = new MovesetData(
                        translatedMoveNames.get(jsonMoveset.get("quick")),
                        translatedMoveNames.get(jsonMoveset.get("charge")),
                        (Boolean) jsonMoveset.get("quickIsLegacy"),
                        (Boolean) jsonMoveset.get("chargeIsLegacy"),
                        (Double) jsonMoveset.get("atkScore"),
                        (Double) jsonMoveset.get("defScore"),
                        translatedTypeNames.get("POKEMON_TYPE_" + jsonMoveset.get("chargeMoveType")),
                        translatedTypeNames.get("POKEMON_TYPE_" + jsonMoveset.get("quickMoveType")));
                movesetList.add(movesetData);
            }

            result.append(dexIndex, movesetList);
        }

        return result;
    }

    private static Pair<HashMap<String, String>, HashMap<String, String>> getTranslations(Context context) {
        final Resources res = context.getResources();

        final String language;
        if (!res.getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            Locale originalLocale; // Save original locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                originalLocale = res.getConfiguration().getLocales().get(0);
            } else {
                originalLocale = res.getConfiguration().locale;
            }
            language = originalLocale.getLanguage();
        } else {
            language = Locale.ENGLISH.getLanguage();
        }

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

}
