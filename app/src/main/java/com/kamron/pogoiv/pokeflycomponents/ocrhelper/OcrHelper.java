package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.google.common.base.Optional;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.ARC_INIT_POINT;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.ARC_RADIUS;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.CANDY_NAME_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_CANDY_AMOUNT_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_CP_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_EVOLUTION_COST_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_HP_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_NAME_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_POWER_UP_CANDY_COST;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_POWER_UP_STARDUST_COST;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_TYPE_AREA;


/**
 * Created by Sarav on 8/25/2016.
 * A class to scan a screenshot and extract useful information visible in the bitmap.
 */
public class OcrHelper {

    private static OcrHelper instance = null;
    private TessBaseAPI tesseract = null;
    private final GoIVSettings settings;
    private final LruCache<String, String> ocrCache = new LruCache<>(200);
    private final LruCache<String, String> appraisalCache = new LruCache<>(200);
    private final int heightPixels;
    private final int widthPixels;
    private final boolean candyWordFirst;
    private final String nidoFemale;
    private final String nidoMale;
    private final boolean isPokeSpamEnabled;

    private OcrHelper(String dataPath, int widthPixels, int heightPixels, String nidoFemale, String nidoMale,
                      GoIVSettings settings) {
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
        this.isPokeSpamEnabled = settings.isPokeSpamEnabled();
        this.settings = settings;

        Map<String, String> appraisalMap = settings.loadAppraisalCache();
        for (Map.Entry<String, String> entry : appraisalMap.entrySet()) {
            appraisalCache.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * init
     * Initializes the OCR helper and readies it for use
     *
     * @param dataPath Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static OcrHelper init(String dataPath, int widthPixels, int heightPixels, String nidoFemale,
                                 String nidoMale, GoIVSettings settings) {
        if (instance == null) {
            instance = new OcrHelper(dataPath, widthPixels, heightPixels, nidoFemale, nidoMale, settings);
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
            Timber.e("Avoided NPE on OcrHelper.exit()");
            //The exception is to ensure we get a stack trace. It's not thrown.
            Timber.e(new Throwable());
        }
    }

    private boolean isCandyWordFirst() {
        //Check if language makes the pokemon name in candy second; France/Spain/Italy/Portuguese 
        //have Bonbon/Caramelos/Doces pokeName.
        String language = Locale.getDefault().getLanguage();
        HashSet<String> specialCandyOrderLangs = new HashSet<>(Arrays.asList("fr", "es", "it", "pt"));
        return specialCandyOrderLangs.contains(language);
    }

    /**
     * Replaces colors in a bitmap that are not farther away from a specific color than a given
     * threshold.
     *
     * @param srcBitmap    The source bitmap to scan.
     * @param mutateSrc    Indicates whether to mutate srcBitmap or to produce a new one.
     * @param keepCr       The red color to keep
     * @param keepCg       The green color to keep
     * @param keepCb       The blue color to keep
     * @param replaceColor The color to replace mismatched colors with
     * @param distance     The distance threshold.
     * @param simpleBG     Whether the bitmap has a simple background
     * @return Bitmap with replaced colors
     */
    private Bitmap replaceColors(Bitmap srcBitmap, boolean mutateSrc, int keepCr, int keepCg, int keepCb,
                                 int replaceColor, int distance, boolean simpleBG) {
        int[] allpixels = new int[srcBitmap.getHeight() * srcBitmap.getWidth()];
        srcBitmap.getPixels(allpixels, 0, srcBitmap.getWidth(), 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
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

        Bitmap dstBitmap;
        if (mutateSrc) {
            dstBitmap = srcBitmap;
        } else {
            dstBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
        }
        dstBitmap.setPixels(allpixels, 0, srcBitmap.getWidth(), 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        return dstBitmap;
    }

    /**
     * Scans the arc and tries to determine the pokemon level, returns 1 if nothing found.
     *
     * @param pokemonImage The image of the entire screen
     * @return the estimated pokemon level, or 1 if nothing found
     */
    private double getPokemonLevelFromImg(Bitmap pokemonImage, int trainerLevel) {
        double estimatedPokemonLevel = Data.trainerLevelToMaxPokeLevel(trainerLevel);
        double previousEstPokemonLevel = estimatedPokemonLevel + 0.5; // Initial value out of range
        int previousLevelDistance = -1; // Initial value indicating no found white pixels
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            int index = Data.maxPokeLevelToIndex(estPokemonLevel);
            int x = Data.arcX[index];
            int y = Data.arcY[index];
            int whiteLineDistance = getCardinalWhiteLineDistFromImg(pokemonImage, x, y);

            // If we found a lower white line distance than our last calculation, last calculation was best match.
            // If the actual level is 1.0, we fall out to the default case below the for loop.
            if (whiteLineDistance < previousLevelDistance) {
                return previousEstPokemonLevel;
            }

            // Have not passed the best match yet; store current values for next loop cycle
            previousEstPokemonLevel = estPokemonLevel;
            previousLevelDistance = whiteLineDistance;
        }
        return 1;
    }

    /**
     * Examines the image from the given coordinates to determine the distance which is
     * consistently white pixels in ALL cardinal directions. This helps identify the point
     * closest to the center of the level indicator dot.
     *
     * @param pokemonImage The image of the entire screen
     * @param x            Horizontal ordinate to scan from
     * @param y            Vertical ordinate to scan from
     * @return -1 if the given coordinate is not a white pixel, otherwise the distance from given
     * coordinate which is white in each cardinal direction.
     */
    private int getCardinalWhiteLineDistFromImg(Bitmap pokemonImage, int x, int y) {
        // Base case of not matching
        if (pokemonImage.getPixel(x, y) != Color.WHITE) {
            return -1;
        }


        int d = 0; // Distance we have successfully searched for white pixels.
        while (true) {
            //Check to see if we're out of bounds.
            if (x - d <= 0 || y - d <= 0
                    || x + d >= pokemonImage.getWidth() || y + d >= pokemonImage.getHeight()) {
                // If the level indicator is on white background, we need to break it before it loops off screen.
                // Happens very rarely.
                break;
            }

            // If any pixel this distance is not white, return our successful search distance
            if (pokemonImage.getPixel(x + d, y) != Color.WHITE
                    || pokemonImage.getPixel(x - d, y) != Color.WHITE
                    || pokemonImage.getPixel(x, y + d) != Color.WHITE
                    || pokemonImage.getPixel(x, y - d) != Color.WHITE) {
                return d;
            }
            d++;
        }
        return d;
    }


    /**
     * Get the evolution cost for a pokemon, like getPokemonEvolutionCostFromImg, but without caching.
     *
     * @param evolutionCostImage The precut image of the evolution cost area.
     * @return the evolution cost (or -1 if absent) wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private Optional<Integer> getPokemonEvolutionCostFromImgUncached(Bitmap evolutionCostImage) {
        //clean the image
        //the dark color used for text in pogo is approximately rgb 76,112,114 if you can afford evo
        //and the red color is rgb 255 95 100 when you cant afford the evolution
        Bitmap evolutionCostImageCanAfford = replaceColors(evolutionCostImage, false, 68, 105, 108, Color.WHITE, 30,
                false);
        Bitmap evolutionCostImageCannotAfford = replaceColors(evolutionCostImage, false, 255, 115, 115, Color.WHITE, 40,
                false);

        boolean affordIsBlank = isOnlyWhite(evolutionCostImageCanAfford);
        boolean cannotAffordIsBlank = isOnlyWhite(evolutionCostImageCannotAfford);
        //check if fully evolved
        if (affordIsBlank && cannotAffordIsBlank) { //if there's no red or black text, there's no text at all.
            return Optional.of(-1);
        }

        //use the correctly refined image (refined for red or black text)
        if (affordIsBlank) {
            evolutionCostImage = evolutionCostImageCannotAfford;
        } else {
            evolutionCostImage = evolutionCostImageCanAfford;
        }

        //If not cached or fully evolved, ocr text
        int result;
        tesseract.setImage(evolutionCostImage);
        String ocrResult = fixOcrLettersToNums(tesseract.getUTF8Text());
        try {
            result = Integer.parseInt(ocrResult);
            if (result == 10 || result == 1) { //second zero hidden behind floating button
                result = 100;
            } else if (result == 40 || result == 4) { //second zero hidden behind floating button
                result = 400; //damn magikarp
            } else if (result == 5) {
                result = 50; //second zero hidden behind button
            } else if (result == 2) {
                result = 25; //5 hidden behind button
            }
            return Optional.of(result);
        } catch (NumberFormatException e) {
            return Optional.absent(); //could not ocr text
        }
    }

    /**
     * Get the evolution cost for a pokemon, example, weedle: 12.
     * If there was no detected upgrade cost, returns -1.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the evolution cost (or -1 if absent) wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private Optional<Integer> getPokemonEvolutionCostFromImg(Bitmap pokemonImage) {
        Bitmap evolutionCostImage;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_EVOLUTION_COST_AREA, settings);
            evolutionCostImage = getImageCrop(pokemonImage, area);
        } else {
            evolutionCostImage = getImageCrop(pokemonImage, 0.625, 0.88, 0.2, 0.03);
        }


        String hash = "candyCost" + hashBitmap(evolutionCostImage);

        //return cache if it exists
        String stringCacheEvoCandyCost = ocrCache.get(hash);
        if (stringCacheEvoCandyCost != null) {
            //XXX in the cache, we encode "no result" as an empty string. That's a hack.
            if (stringCacheEvoCandyCost.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(Integer.parseInt(stringCacheEvoCandyCost));
            }
        }
        Optional<Integer> result = getPokemonEvolutionCostFromImgUncached(evolutionCostImage);
        String ocrResult;
        if (result.isPresent()) {
            ocrResult = String.valueOf(result.get()); //Store error code instead of scanned value
        } else {
            //XXX again, in the cache, we encode "no result" as an empty string.
            ocrResult = "";
        }
        ocrCache.put(hash, ocrResult);
        return result;
    }

    /**
     * Get the power up stardust cost for a pokemon.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the power up cost wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private Optional<Integer> getPokemonPowerUpStardustCostFromImg(Bitmap pokemonImage) {
        Bitmap powerUpStardustCostImage;
        if (settings.hasManualScanCalibration()) {
            try {
                ScanArea area = new ScanArea(POKEMON_POWER_UP_STARDUST_COST, settings);
                powerUpStardustCostImage = getImageCrop(pokemonImage, area);
            } catch (Exception e) {
                return Optional.absent();
            }
        } else {
            // TODO fallback to non-calibrated standard values
            return Optional.absent();
        }


        String hash = "powerUpStardustCost" + hashBitmap(powerUpStardustCostImage);

        //return cache if it exists
        String stringCachePowerUpStardustCost = ocrCache.get(hash);
        if (stringCachePowerUpStardustCost != null) {
            //XXX in the cache, we encode "no result" as an empty string. That's a hack.
            if (stringCachePowerUpStardustCost.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(Integer.parseInt(stringCachePowerUpStardustCost));
            }
        }

        tesseract.setImage(powerUpStardustCostImage);
        String ocrResult = fixOcrLettersToNums(tesseract.getUTF8Text());
        try {
            int result = Integer.parseInt(ocrResult);
            ocrCache.put(hash, ocrResult);
            return Optional.of(result);

        } catch (NumberFormatException e) {
            //XXX again, in the cache, we encode "no result" as an empty string.
            ocrCache.put(hash, "");
            return Optional.absent(); //could not ocr text
        }
    }

    /**
     * Get the power up candy cost for a pokemon.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the power up cost wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private Optional<Integer> getPokemonPowerUpCandyCostFromImg(Bitmap pokemonImage) {
        Bitmap powerUpCandyCostImage;
        if (settings.hasManualScanCalibration()) {
            try {
                ScanArea area = new ScanArea(POKEMON_POWER_UP_CANDY_COST, settings);
                powerUpCandyCostImage = getImageCrop(pokemonImage, area);
            } catch (Exception e) {
                return Optional.absent();
            }
        } else {
            // TODO fallback to non-calibrated standard values
            return Optional.absent();
        }


        String hash = "powerUpCandyCost" + hashBitmap(powerUpCandyCostImage);

        //return cache if it exists
        String stringCachePowerUpCandyCost = ocrCache.get(hash);
        if (stringCachePowerUpCandyCost != null) {
            //XXX in the cache, we encode "no result" as an empty string. That's a hack.
            if (stringCachePowerUpCandyCost.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(Integer.parseInt(stringCachePowerUpCandyCost));
            }
        }

        tesseract.setImage(powerUpCandyCostImage);
        String ocrResult = fixOcrLettersToNums(tesseract.getUTF8Text());
        try {
            int result = Integer.parseInt(ocrResult);
            ocrCache.put(hash, ocrResult);
            return Optional.of(result);

        } catch (NumberFormatException e) {
            //XXX again, in the cache, we encode "no result" as an empty string.
            ocrCache.put(hash, "");
            return Optional.absent(); //could not ocr text
        }
    }

    /**
     * Heuristic method to determine if the image looks empty. Works by taking a horisontal row of pixels from he
     * middle, and looks if they're all pure white.
     *
     * @param refinedImage A pre-processed image of the evolution cost. (should be pre-refined to replace all non
     *                     text colors with pure white)
     * @return true if the image is likely only white
     */
    private boolean isOnlyWhite(Bitmap refinedImage) {
        int[] pixelArray = new int[refinedImage.getWidth()];

        //below code takes one line of pixels in the middle of the pixture from left to right
        refinedImage.getPixels(pixelArray, 0, refinedImage.getWidth(), 0, refinedImage.getHeight() / 2, refinedImage
                .getWidth(), 1);

        for (int pixel : pixelArray) {
            if (pixel != Color.rgb(255, 255, 255)) { // if pixel is not white
                return false;
            }
        }
        return true;
    }

    /**
     * Get the hashcode for a bitmap.
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
        return src.replace("1", "l").replace("0", "o").replace("5", "s").replace("2", "z");
    }

    /**
     * Correct some OCR errors in argument where only numbers are expected.
     */
    private static String fixOcrLettersToNums(String src) {
        return src.replace("S", "5").replace("s", "5").replace("O", "0").replace("B", "8").replace("o",
                "0").replace("l", "1").replace("I", "1").replace("i", "1").replace("Z", "2").replaceAll("[^0-9]", "");
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
        Bitmap pokemon = getImageCrop(pokemonImage, 0.33, 0.25, 0.33, 0.2);
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
     * Get the pokemon name as analysed from a pokemon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private String getPokemonNameFromImg(Bitmap pokemonImage) {
        Bitmap name;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_NAME_AREA, settings);
            name = getImageCrop(pokemonImage, area);
        } else {
            name = getImageCrop(pokemonImage, 0.1, 0.45, 0.85, 0.055);
        }

        String hash = "name" + hashBitmap(name);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            name = replaceColors(name, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(name);
            pokemonName = fixOcrNumsToLetters(tesseract.getUTF8Text().replace(" ", ""));
            if (isNidoranName(pokemonName)) {
                pokemonName = getNidoranGenderName(pokemonImage);
            }
            ocrCache.put(hash, pokemonName);
        }
        return pokemonName;
    }

    /**
     * Get the pokemon type(s) as analysed from a pokemon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private String getPokemonTypeFromImg(Bitmap pokemonImage) {
        Bitmap type;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_TYPE_AREA, settings);
            type = getImageCrop(pokemonImage, area);
        } else {
            type = getImageCrop(pokemonImage, 0.365278, 0.621094, 0.308333, 0.035156);
        }

        String hash = "type" + hashBitmap(type);
        String pokemonType = ocrCache.get(hash);

        if (pokemonType == null) {
            type = replaceColors(type, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(type);
            pokemonType = tesseract.getUTF8Text();
            ocrCache.put(hash, pokemonType);
        }
        return pokemonType;
    }

    /**
     * Get a cropped version of your image.
     *
     * @param img     Which image to crop
     * @param xStart  % of how far in the top left corner of the crop should be x coordinate
     * @param yStart  % of how far in the top left corner of the crop should be y coordinate
     * @param xWidth  how many % of the width should be kept starting from the xstart.
     * @param yHeight how many % of the height should be kept starting from the ystart.
     * @return The crop of the image.
     */
    public Bitmap getImageCrop(Bitmap img, double xStart, double yStart, double xWidth, double yHeight) {
        Bitmap crop = Bitmap.createBitmap(img, (int) (widthPixels * xStart), (int) (heightPixels * yStart),
                (int) (widthPixels * xWidth), (int) (heightPixels * yHeight));
        return crop;
    }

    /**
     * Get an image crop using a scanarea.
     *
     * @param img      The image to crop
     * @param scanArea The area of the image to get
     * @return The scanarea
     */
    public Bitmap getImageCrop(Bitmap img, ScanArea scanArea) {
        if (scanArea.width < 0 || scanArea.height < 0 || scanArea.xPoint < 0 || scanArea.yPoint < 0) {
            return null;
        }
        Bitmap crop = Bitmap.createBitmap(img, (scanArea.xPoint), scanArea.yPoint,
                scanArea.width, scanArea.height);
        return crop;
    }

    /**
     * Get the correctly gendered name of a pokemon.
     *
     * @param pokemonImage The image of the nidoranX.
     * @return The correct name of the pokemon, with the gender symbol at the end.
     */
    private String getNidoranGenderName(Bitmap pokemonImage) {
        if (isNidoranFemale(pokemonImage)) {
            return nidoFemale;
        } else {
            return nidoMale;
        }
    }

    private boolean isNidoranName(String pokemonName) {
        if (pokemonName.toLowerCase().contains(nidoFemale.toLowerCase().replace("♀", ""))) {
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    private static String removeFirstOrLastWord(String src, boolean removeFirst) {
        if (removeFirst) {
            int fstSpace = src.indexOf(' ');
            if (fstSpace != -1) {
                return src.substring(fstSpace + 1);
            }
        } else {
            int lstSpace = src.lastIndexOf(' ');
            if (lstSpace != -1) {
                return src.substring(0, lstSpace);
            }
        }
        return src;
    }

    /**
     * Gets the candy name from a pokenon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return the candy name, or "" if nothing was found
     */
    private String getCandyNameFromImg(Bitmap pokemonImage) {
        Bitmap candy;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(CANDY_NAME_AREA, settings);
            candy = getImageCrop(pokemonImage, area);
        } else {
            candy = getImageCrop(pokemonImage, 0.5, 0.73, 0.47, 0.026);
        }

        String hash = "candy" + hashBitmap(candy);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            candy = replaceColors(candy, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(candy);
            candyName = fixOcrNumsToLetters(
                    removeFirstOrLastWord(tesseract.getUTF8Text().trim().replace("-", " "), candyWordFirst));
            if (isNidoranName(candyName)) {
                candyName = getNidoranGenderName(pokemonImage);
            }
            ocrCache.put(hash, candyName);
        }
        return candyName;
    }

    /**
     * Get the pokemon hp from a picture.
     *
     * @param pokemonImage the image of the whole screen
     * @return an integer of the interpreted pokemon name, 10 if scan failed
     */
    private Optional<Integer> getPokemonHPFromImg(Bitmap pokemonImage) {
        Bitmap hp;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_HP_AREA, settings);
            hp = getImageCrop(pokemonImage, area);
        } else {
            hp = getImageCrop(pokemonImage, 0.357, 0.52, 0.285, 0.0293);
        }
        String hash = "hp" + hashBitmap(hp);
        String pokemonHPStr = ocrCache.get(hash);

        if (pokemonHPStr == null) {
            hp = replaceColors(hp, true, 55, 66, 61, Color.WHITE, 200, true);
            tesseract.setImage(hp);
            pokemonHPStr = tesseract.getUTF8Text();
            ocrCache.put(hash, pokemonHPStr);
        }

        if (pokemonHPStr.contains("/")) {
            try {
                //If "/" comes at the end we'll get an array with only one component.
                String[] hpParts = pokemonHPStr.split("/");
                String hpStr;
                try {
                    if (hpParts.length >= 2) {  //example read "30 / 55 hp"
                        //Cant read part 0 because that changes if poke has low hp
                        hpStr = hpParts[1];
                        hpStr = hpStr.substring(0, hpStr.length() - 2); //Removes the two last chars, like "hp" or "ps"
                    } else if (hpParts.length == 1) { //Failed to read "/", example "30 7 55 hp"
                        hpStr = hpParts[0];
                        hpStr = hpStr.substring(0, hpStr.length() - 2); //Removes the two last chars, like "hp" or "ps"
                    } else {
                        return Optional.absent();
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    return Optional.absent();
                }

                return Optional.of(Integer.parseInt(fixOcrLettersToNums(hpStr)));
            } catch (NumberFormatException e) {
                //Fall-through to default.
            }
        }

        return Optional.absent();
    }

    /**
     * Get the CP of a pokemon image.
     *
     * @param pokemonImage the image of the whole pokemon screen
     * @return a CP of the pokemon, 10 if scan failed
     */
    private Optional<Integer> getPokemonCPFromImg(Bitmap pokemonImage) {
        Bitmap cp;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_CP_AREA, settings);
            cp = getImageCrop(pokemonImage, area);
        } else {
            cp = getImageCrop(pokemonImage, 0.25, 0.064, 0.5, 0.046);
        }

        cp = replaceColors(cp, true, 255, 255, 255, Color.BLACK, 30, false);

        final int width = cp.getWidth();
        final int height = cp.getHeight();

        // Every chunk will contain a character
        ArrayList<Rect> chunks = new ArrayList<>(6);
        Rect currentChunk = null;
        // On devices denser than XHDPI (2x) we can skip a pixel every two (or more) to increase performances
        int increment = (int) Math.max(1, Math.ceil(Resources.getSystem().getDisplayMetrics().density / 2));
        // When we're over a chunk check every pixel instead of skipping so we're sure to find the blank space after it
        for (int x = 0; x < width; x += (currentChunk != null) ? 1 : increment) {
            for (int y = 0; y < height; y += increment) {
                final int pxColor = cp.getPixel(x, y);

                if (currentChunk == null) {
                    if (pxColor != Color.BLACK) {
                        // We found a non-black pixel, start a new character chunk
                        currentChunk = new Rect(x, y, x, height - 1);
                        break;
                    } else if (y >= height - increment) {
                        // We reached the end of this column without finding any non-black pixel.
                        // The next one probably wont be the start of a new chunk: skip it.
                        x += increment;
                    }

                } else {
                    if (pxColor != Color.BLACK) {
                        // We found a non-black pixel. If the current chunk top is below this pixel, update it
                        if (currentChunk.top > y) {
                            currentChunk.top = y;
                        }
                        currentChunk.right = x;
                        break;

                    } else if (y >= height - increment) {
                        // We reached the end of this column without finding any non-black pixel.
                        // End and save the current chunk.
                        chunks.add(currentChunk);
                        currentChunk = null;
                    }
                }
            }
        }

        if (chunks.size() > 0) {
            // Compute the average height of the chunks
            int chunksHeightsSum = 0;
            Iterator<Rect> chunksIterator = chunks.iterator();
            while (chunksIterator.hasNext()) {
                Rect chunk = chunksIterator.next();
                if (chunk.width() <= increment * 2) {
                    // Discard all the chunks smaller than the width of 2 columns
                    chunksIterator.remove();
                } else {
                    chunksHeightsSum += chunk.height();
                }
            }
            final int avgChunksHeight;
            if (chunks.size() > 0) {
                avgChunksHeight = chunksHeightsSum / chunks.size();
            } else {
                avgChunksHeight = 1; // Didn't find any chunk wider than 2 columns, fallback to a safe value
            }

            // Discard all the chunks lower than the average height
            chunksIterator = chunks.iterator();
            while (chunksIterator.hasNext()) {
                Rect chunk = chunksIterator.next();
                if (chunk.height() < avgChunksHeight) {
                    chunksIterator.remove();
                }
            }
        }

        // Merge all the detected chunks in a larger Rect
        Rect mergeRect = null;
        for (Rect chunk : chunks) {
            if (mergeRect == null) {
                mergeRect = new Rect(chunk);
            } else {
                mergeRect.union(chunk);
            }
        }

        tesseract.setImage(cp);
        if (mergeRect != null) {
            tesseract.setRectangle(mergeRect);
        }
        String cpText = tesseract.getUTF8Text();
        cpText = fixOcrLettersToNums(cpText);

        try {
            return Optional.of(Integer.parseInt(fixOcrLettersToNums(cpText)));
        } catch (NumberFormatException e) {
            return Optional.absent();
        }
    }

