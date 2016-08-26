package com.kamron.pogoiv;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.LruCache;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.Arrays;

/**
 * Created by Sarav on 8/25/2016.
 * A class to scan a screenshot and extract useful information visible in the bitmap.
 */
public class OCRHelper {

    private static OCRHelper instance = null;
    private TessBaseAPI tesseract = null;
    private LruCache<String, String> ocrCache = new LruCache<>(200);
    private int heightPixels;
    private int widthPixels;
    private int candyOrder;

    /* TODO: This is a temporary hack to keep the commits more bite sized. Will fix soon. */
    public double estimatedPokemonLevel;
    public String pokemonName;
    public String candyName;
    public int pokemonHP;
    public int pokemonCP;
    public String nidoFemale;
    public String nidoMale;

    private OCRHelper() {}

    /**
     * init
     * Initializes the OCR helper and readies it for use
     *
     * @param dataPath     Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static OCRHelper init(String dataPath, int candyOrder, int widthPixels, int heightPixels) {
        if (instance == null) {
            instance = new OCRHelper();
            instance.tesseract = new TessBaseAPI();
            instance.tesseract.init(dataPath, "eng");
            instance.tesseract.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789/♀♂");
            instance.candyOrder = candyOrder;
            instance.heightPixels = heightPixels;
            instance.widthPixels = widthPixels;
        }
        return instance;
    }

    public void exit() {
        instance.tesseract.stop();
        instance.tesseract.end();
        instance.tesseract = null;
    }

    /**
     * replaceColors
     * Replaces colors in a bitmap that are not farther away from a specific color than a given
     * threshold.
     *
     * @param myBitmap     The bitmap to check the colors for.
     * @param keepCr       The red color to keep
     * @param keepCg       The green color to keep
     * @param keepCb       The blue color to keep
     * @param replaceColor The color to replace mismatched colors with
     * @param distance     The distance threshold.
     * @return Bitmap with replaced colors
     */
    private Bitmap replaceColors(Bitmap myBitmap, int keepCr, int keepCg, int keepCb, int replaceColor, int distance) {
        int[] allpixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        int distanceSq = distance * distance;

        for (int i = 0; i < allpixels.length; i++) {
            int rDiff = keepCr - Color.red(allpixels[i]);
            int gDiff = keepCg - Color.green(allpixels[i]);
            int bDiff = keepCb - Color.blue(allpixels[i]);
            int dSq = rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;
            if (dSq > distanceSq) {
                allpixels[i] = replaceColor;
            }
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

    /**
     * Scans the arc and tries to determine the pokemon level, returns 1 if nothing found
     *
     * @param pokemonImage The image of the entire screen
     * @return the estimated pokemon level, or 1 if nothing found
     */
    private double getPokemonLevelFromImg(Bitmap pokemonImage, double trainerLevel) {
        double estimatedPokemonLevel = trainerLevel + 1.5;
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            int index = Data.convertLevelToIndex(estPokemonLevel);
            int x = Data.arcX[index];
            int y = Data.arcY[index];
            if (pokemonImage.getPixel(x, y) == Color.rgb(255, 255, 255)) {
                return estPokemonLevel;
            }
        }
        return 1;
    }

    /**
     * Get the hashcode for a bitmap
     */
    String hashBitmap(Bitmap bmp) {
        int[] allpixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return Integer.toHexString(Arrays.hashCode(allpixels));
    }

    /**
     * Correct some OCR errors in argument.
     */
    private static String fixOcr(String src) {
        return src.replace("1", "l").replace("0", "o");
    }

    /**
     * Dont missgender the poor nidorans.
     * <p/>
     * Takes a subportion of the screen, and averages the color to check the average values and compares to known male / female average
     *
     * @param pokemonImage The screenshot of the entire application
     * @return True if the nidoran is female
     */
    private boolean isNidoranFemale(Bitmap pokemonImage) {
        Bitmap pokemon = Bitmap.createBitmap(pokemonImage, widthPixels / 3, (int) Math.round(heightPixels / 4), (int) Math.round(widthPixels / 3), (int) Math.round(heightPixels / 5));
        int[] pixelArray = new int[pokemon.getHeight() * pokemon.getWidth()];
        pokemon.getPixels(pixelArray, 0, pokemon.getWidth(), 0, 0, pokemon.getWidth(), pokemon.getHeight());
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        // a loop that sums the color values of all the pixels in the image of the nidoran
        for (int i = 0; i < pixelArray.length; i++) {
            redSum += Color.red(pixelArray[i]);
            blueSum += Color.green(pixelArray[i]);
            greenSum += Color.blue(pixelArray[i]);
        }
        int redAverage = redSum / pixelArray.length;
        int greenAverage = greenSum / pixelArray.length;
        int blueAverage = blueSum / pixelArray.length;
        //Average male nidoran has RGB value ~~ 136,165,117
        //Average female nidoran has RGB value~ 135,190,140
        int femaleGreenLimit = 175; //if average green is over 175, its probably female
        int femaleBlueLimit = 130; //if average blue is over 130, its probably female
        boolean isFemale = true;
        if (greenAverage < femaleGreenLimit && blueAverage < femaleBlueLimit) {
            isFemale = false; //if neither average is above the female limit, then it's male.
        }
        return isFemale;
    }

    /**
     * get the pokemon name as analysed from a pokemon image
     *
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private String getPokemonNameFromImg(Bitmap pokemonImage) {
        Bitmap name = Bitmap.createBitmap(pokemonImage, widthPixels / 4, (int) Math.round(heightPixels / 2.22608696), (int) Math.round(widthPixels / 2.057), (int) Math.round(heightPixels / 18.2857143));
        String hash = "name" + hashBitmap(name);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            name = replaceColors(name, 68, 105, 108, Color.WHITE, 200);
            tesseract.setImage(name);
            pokemonName = fixOcr(tesseract.getUTF8Text().replace(" ", ""));
            if (pokemonName.toLowerCase().contains("nidora")) {
                boolean isFemale = isNidoranFemale(pokemonImage);
                if (isFemale) {
                    pokemonName = nidoFemale;
                } else {
                    pokemonName = nidoMale;
                }
            }
            name.recycle();
            ocrCache.put(hash, pokemonName);
        }
        return pokemonName;
    }


    /**
     * gets the candy name from a pokenon image
     *
     * @param pokemonImage the image of the whole screen
     * @return the candy name, or "" if nothing was found
     */
    private String getCandyNameFromImg(Bitmap pokemonImage) {
        Bitmap candy = Bitmap.createBitmap(pokemonImage, widthPixels / 2, (int) Math.round(heightPixels / 1.3724285), (int) Math.round(widthPixels / 2.057), (int) Math.round(heightPixels / 38.4));
        String hash = "candy" + hashBitmap(candy);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            candy = replaceColors(candy, 68, 105, 108, Color.WHITE, 200);
            tesseract.setImage(candy);
            try {
                candyName = fixOcr(tesseract.getUTF8Text().trim().replace("-", " ").split(" ")[candyOrder]);
                candyName = new StringBuilder().append(candyName.substring(0, 1)).append(candyName.substring(1).toLowerCase()).toString();
            } catch (StringIndexOutOfBoundsException e) {
                candyName = "";
            }
            candy.recycle();
            ocrCache.put(hash, candyName);
        }
        return candyName;
    }

