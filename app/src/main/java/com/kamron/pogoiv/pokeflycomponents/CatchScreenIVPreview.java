package com.kamron.pogoiv.pokeflycomponents;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;
import com.kamron.pogoiv.scanlogic.CPRange;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Johan on 2018-08-27.
 */

public class CatchScreenIVPreview {

    private Pokefly pokefly;

    public CatchScreenIVPreview(Pokefly pokefly) {
        this.pokefly = pokefly;


    }

    public void attemptIVPreview(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap = tryGettingBitmapACoupleOfTimes(bitmap);
        }

        //Get the OCR info from the catch screen
        String[] catchScreenScanResult = OcrHelper.getPokemonNameAndCPFromCatchScreen(bitmap);
        String catchScreenResultPokeName = catchScreenScanResult[0];
        int catchScreenResultCP = 0;
        try {
            catchScreenResultCP = Integer.parseInt(catchScreenScanResult[1]);
        } catch ( NumberFormatException e){
            catchScreenResultCP = -1;
        }


        Pokemon pokemon = parsePokemonFromScanString(catchScreenResultPokeName);


        IVCombination maxIV = new IVCombination(15, 15, 15);
        if (pokemon != null) { //If we didnt understand which pokemon is on the screen, we have to fail.
            List<CPRange> cpRangeAtMaxIVList = new ArrayList<>(80); //There are 80 possible poke levels
            //Get the possible CP for perfect pokemon for all possible levels (level 0.5 - 40)
            for (double lvl = 0.5; lvl <= 40; lvl += 0.5) {
                cpRangeAtMaxIVList.add(PokeInfoCalculator.getInstance().getCpRangeAtLevel(pokemon, maxIV, maxIV, lvl));
            }

            boolean possiblePerfect = false;
            //Compare all perfect CP possibilities with the one we scanned
            for (CPRange range : cpRangeAtMaxIVList) {
                if (range.high == catchScreenResultCP) {
                    String result = pokemon.name + " might be 100% with " + range.high + " CP";
                    Toast.makeText(pokefly, result, Toast.LENGTH_LONG).show();
                    possiblePerfect = true;
                }
            }
            if (possiblePerfect == false){
                Toast.makeText(pokefly, pokemon.name + " " + catchScreenResultCP + " cannot be 100% IV", Toast
                        .LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(pokefly, "Couldn't find " + catchScreenScanResult[0] + " , " + catchScreenScanResult[1],
                    Toast.LENGTH_LONG).show();
        }


        //
    }

    private Pokemon parsePokemonFromScanString(String catchScreenResultPokeName) {
        Pokemon pokemon = null;

        String[] allPokemon = PokeInfoCalculator.getPokemonNamesArray(pokefly.getResources());

        for (String pokemonName : allPokemon) {
            if (catchScreenResultPokeName.toLowerCase().contains(pokemonName.toLowerCase())) {
                pokemon = PokeInfoCalculator.getInstance().get(pokemonName);
            }
        }
        return pokemon;
    }

    private Bitmap tryGettingBitmapACoupleOfTimes(Bitmap bitmap) {

        int tries = 5;
        while (tries > 0 && bitmap == null) {
            bitmap = ScreenGrabber.getInstance().grabScreen();
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