    /**
     * Get the unique identifier of a pokemon, aka even if you power up the pokemon, the result stays the same.
     *
     * @param pokemonImage the image of the whole pokemon screen
     * @return a string which should remain the same even if you power up a pokemon
     */
    private String getPokemonIdentifierFromImg(Bitmap pokemonImage) {
        Bitmap infoRow = Bitmap.createBitmap(pokemonImage,
                (int) Math.round(widthPixels * .1f), (int) Math.round(heightPixels / 1.714286f),
                (int) Math.round(widthPixels * .8f), (int) Math.round(heightPixels / 25.26316f));
        tesseract.setImage(infoRow);
        String uniqueText = tesseract.getUTF8Text();

        return uniqueText;
    }


    /**
     * Gets the candy amount from a pokemon image, it will return absent if PokeSpam is disabled.
     *
     * @param pokemonImage the image of the whole screen
     * @return candyAmount the candy amount, or blank Optional object if nothing was found
     */
    private Optional<Integer> getCandyAmountFromImg(Bitmap pokemonImage) {
        if (!isPokeSpamEnabled) {
            return Optional.absent();
        }
        Bitmap candyAmount;
        if (settings.hasManualScanCalibration()) {
            ScanArea area = new ScanArea(POKEMON_CANDY_AMOUNT_AREA, settings);
            candyAmount = getImageCrop(pokemonImage, area);
        } else {
            candyAmount = getImageCrop(pokemonImage, 0.60, 0.695, 0.20, 0.038);
        }

        String hash = "candyAmount" + hashBitmap(candyAmount);
        String pokemonCandyStr = ocrCache.get(hash);

        if (pokemonCandyStr == null) {
            candyAmount = replaceColors(candyAmount, true, 68, 105, 108, Color.WHITE, 90, true);
            tesseract.setImage(candyAmount);
            pokemonCandyStr = tesseract.getUTF8Text();
            ocrCache.put(hash, pokemonCandyStr);
        }

        if (pokemonCandyStr.length() > 0) {
            try {
                return Optional.of(Integer.parseInt(fixOcrLettersToNums(pokemonCandyStr)));
            } catch (NumberFormatException e) {
                //Fall-through to default.
            }
        }

        return Optional.absent();
    }


    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and returns the pulled info.
     *
     * @param pokemonImage The image of the pokemon
     * @param trainerLevel Current level of the trainer
     * @return an object
     */
    public ScanResult scanPokemon(@NonNull Bitmap pokemonImage, int trainerLevel) {
        ensureCorrectLevelArcSettings(trainerLevel); //todo, make it so it doesnt initiate on every scan?
        double estimatedPokemonLevel = getPokemonLevelFromImg(pokemonImage, trainerLevel);

        String pokemonName = getPokemonNameFromImg(pokemonImage);
        String pokemonType = getPokemonTypeFromImg(pokemonImage);
        String candyName = getCandyNameFromImg(pokemonImage);
        Optional<Integer> pokemonHP = getPokemonHPFromImg(pokemonImage);
        Optional<Integer> pokemonCP = getPokemonCPFromImg(pokemonImage);
        Optional<Integer> pokemonCandyAmount = getCandyAmountFromImg(pokemonImage);
        Optional<Integer> pokemonUpgradeCost = getPokemonEvolutionCostFromImg(pokemonImage);
        Optional<Integer> pokemonPowerUpStardustCost = getPokemonPowerUpStardustCostFromImg(pokemonImage);
        Optional<Integer> pokemonPowerUpCandyCost = getPokemonPowerUpCandyCostFromImg(pokemonImage);
        String pokemonUniqueIdentifier = pokemonName + pokemonType + candyName + pokemonHP.toString() + pokemonCP
                .toString() + pokemonPowerUpStardustCost.toString() + pokemonPowerUpCandyCost.toString();


        return new ScanResult(estimatedPokemonLevel, pokemonName, pokemonType, candyName, pokemonHP,
                pokemonCP, pokemonCandyAmount, pokemonUpgradeCost, pokemonPowerUpStardustCost, pokemonPowerUpCandyCost,
                pokemonUniqueIdentifier);
    }