    /**
     * get the pokemon hp from a picture
     *
     * @param pokemonImage the image of the whole screen
     * @return an integer of the interpreted pokemon name, 10 if scan failed
     */
    private int getPokemonHPFromImg(Bitmap pokemonImage) {
        int pokemonHP = 10;
        Bitmap hp = Bitmap.createBitmap(pokemonImage, (int) Math.round(widthPixels / 2.8), (int) Math.round(heightPixels / 1.8962963), (int) Math.round(widthPixels / 3.5), (int) Math.round(heightPixels / 34.13333333));
        String hash = "hp" + hashBitmap(hp);
        String pokemonHPStr = ocrCache.get(hash);

        if (pokemonHPStr == null) {
            hp = replaceColors(hp, 55, 66, 61, Color.WHITE, 200);
            tesseract.setImage(hp);
            pokemonHPStr = tesseract.getUTF8Text();
            hp.recycle();
            ocrCache.put(hash, pokemonHPStr);
        }

        if (pokemonHPStr.contains("/")) {
            try {
                pokemonHP = Integer.parseInt(pokemonHPStr.split("/")[1].replace("Z", "2").replace("O", "0").replace("l", "1").replaceAll("[^0-9]", ""));
            } catch (java.lang.NumberFormatException e) {
                pokemonHP = 10;
            }
        }
        return pokemonHP;
    }

    /**
     * get the cp of a pokemon image
     *
     * @param pokemonImage the image of the whole pokemon screen
     * @return a CP of the pokemon, 10 if scan failed
     */
    private int getPokemonCPFromImg(Bitmap pokemonImage) {
        int pokemonCP = 0;
        Bitmap cp = Bitmap.createBitmap(pokemonImage, (int) Math.round(widthPixels / 3.0), (int) Math.round(heightPixels / 15.5151515), (int) Math.round(widthPixels / 3.84), (int) Math.round(heightPixels / 21.333333333));
        cp = replaceColors(cp, 255, 255, 255, Color.BLACK, 30);
        tesseract.setImage(cp);
        String cpText = tesseract.getUTF8Text().replace("O", "0").replace("l", "1");
        if (cpText.length() >= 2) { //gastly can block the "cp" text, so its not visible...
            cpText = cpText.substring(2);
        }
        try {
            pokemonCP = Integer.parseInt(cpText);
        } catch (java.lang.NumberFormatException e) {
            pokemonCP = 10;
        }
        cp.recycle();
        return pokemonCP;
    }

    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage The image of the pokemon
     */
    void scanPokemon(Bitmap pokemonImage, int trainerLevel) {
        estimatedPokemonLevel = getPokemonLevelFromImg(pokemonImage, trainerLevel);
        pokemonName = getPokemonNameFromImg(pokemonImage);
        candyName = getCandyNameFromImg(pokemonImage);
        pokemonHP = getPokemonHPFromImg(pokemonImage);
        pokemonCP = getPokemonCPFromImg(pokemonImage);
    }

}
