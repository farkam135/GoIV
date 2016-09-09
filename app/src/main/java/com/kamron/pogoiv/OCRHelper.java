package com.kamron.pogoiv;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.logic.ScanResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Sarav on 8/25/2016.
 * A class to scan a screenshot and extract useful information visible in the bitmap.
 */
public class OCRHelper {

    private static OCRHelper instance = null;
    private TessBaseAPI tesseract = null;
    private final LruCache<String, String> ocrCache = new LruCache<>(200);
    private final int heightPixels;
    private final int widthPixels;
    private final boolean candyWordFirst;
    private final String nidoFemale;
    private final String nidoMale;


    private OCRHelper(String dataPath, int widthPixels, int heightPixels, String nidoFemale, String nidoMale) {
        tesseract = new TessBaseAPI();
        tesseract.init(dataPath, "eng");
        tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        tesseract.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789/♀♂");
        this.heightPixels = heightPixels;
        this.widthPixels = widthPixels;
        this.candyWordFirst = isCandyWordFirst();
        this.nidoFemale = nidoFemale;
        this.nidoMale = nidoMale;
    }

    /**
     * init
     * Initializes the OCR helper and readies it for use
     *
     * @param dataPath Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static OCRHelper init(String dataPath, int widthPixels, int heightPixels, String nidoFemale,
                                 String nidoMale) {
        if (instance == null) {
            instance = new OCRHelper(dataPath, widthPixels, heightPixels, nidoFemale, nidoMale);
        }
        return instance;
    }

    public void exit() {
        if (tesseract != null) {
            tesseract.stop();
            tesseract.end();
            tesseract = null;
            instance = null;
        } else {
            Timber.e("Avoided NPE on OCRHelper.exit()");
            //The exception is to ensure we get a stack trace. It's not thrown.
            Timber.e(new Throwable());
        }
    }

    private boolean isCandyWordFirst() {
        //Check if language makes the pokemon name in candy second; France/Spain/Italy have Bonbon/Caramelos pokeName.
        String language = Locale.getDefault().getLanguage();
        HashSet<String> specialCandyOrderLangs = new HashSet<>(Arrays.asList("fr", "es", "it"));
        return specialCandyOrderLangs.contains(language);
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
     * @param simpleBG     Whether the bitmap has a simple background
     * @return Bitmap with replaced colors
     */
    private Bitmap replaceColors(Bitmap myBitmap, int keepCr, int keepCg, int keepCb, int replaceColor, int distance,
                                 boolean simpleBG) {
        int[] allpixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        int bgColor = replaceColor;
        int distanceSq = distance * distance;

        if (simpleBG) {
            bgColor = allpixels[0];
        }

        for (int i = 0; i < allpixels.length; i++) {
            /* Avoid unnecessary math for obviously background color. This removes most of the math
             * for candy, HP and name bitmaps. */
            if (allpixels[i] == bgColor) {
                allpixels[i] = replaceColor;
                continue;
            }
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
    private double getPokemonLevelFromImg(Bitmap pokemonImage, int trainerLevel) {
        double estimatedPokemonLevel = Data.trainerLevelToMaxPokeLevel(trainerLevel);
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            int index = Data.levelToLevelIdx(estPokemonLevel);
            int x = Data.arcX[index];
            int y = Data.arcY[index];
            if (pokemonImage.getPixel(x, y) == Color.rgb(255, 255, 255)) {
                return estPokemonLevel;
            }
        }
        return 1;
    }

    /**
     * Get the evolution cost for a pokemon, example, weedle : 12
     * If there was no detected upgrade cost, returns -1
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the evolution cost, or -1 on scan failure
     */
    public int getPokemonEvolutionCostFromImg(Bitmap pokemonImage) {
        Bitmap evolutionCostImage =
                Bitmap.createBitmap(pokemonImage, (int) (widthPixels * 0.625), (int) (heightPixels * 0.86),
                        (int) (widthPixels * 0.2), (int) (heightPixels * 0.05));
        String hash = "candyCost" + hashBitmap(evolutionCostImage);
        String stringCacheEvoCandyCost = ocrCache.get(hash);
        int result = -1;
        if (stringCacheEvoCandyCost == null) { //if no cached result
            //the dark color used for text in pogo is 76,112,114
            evolutionCostImage = replaceColors(evolutionCostImage, 76, 112, 114, Color.WHITE, 40, false);
            tesseract.setImage(evolutionCostImage);
            stringCacheEvoCandyCost = tesseract.getUTF8Text().replace("S", "5").replace("s", "5");
            try {
                result = Integer.parseInt(stringCacheEvoCandyCost);
            } catch (NumberFormatException e) {
                stringCacheEvoCandyCost = "-1";
            }
            ocrCache.put(hash, stringCacheEvoCandyCost);
        }
        System.out.println("asdasdasd");
        return result;
    }

    /**
     * Get the hashcode for a bitmap
     */
    private String hashBitmap(Bitmap bmp) {
        int[] allpixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return Integer.toHexString(Arrays.hashCode(allpixels));
    }

    /**
     * Correct some OCR errors in argument where only letters are expected.
     */
    private static String fixOcrNumsToLetters(String src) {
        return src.replace("1", "l").replace("0", "o");
    }

    /**
     * Correct some OCR errors in argument where only numbers are expected.
     */
    private static String fixOcrLettersToNums(String src) {
        return src.replace("O", "0").replace("l", "1").replace("Z", "2");
    }

    /**
     * Dont missgender the poor nidorans.
     * <p/>
     * Takes a subportion of the screen, and averages the color to check the average values and compares to known
     * male / female average
     *
     * @param pokemonImage The screenshot of the entire application
     * @return True if the nidoran is female
     */
    private boolean isNidoranFemale(Bitmap pokemonImage) {
        Bitmap pokemon = Bitmap.createBitmap(pokemonImage, widthPixels / 3, Math.round(heightPixels / 4),
                Math.round(widthPixels / 3), Math.round(heightPixels / 5));
        int[] pixelArray = new int[pokemon.getHeight() * pokemon.getWidth()];
        pokemon.getPixels(pixelArray, 0, pokemon.getWidth(), 0, 0, pokemon.getWidth(), pokemon.getHeight());
        int greenSum = 0;
        int blueSum = 0;

        // a loop that sums the color values of all the pixels in the image of the nidoran
        for (int pixel : pixelArray) {
            blueSum += Color.green(pixel);
            greenSum += Color.blue(pixel);
        }
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
        Bitmap name = Bitmap.createBitmap(pokemonImage, widthPixels / 4, (int) Math.round(heightPixels / 2.22608696),
                (int) Math.round(widthPixels / 2.057), (int) Math.round(heightPixels / 18.2857143));
        String hash = "name" + hashBitmap(name);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            name = replaceColors(name, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(name);
            pokemonName = fixOcrNumsToLetters(tesseract.getUTF8Text().replace(" ", ""));
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

    @NonNull
    private static String removeFirstOrLastWord(String src, boolean removeFirst) {
        if (removeFirst) {
            int fstSpace = src.indexOf(' ');
            if (fstSpace != -1)
                return src.substring(fstSpace + 1);
        } else {
            int lstSpace = src.lastIndexOf(' ');
            if (lstSpace != -1)
                return src.substring(0, lstSpace);
        }
        return src;
    }

    /**
     * gets the candy name from a pokenon image
     *
     * @param pokemonImage the image of the whole screen
     * @return the candy name, or "" if nothing was found
     */
    private String getCandyNameFromImg(Bitmap pokemonImage) {
        Bitmap candy = Bitmap.createBitmap(pokemonImage, widthPixels / 2, (int) Math.round(heightPixels / 1.3724285),
                (int) Math.round(widthPixels / 2.1), (int) Math.round(heightPixels / 38.4));
        String hash = "candy" + hashBitmap(candy);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            candy = replaceColors(candy, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(candy);
            try {
                candyName = fixOcrNumsToLetters(
                        removeFirstOrLastWord(tesseract.getUTF8Text().trim().replace("-", " "), candyWordFirst));
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
        Bitmap hp = Bitmap.createBitmap(pokemonImage, (int) Math.round(widthPixels / 2.8),
                (int) Math.round(heightPixels / 1.8962963), (int) Math.round(widthPixels / 3.5),
                (int) Math.round(heightPixels / 34.13333333));
        String hash = "hp" + hashBitmap(hp);
        String pokemonHPStr = ocrCache.get(hash);

        if (pokemonHPStr == null) {
            hp = replaceColors(hp, 55, 66, 61, Color.WHITE, 200, true);
            tesseract.setImage(hp);
            pokemonHPStr = tesseract.getUTF8Text();
            hp.recycle();
            ocrCache.put(hash, pokemonHPStr);
        }

        if (pokemonHPStr.contains("/")) {
            try {
                pokemonHP = Integer.parseInt(fixOcrLettersToNums(pokemonHPStr.split("/")[1]).replaceAll("[^0-9]", ""));
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
        int pokemonCP;
        Bitmap cp = Bitmap.createBitmap(pokemonImage, (int) Math.round(widthPixels / 3.0),
                (int) Math.round(heightPixels / 15.5151515), (int) Math.round(widthPixels / 3.84),
                (int) Math.round(heightPixels / 21.333333333));
        cp = replaceColors(cp, 255, 255, 255, Color.BLACK, 30, false);
        tesseract.setImage(cp);
        String cpText = fixOcrLettersToNums(tesseract.getUTF8Text());
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
     * Performs OCR on an image of a pokemon and returns the pulled info.
     *
     * @param pokemonImage The image of the pokemon
     * @param trainerLevel Current level of the trainer
     * @return an object
     */
    public ScanResult scanPokemon(Bitmap pokemonImage, int trainerLevel) {
        double estimatedPokemonLevel = getPokemonLevelFromImg(pokemonImage, trainerLevel);
        String pokemonName = getPokemonNameFromImg(pokemonImage);
        String candyName = getCandyNameFromImg(pokemonImage);
        int pokemonHP = getPokemonHPFromImg(pokemonImage);
        int pokemonCP = getPokemonCPFromImg(pokemonImage);
        int pokemonUpgradeCost = getPokemonEvolutionCostFromImg(pokemonImage);

        return new ScanResult(estimatedPokemonLevel, pokemonName, candyName, pokemonHP, pokemonCP, pokemonUpgradeCost);
    }
}