    /**
     * Checks if the user has custom screen calibration, and if so, initiates the arc x,y parameters
     *
     * @param trainerLevel the trainer level to initiate the arc points to.
     */
    private void ensureCorrectLevelArcSettings(int trainerLevel) {
        if (settings.hasManualScanCalibration()) {
            ScanPoint arcInit = new ScanPoint(ARC_INIT_POINT, settings);
            int arcRadius = Integer.valueOf(settings.getCalibrationValue(ARC_RADIUS));
            Data.setupArcPoints(arcInit, arcRadius, trainerLevel);
        }
    }


    /**
     * Reads the bottom part of the screen and returns the text there.
     *
     * @param screen The full phone screen.
     * @return String of whats on the bottom of the screen.
     */
    public String getAppraisalText(@NonNull Bitmap screen) {
        Bitmap bottom = getImageCrop(screen, 0.05, 0.89, 0.90, 0.07);
        String hash = "appraisal" + hashBitmap(bottom);
        String appraisalText = appraisalCache.get(hash);

        if (appraisalText == null) {
            //68,105,108 is the color of the appraisal text
            bottom = replaceColors(bottom, true, 68, 105, 108, Color.WHITE, 100, true);
            tesseract.setImage(bottom);
            //Set tesseract not single line mode
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
            appraisalText = tesseract.getUTF8Text();
            appraisalCache.put(hash, appraisalText);
            settings.saveAppraisalCache(appraisalCache.snapshot());
        }

        return hash + "#" + appraisalText;

    }

    /**
     * Removes an entry from the ocrCache.
     *
     * @param hash The hash of the entry to remove.
     */
    public void removeEntryFromApprisalCache(String hash) {
        appraisalCache.remove(hash);
        settings.saveAppraisalCache(appraisalCache.snapshot());
    }
}
