package com.kamron.pogoiv;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.test.filters.MediumTest;

import android.test.InstrumentationTestCase;
import android.util.DisplayMetrics;

import android.view.WindowManager;

import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.ScanResult;

import junit.framework.Assert;



import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by NightMadness on 11/18/2016.
 */

public class OCRscanTests extends InstrumentationTestCase {

    /* Default Values */
    private final String dataPath = "/storage/emulated/0/Android/data/com.kamron.pogoiv.nointernet/files";
    private final String nidoFemale = "Nidoran♀";
    private final String nidoMale = "Nidoran♂";
    private final boolean isPokeSpamEnabled = true;


    @MediumTest
    public void testOddish() {
        int heightPixels = 2560;
        int widthPixels = 1440;

        final String fileName = "oddish_lvl24.png";
        int expectedPokemonHP = 22;
        int expectedPokemonCP = 80;
        String expectedPokemonName = "Oddish";

        testPokemonImage(OcrHelper.init(dataPath, widthPixels, heightPixels, nidoFemale, nidoMale,
                true), fileName, expectedPokemonHP, expectedPokemonCP, expectedPokemonName);
    }

    @MediumTest
    public void testNidoqueen() {
        int widthPixels = 1440;
        int heightPixels = 2560;

        final String fileName = "nidoqueen_lvl24.png";
        int expectedPokemonCP = 1432;
        int expectedPokemonHP = 118;
        String expectedPokemonName = "Nidoqueen";

        testPokemonImage(OcrHelper.init(dataPath, widthPixels, heightPixels, nidoFemale, nidoMale,
                true), fileName, expectedPokemonHP, expectedPokemonCP, expectedPokemonName);
    }

    private void testPokemonImage(OcrHelper ocrHelper, String fileName, Integer expectedPokemonHP, Integer
            expectedPokemonCP, String expectedPokemonName) {
        try {
            InputStream in = getInstrumentation().getContext().getAssets().open(fileName);
            Bitmap image = BitmapFactory.decodeStream(in);
            int trainerLevel = 24;

            Resources resources = getResources();
            setTrainerLevel ma = new setTrainerLevel(trainerLevel, resources);
            ScanResult result = ocrHelper.scanPokemon(image, trainerLevel);

            Assert.assertEquals("PokemonHP", expectedPokemonHP, (Integer) result.getPokemonHP().orNull());
            Assert.assertEquals("PokemonCP", expectedPokemonCP, (Integer) result.getPokemonCP().orNull());

            PokeInfoCalculator pokeInfoCalculator = PokeInfoCalculator.getInstance(
                    getPokemonNamesArray(),
                    getPokemonDisplayNamesArray(),
                    getResources().getIntArray(R.array.attack),
                    getResources().getIntArray(R.array.defense),
                    getResources().getIntArray(R.array.stamina),
                    getResources().getIntArray(R.array.devolutionNumber),
                    getResources().getIntArray(R.array.evolutionCandyCost));

            //To remain consistent across devices we should disable user corrections
            /*
            final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";
            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(PREF_USER_CORRECTIONS,
                    Context.MODE_PRIVATE);
            PokemonNameCorrector pnc = new PokemonNameCorrector(pokeInfoCalculator, (Map<String, String>) sharedPref
                    .getAll());
            */

            PokemonNameCorrector pnc = new PokemonNameCorrector(pokeInfoCalculator, (new
                    HashMap<String, String>()));


            PokemonNameCorrector.PokeDist pokemonDist = pnc.getPossiblePokemon(result.getPokemonName(),
                    result.getCandyName(),
                    result.getUpgradeCandyCost());

            Assert.assertEquals("PokemonName", expectedPokemonName, pokemonDist.pokemon.name);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    //Copied from main Activity
    public class setTrainerLevel {
        private final Point arcInit = new Point();
        private int arcRadius;
        private DisplayMetrics displayMetrics;

        public setTrainerLevel(int trainerLevel, Resources res) {
            displayMetrics = res.getDisplayMetrics();
            setupDisplaySizeInfo();
            Data.setupArcPoints(arcInit, arcRadius, trainerLevel);
        }

        private void setupDisplaySizeInfo() {
            arcInit.x = (int) (displayMetrics.widthPixels * 0.5);

            arcInit.y = (int) Math.floor(displayMetrics.heightPixels / 2.803943);
            if (displayMetrics.heightPixels == 2392 || displayMetrics.heightPixels == 800) {
                arcInit.y--;
            } else if (displayMetrics.heightPixels == 1920) {
                arcInit.y++;
            }

            arcRadius = (int) Math.round(displayMetrics.heightPixels / 4.3760683);
            if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960
                    || displayMetrics.heightPixels == 800) {
                arcRadius++;
            }
        }
    }

    //Copied from Pokefly
    private String[] getPokemonNamesArray() {
        if (getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            //If flag ON, force to use English strings as pokemon name for OCR.
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            Locale def = getResources().getConfiguration().locale;//Keep original locale
            conf.setLocale(new Locale("en"));
            res.updateConfiguration(conf, null);
            String[] rtn = res.getStringArray(R.array.pokemon);
            conf.setLocale(def);//Restore to original locale
            res.updateConfiguration(conf, null);
            return rtn;
        }
        return getResources().getStringArray(R.array.pokemon);
    }

    //Copied from Pokefly
    private String[] getPokemonDisplayNamesArray() {
        if (GoIVSettings.getInstance(getBaseContext()).isShowTranslatedPokemonName()) {
            //If pref ON, use translated strings as pokemon name.
            return getResources().getStringArray(R.array.pokemon);
        }
        //Otherwise, use default locale's pokemon name.
        return getPokemonNamesArray();
    }

    private Resources getResources() {
        return getInstrumentation().getTargetContext().getResources();
    }

    private Context getBaseContext() {
        return getInstrumentation().getTargetContext();
    }

}
