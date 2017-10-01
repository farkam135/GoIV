package com.kamron.pogoiv.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Johan on 2016-08-27.
 * <p/>
 * A class for methods which is only used during development
 */
public class DevMethods {


    /**
     * saveImage
     * Used to save the image the screen capture is captuing, used for debugging.
     *
     * @param finalBitmap The bitmap to save
     * @param name        The name of the file to save it as
     */
    public static void saveImage(Bitmap finalBitmap, String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String fileName = "Image-" + name + ".jpg";
        File file = new File(myDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception exception) {
            Timber.e("Exception thrown in saveImage()");
            Timber.e(exception);
        }
    }

    /**
     * Method used to generate the <item> </item> list for candy cost of evolution
     * Since pokemons follow the following pattern (with a few exceptions):
     * Pokemon with 3 evolutions will cost
     * 25,100,-
     * 2 evolutions
     * 50, -
     * Exceptions: Eevee:25, weedle:12 kakuna:50, caterpie:12 metapod:50, pidgey:12, magicarp:400 and rattatta:25,
     * fix them manually.
     */
    private void printOutEvolutionCandyCosts(PokeInfoCalculator pokeCalculator) {
        //to create the evolutions

        int evolutionCost = -99999;
        for (Pokemon poke : pokeCalculator.getPokedex()) {
            ArrayList<Pokemon> evoLine = pokeCalculator.getEvolutionLine(poke);
            int numberInEvoLine = 1;
            for (int i = 0; i < evoLine.size(); i++) {
                System.out.println("poke:" + poke.name + " evoLine size: " + evoLine.size());
                if (poke.name.equals(evoLine.get(i).name)) {
                    numberInEvoLine = i;
                }
            }
            if (evoLine.size() == 3) {
                if (numberInEvoLine == 0) {
                    evolutionCost = 25;
                }
                if (numberInEvoLine == 1) {
                    evolutionCost = 100;
                }
                if (numberInEvoLine == 2) {
                    evolutionCost = -1;
                }
            }
            if (evoLine.size() == 2) {
                if (numberInEvoLine == 0) {
                    evolutionCost = 50;
                }
                if (numberInEvoLine == 1) {
                    evolutionCost = -1;
                }
            }
            if (evoLine.size() == 1) {
                evolutionCost = -1;
            }


            System.out.println(
                    "nahojjjen generating script: " + "<item>" + evolutionCost + "</item> <!--" + poke.name + "-->");
        }

    }
}
