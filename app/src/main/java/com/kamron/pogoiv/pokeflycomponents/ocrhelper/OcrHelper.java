package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.palette.graphics.Palette;
import android.util.LruCache;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanData;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.LevelRange;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.ARC_INIT_POINT;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.ARC_RADIUS;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.CANDY_NAME_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_CANDY_AMOUNT_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_CP_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_EVOLUTION_COST_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_GENDER_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_HP_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_NAME_AREA;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_POWER_UP_CANDY_COST;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_TYPE_AREA;


/**
 * Created by Sarav on 8/25/2016.
 * A class to scan a screenshot and extract useful information visible in the bitmap.
 */
public class OcrHelper {

    private static OcrHelper instance = null;
    private static TessBaseAPI tesseract = null;
    private static boolean isPokeSpamEnabled;
    private static LruCache<String, String> ocrCache;

    private static int DEFAULT_FONT_COLOR = 4680814; //Pokemon go font color, rgb 71,108,110 (approximate)
    private static int CANT_AFFORD_FONT_COLOR = 16736100; //Pokemon go font color, rgb 255,95,100 (approximate)

    private static WeakReference<Pokefly> pokeflyRef;


    private OcrHelper() {
    }

    /**
     * Initializes the OCR helper and readies it for use.
     *
     * @param dataPath Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static synchronized OcrHelper init(@NonNull Pokefly pokefly,
                                              @NonNull String dataPath,
                                              @NonNull PokeInfoCalculator pokeInfoCalculator) {
        pokeflyRef = new WeakReference<>(pokefly);

        if (instance == null) {
            tesseract = new TessBaseAPI();
            tesseract.init(dataPath, "eng");
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
            tesseract.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789/-♀♂");

            ocrCache = new LruCache<>(200);

            instance = new OcrHelper();
        }

        // Reload current settings
        GoIVSettings settings = GoIVSettings.getInstance(pokefly);

        isPokeSpamEnabled = settings.isPokeSpamEnabled();

        return instance;
    }

    public void exit() {
        if (tesseract != null) {
            tesseract.stop();
            tesseract.end();
            tesseract = null;
        }
        instance = null;
        ocrCache = null;
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
    private static Bitmap replaceColors(Bitmap srcBitmap, boolean mutateSrc, int keepCr, int keepCg, int keepCb,
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
    private static double getPokemonLevelFromImg(@NonNull Bitmap pokemonImage, int trainerLevel) {
        double estimatedPokemonLevel = Data.trainerLevelToMaxPokeLevel(trainerLevel);
        double previousEstPokemonLevel = estimatedPokemonLevel + 0.5; // Initial value out of range
        int previousLevelDistance = -1; // Initial value indicating no found white pixels
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            int index = Data.levelToLevelIdx(estPokemonLevel);
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
     * Examines the image from the given coordinates to determine the distance which is consistently white pixels in
     * 2 directions: towards and away the level arc center.
     * This helps identify the point closest to the center of the level indicator dot.
     *
     * @param pokemonImage The image of the entire screen
     * @param x            Horizontal ordinate to scan from
     * @param y            Vertical ordinate to scan from
     * @return -1 if the given coordinate is not a white pixel, otherwise the distance from given
     * coordinate which is white in each cardinal direction.
     */
    private static int getCardinalWhiteLineDistFromImg(Bitmap pokemonImage, int x, int y) {
        final int targetColor = Color.WHITE;

        Double angle = null;
        int r = -1;
        int i1x = x;
        int i1y = y;
        int i2x = x;
        int i2y = y;

        while (pokemonImage.getPixel(i1x, i1y) == targetColor || pokemonImage.getPixel(i2x, i2y) == targetColor) {
            r++;
            if (angle == null) {
                angle = Math.atan2(Data.arcInitY - y, Data.arcInitX - x);
            }
            i1x = (int) Math.round(x + r * Math.cos(angle));
            i1y = (int) Math.round(y + r * Math.sin(angle));
            i2x = (int) Math.round(x - r * Math.cos(angle));
            i2y = (int) Math.round(y - r * Math.sin(angle));
            if (i1x < 0 || i1x >= pokemonImage.getWidth()
                    || i1y < 0 || i1y >= pokemonImage.getHeight()
                    || i2x < 0 || i2x >= pokemonImage.getWidth()
                    || i2y < 0 || i2y >= pokemonImage.getHeight()) {
                return -1;
            }
        }

        return r;
    }

    /**
     * Get the evolution cost for a pokemon, like getPokemonEvolutionCostFromImg, but without caching.
     *
     * @param evolutionCostImage The precut image of the evolution cost area.
     * @return the evolution cost (or -1 if absent) wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private static Optional<Integer> getPokemonEvolutionCostFromImgUncached(@NonNull Bitmap evolutionCostImage) {
        //clean the image

        //Could use DEAFULT_FONT_COLOR, but hardcoded instead
        Bitmap evolutionCostImageCanAfford = replaceColors(evolutionCostImage, false, 68, 105, 108, Color.WHITE, 30,
                false);

        //Could use CANT_AFFORD_FONT_COLOR, but hardcoded instead.
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
    private static Optional<Integer> getPokemonEvolutionCostFromImg(@NonNull Bitmap pokemonImage,

                                                                    @Nullable ScanArea evolutionCostArea) {
        Bitmap evolutionCostImage = null;

        //Since 'new attack' button is at the same place as "evolve" on max evolutions, we need to make sure
        //We're not wrongly reading a 'new attack' button. Check this by scanning left of evolutionCostImage, and
        //looking for a specific color that evolve button doesnt have.
        Bitmap leftOfEvolutionCostImage = null;
        if (evolutionCostArea != null) {
            evolutionCostImage = getImageCrop(pokemonImage, evolutionCostArea);

            leftOfEvolutionCostImage = Bitmap.createBitmap(pokemonImage,
                    evolutionCostArea.xPoint-evolutionCostArea.width,//-evolutionCostArea.width,
                    evolutionCostArea.yPoint,
                    evolutionCostArea.width,
                    evolutionCostArea.height);
            //xstart,ystart,xwidth,
            // ywidth)

        }
        if (evolutionCostImage == null) {
            evolutionCostImage = getImageCrop(pokemonImage, 0.625, 0.815, 0.2, 0.03);
        }

        boolean isNewAttackButton = false;

        //Run through a row of pixels to the left of the evolution cost number, to see if there's 'new attack' text
        //The dark-green ish text for the stardust has the color code rgb: 71 108 110 (not exact near edges & anti
        // aliasing)
        //The block counts the number of text pixels, and checks the % that is text. Evolutions that require
        // evolution stones only have a '1', which should not count as a 'new attack button' field.

       // The color code for 'cant afford' text is approximately rgb 255 95 100

        if (leftOfEvolutionCostImage != null){
            int middle = leftOfEvolutionCostImage.getHeight()/2;
            int amountOfTextPixels = 0;
            int amountOfPinkBackgroundPixels = 0; //evolutions that require items have pink-ish background
            for (int i = 0; i < leftOfEvolutionCostImage.getWidth(); i++) {
                int color = leftOfEvolutionCostImage.getPixel(i, middle);
                boolean normalRange = isInColorRange(color, DEFAULT_FONT_COLOR, 6);
                boolean cantAffordRange = isInColorRange(color, CANT_AFFORD_FONT_COLOR, 6); //if user
                //cant afford the stardust cost

                if (isInColorRange(color, Color.rgb(252,231,239), 3)){
                    amountOfPinkBackgroundPixels++;
                }

                if (normalRange || cantAffordRange) {
                    amountOfTextPixels++;
                }


            }
            double percentTextPixels = amountOfTextPixels / (double) leftOfEvolutionCostImage.getWidth();
            double percentPinkBackground = amountOfPinkBackgroundPixels / (double) leftOfEvolutionCostImage.getWidth();
            boolean isPinkBackground = percentPinkBackground > 0.3;
            if (percentTextPixels > 0.04 && isPinkBackground== false){
                //An evolution stone with '1' cost results in approx 5% text pixels
                isNewAttackButton = true;
            }
        }
        if (isNewAttackButton){
            return Optional.of(-1);
        }


        String hash = "candyCost" + hashBitmap(evolutionCostImage);

        if (ocrCache != null) {
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
        }

        Optional<Integer> result = getPokemonEvolutionCostFromImgUncached(evolutionCostImage);
        String ocrResult;
        if (result.isPresent()) {
            ocrResult = String.valueOf(result.get()); //Store error code instead of scanned value
        } else {
            //XXX again, in the cache, we encode "no result" as an empty string.
            ocrResult = "";
        }
        if (ocrCache != null) {
            ocrCache.put(hash, ocrResult);
        }
        return result;
    }

    /**
     * Checks if the rgb value of an input color is within +- distance on the R, G and B channel.
     * For example, the color 100,100,100 and the color 100,110,110 distance 15 would fail, because both G and B
     * channels are outside the range.
     * @param inputColor
     * @param matchWithColor
     * @param allowedDistance
     * @return
     */
    public static boolean isInColorRange(int inputColor, int matchWithColor, int allowedDistance) {
        int red = Color.red(inputColor);
        int green = Color.green(inputColor);
        int blue = Color.blue(inputColor);

        int matchRed = Color.red(matchWithColor);
        int matchGreen = Color.green(matchWithColor);
        int matchBlue = Color.blue(matchWithColor);

        boolean redInRange = red > matchRed - allowedDistance && red < matchRed + allowedDistance;
        boolean blueInRange = blue > matchBlue - allowedDistance && blue < matchBlue + allowedDistance;
        boolean greenInRange = green > matchGreen - allowedDistance && green < matchGreen + allowedDistance;

        return redInRange && blueInRange && greenInRange;
    }

    /**
     * Get the moveset for a pokémon.
     *
     * @param pokemonImage      The image of the full pokemon screen
     * @param evolutionCostArea The pokémon evolution cost are, moveset is always located below this information
     * @return A pair of strings that represent the fast and charged moves
     */
    private static @Nullable Pair<String, String> getMovesetFromImg(@NonNull Bitmap pokemonImage,
                                                                    @Nullable LevelRange levelRange,
                                                                    @Nullable ScanArea powerUpCandyCostArea,
                                                                    @Nullable ScanArea evolutionCostArea) {
        if (levelRange == null || powerUpCandyCostArea == null || evolutionCostArea == null) {
            return null;
        }

        final int y;
        final int h;
        if (levelRange.min >= Data.MAXIMUM_POKEMON_LEVEL) {
            // This pokemon has reached the max level and cannot be powered up: the power up button
            // isn't present in the game UI. The moveset will be positioned
            // below the power up cost bottom and above the evolution cost bottom
            y = powerUpCandyCostArea.yPoint + powerUpCandyCostArea.height;
            h = (evolutionCostArea.yPoint + evolutionCostArea.height) - y;

        } else {
            // This pokemon is not maxed out, its moveset is below the evolution cost and above
            // the end of the screen (or the navigation bar with the virtual keys)
            y = evolutionCostArea.yPoint + evolutionCostArea.height;
            h = (pokemonImage.getHeight() - getNavigationBarHeight()) - y;
        }

        final int x = (int) (pokemonImage.getWidth() / 10 * 1.3f);
        final int w = (int) (pokemonImage.getWidth() / 10 * 5.0f) - x;

        Bitmap movesetImage = getImageCrop(pokemonImage, new ScanArea(x, y, w, h));
        if (movesetImage == null) {
            return null;
        }

        String hash = "moveset" + hashBitmap(movesetImage);

        if (ocrCache != null) {
            //return cache if it exists
            String stringCacheMoveset = ocrCache.get(hash);
            if (stringCacheMoveset != null) {
                //XXX in the cache, we encode "no result" as an empty string. That's a hack.
                if (stringCacheMoveset.isEmpty()) {
                    return null;
                } else {
                    String[] moves = stringCacheMoveset.split("\n");
                    return new Pair<>(moves[0], moves[1]);
                }
            }
        }

        movesetImage = replaceColors(movesetImage, true, 68, 105, 108, Color.BLACK, 50, false);

        tesseract.setImage(movesetImage);
        tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        String ocrResult = tesseract.getUTF8Text();
        tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        String[] lines = ocrResult.split("\n");
        if (lines.length == 2 && lines[0].trim().length() >= 3 && lines[1].trim().length() >= 3) {
            // Just 2 lines were detected with at least 3 characters
            String fast = lines[0].trim();
            String charged = lines[1].trim();
            Pair<String, String> result = new Pair<>(fast, charged);
            if (ocrCache != null) {
                ocrCache.put(hash, fast + "\n" + charged);
            }
            return result;
        }
        return null;
    }

    /**
     * Get the power up stardust cost for a pokemon.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the power up cost wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private static Optional<Integer> getPokemonPowerUpStardustCostFromImg(@NonNull Bitmap pokemonImage,
                                                                          @Nullable ScanArea powerUpStardustCostArea) {
        Bitmap powerUpStardustCostImage = null;
        if (powerUpStardustCostArea != null) {
            powerUpStardustCostImage = getImageCrop(pokemonImage, powerUpStardustCostArea);
        }
        if (powerUpStardustCostImage == null) {
            powerUpStardustCostImage = getImageCrop(pokemonImage, 0.544, 0.803, 0.139, 0.0247);
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
    private static Optional<Integer> getPokemonPowerUpCandyCostFromImg(@NonNull Bitmap pokemonImage,
                                                                       @Nullable ScanArea powerUpCandyCostArea) {
        Bitmap powerUpCandyCostImage = null;
        if (powerUpCandyCostArea != null) {
            powerUpCandyCostImage = getImageCrop(pokemonImage, powerUpCandyCostArea);
        }
        if (powerUpCandyCostImage == null) {
            powerUpCandyCostImage = getImageCrop(pokemonImage, 0.73, 0.742, 0.092, 0.0247);
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
    private static boolean isOnlyWhite(Bitmap refinedImage) {
        int[] pixelArray = new int[refinedImage.getWidth()];

        //below code takes one line of pixels in the middle of the pixture from left to right
        refinedImage.getPixels(pixelArray, 0, refinedImage.getWidth(), 0, refinedImage.getHeight() / 2, refinedImage
                .getWidth(), 1);

        int numNonWhitePixels = 0;
        for (int pixel : pixelArray) {
            if (pixel != Color.rgb(255, 255, 255)) { // if pixel is not white
                numNonWhitePixels++;
            }
        }
        float percentNonWhite = (float) numNonWhitePixels / (float) pixelArray.length;
        if (percentNonWhite > 0.013){
            return false;
        }

        return true;
    }

    /**
     * Get the hashcode for a bitmap.
     */
    private static String hashBitmap(Bitmap bmp) {
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
        return src.trim().replace("S", "5").replace("s", "5").replace("O", "0").replace("B", "8")
                .replace("o", "0").replace("l", "1").replace("I", "1").replace("i", "1").replace("Z", "2")
                .replaceAll("[^0-9]+$", "");
    }

    /**
     * Get the pokemon name as analysed from a pokemon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private static String getPokemonNameFromImg(@NonNull Bitmap pokemonImage,
                                                @NonNull Pokemon.Gender pokemonGender,
                                                @Nullable ScanArea nameArea) {
        Bitmap name = null;
        if (nameArea != null) {
            name = getImageCrop(pokemonImage, nameArea);
        }
        if (name == null) {
            name = getImageCrop(pokemonImage, 0.1, 0.4125, 0.85, 0.055);
        }

        String hash = "name" + hashBitmap(name);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            name = replaceColors(name, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(name);
            pokemonName = fixOcrNumsToLetters(tesseract.getUTF8Text().replace(" ", ""));
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
    private static String getPokemonTypeFromImg(@NonNull Bitmap pokemonImage, @Nullable ScanArea typeArea) {
        Bitmap type = null;
        if (typeArea != null) {
            type = getImageCrop(pokemonImage, typeArea);
        }
        if (type == null) {
            type = getImageCrop(pokemonImage, 0.365278, 0.572, 0.308333, 0.035156);
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
     * Get the pokemon gender as analysed from a pokemon image.
     *
     * @param pokemonImage The image of the whole screen
     * @return Optional.of(" ♂ ") if the pokémon is male, Optional.of("♀") if female, Optional.absent() otherwise
     */
    public static Pokemon.Gender getPokemonGenderFromImg(@NonNull Bitmap pokemonImage, @Nullable ScanArea genderArea) {
        Bitmap genderImage = null;
        if (genderArea != null) {
            genderImage = getImageCrop(pokemonImage, genderArea);
        }
        if (genderImage == null) {
            genderImage = getImageCrop(pokemonImage, 0.822, 0.455, 0.0682, 0.03756);
        }

        replaceColors(genderImage, true, 68, 105, 108, Color.WHITE, 200, true);

        int width = genderImage.getWidth();
        int height = genderImage.getHeight();

        // The top left pixel should always be empty
        int bgColor = genderImage.getPixel(0, 0);

        // Analyze the gender area to search for ♂ or ♀.
        // Divide it in 2 vertical halves.
        // Scan one line every two and search for the first non white pixel.
        // Sum its X coordinate and repeat. The final sum will be used as score.
        int upperHalfScore = 0;
        int lowerHalfScore = 0;

        // Top
        for (int y = 0; y < height / 2; y += 2) {
            for (int x = 0; x < width; x++) {
                if (genderImage.getPixel(x, y) != bgColor) {
                    upperHalfScore += x;
                    break;
                }
            }
        }

        // Bottom
        for (int y = (int) Math.ceil(height / 2f); y < height; y += 2) {
            for (int x = 0; x < width; x++) {
                if (genderImage.getPixel(x, y) != bgColor) {
                    lowerHalfScore += x;
                    break;
                }
            }
        }

        if (upperHalfScore > lowerHalfScore) {
            return Pokemon.Gender.M;
        } else if (lowerHalfScore > upperHalfScore) {
            return Pokemon.Gender.F;
        } else {
            return Pokemon.Gender.N;
        }
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
    private static Bitmap getImageCrop(@NonNull Bitmap img,
                                       double xStart, double yStart,
                                       double xWidth, double yHeight) {
        int w = img.getWidth();
        int h = img.getHeight();
        return Bitmap.createBitmap(img,
                (int) (w * xStart), (int) (h * yStart),
                (int) (w * xWidth), (int) (h * yHeight));
    }

    /**
     * Get an image crop using a scanarea.
     *
     * @param img      The image to crop
     * @param scanArea The area of the image to get
     * @return The scanarea
     */
    private static @Nullable Bitmap getImageCrop(@NonNull Bitmap img, @NonNull ScanArea scanArea) {
        if (scanArea.xPoint < 0) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea x is less then zero, value: " + scanArea.xPoint));
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        if (scanArea.yPoint < 0) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea y is less then zero, value: " + scanArea.yPoint));
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        if (scanArea.width <= 0) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea width is less or equal then zero, value: " + scanArea.width));
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        if (scanArea.height <= 0) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea height is less or equal then zero, value: " + scanArea.height));
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        if (scanArea.xPoint + scanArea.width > img.getWidth()) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea x+width is greater then image width, value: " + (scanArea.xPoint + scanArea.width)));
            Timber.d("Image size (w,h): %1$s,%2$s", img.getWidth(), img.getHeight());
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        if (scanArea.yPoint + scanArea.height > img.getHeight()) {
            Timber.e(new IllegalArgumentException(
                    "ScanArea y+height is greater then image height, value: " + (scanArea.yPoint + scanArea.height)));
            Timber.d("Image size (w,h): %1$s,%2$s", img.getWidth(), img.getHeight());
            Timber.d("ScanArea (x,y,w,h): %1$s", scanArea.toString());
            return null;
        }
        return Bitmap.createBitmap(img, scanArea.xPoint, scanArea.yPoint, scanArea.width, scanArea.height);
    }

    /**
     * Gets the candy name from a pokemon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return the candy name, or "" if nothing was found
     */
    private static String getCandyNameFromImg(@NonNull Bitmap pokemonImage,
                                              @NonNull Pokemon.Gender pokemonGender,
                                              @Nullable ScanArea candyNameArea) {
        Bitmap candy = null;
        if (candyNameArea != null) {
            candy = getImageCrop(pokemonImage, candyNameArea);
        }
        if (candy == null) {
            candy = getImageCrop(pokemonImage, 0.5, 0.678, 0.47, 0.026);
        }

        String hash = "candy" + hashBitmap(candy);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            candy = replaceColors(candy, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(candy);
            candyName = tesseract.getUTF8Text();
            candyName = fixOcrNumsToLetters(candyName);
            ocrCache.put(hash, candyName);
        }
        return candyName;
    }

    private static List<String> getCandyNamesFromImg(@NonNull Bitmap pokemonImage,
                                                     @NonNull Pokemon.Gender pokemonGender,
                                                     @Nullable ScanArea candyNameArea) {
        int img_w = pokemonImage.getWidth();
        int img_h = pokemonImage.getHeight();
        int area_y = (int)(0.678 * img_h);
        int area_h = (int)(0.026 * img_h);

        if (candyNameArea != null) {
            area_y = candyNameArea.yPoint;
            area_h = candyNameArea.height;
        }

        // TODO: Get both scan areas from config instead of hacking them like this
        ScanArea candyNameArea_3 = new ScanArea((int)(0.34*img_w), area_y, (int)(0.31*img_w), area_h);
        ScanArea candyNameArea_24 = new ScanArea((int)(0.5*img_w), area_y, (int)(0.47*img_w), area_h);

        return Arrays.asList(getCandyNameFromImg(pokemonImage, pokemonGender, candyNameArea_3),
                             getCandyNameFromImg(pokemonImage, pokemonGender, candyNameArea_24));
    }

    /**
     * Get the pokemon hp from a picture.
     *
     * @param pokemonImage the image of the whole screen
     * @return an integer of the interpreted pokemon name, 10 if scan failed
     */
    private static Optional<Integer> getPokemonHPFromImg(@NonNull Bitmap pokemonImage, @Nullable ScanArea hpArea) {
        Bitmap hp = null;
        if (hpArea != null) {
            hp = getImageCrop(pokemonImage, hpArea);
        }
        if (hp == null) {
            hp = getImageCrop(pokemonImage, 0.357, 0.482, 0.285, 0.0293);
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
    private Optional<Integer> getPokemonCPFromImg(@NonNull Bitmap pokemonImage, @Nullable ScanArea cpArea) {
        Bitmap cp = null;
        if (cpArea != null) {
            cp = getImageCrop(pokemonImage, cpArea);
        }
        if (cp == null) {
            cp = getImageCrop(pokemonImage, 0.25, 0.059, 0.5, 0.046);
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

        try {
            return Optional.of(Integer.parseInt(fixOcrLettersToNums(tesseract.getUTF8Text())));
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
    private static String getPokemonIdentifierFromImg(Bitmap pokemonImage) {
        int w = pokemonImage.getWidth();
        int h = pokemonImage.getHeight();
        Bitmap infoRow = Bitmap.createBitmap(pokemonImage,
                Math.round(w * .1f), Math.round(h / 1.714286f),
                Math.round(w * .8f), Math.round(h / 25.26316f));
        tesseract.setImage(infoRow);
        return tesseract.getUTF8Text(); // Unique text
    }


    /**
     * Gets the candy amount from a pokemon image, it will return absent if PokeSpam is disabled.
     *
     * @param pokemonImage the image of the whole screen
     * @return candyAmount the candy amount, or blank Optional object if nothing was found
     */
    private static Optional<Integer> getCandyAmountFromImg(@NonNull Bitmap pokemonImage,
                                                           @Nullable ScanArea candyAmountArea) {
        if (!isPokeSpamEnabled) {
            return Optional.absent();
        }
        Bitmap candyAmount = null;
        if (candyAmountArea != null) {
            candyAmount = getImageCrop(pokemonImage, candyAmountArea);
        }
        if (candyAmount == null) {
            candyAmount = getImageCrop(pokemonImage, 0.60, 0.644, 0.20, 0.038);
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
    public ScanData scanPokemon(@NonNull GoIVSettings settings,
                                @NonNull Bitmap pokemonImage,
                                int trainerLevel,
                                boolean requestFullScan) {

        if (requestFullScan){
            rememberGUIAccentColorBasedOnScan(pokemonImage);
        }
        ensureCorrectLevelArcSettings(settings, trainerLevel); //todo, make it so it doesnt initiate on every scan?
        int totalOffset = 0;
        boolean is_lucky = false;
        double offsetBase; // offsets are calculated off height of powerup candy cost field. Double to retain precision
        // Since we only care about height, its fine that we don't know the proper offset yet
        ScanArea powerUpCandyArea = ScanArea.calibratedFromSettings(POKEMON_POWER_UP_CANDY_COST, settings);
        if (powerUpCandyArea == null) {
            offsetBase = 0.0247 * pokemonImage.getHeight();
            Toast.makeText(pokeflyRef.get(), "Please update GoIV recalibration on a normal unlucky Pokemon with at "
                    + "most two candy types.", Toast
                    .LENGTH_SHORT).show();
        } else {
            offsetBase = powerUpCandyArea.height;
        }

        /* Scan fields more or less top to bottom. */
        // Level Arc skipped so power up cost can be used to refine

        Optional<Integer> cp = getPokemonCPFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_CP_AREA, settings));

        // Name skipped for help from gender

        Optional<Integer> hp = getPokemonHPFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_HP_AREA, settings, 0));
        // If no hp was found, check by offsetting down by the height of the
        // "LUCKY POKEMON" string, which is slightly higher than the power up candy cost field
        if (!hp.isPresent()) {
            int luckyOffset = (int) (offsetBase * 1.2);
            hp = getPokemonHPFromImg(pokemonImage,
                    ScanArea.calibratedFromSettings(POKEMON_HP_AREA, settings, totalOffset + luckyOffset));

            if (hp.isPresent()) {
                // Found successfully; assume this is a lucky pokemon and offset all further scans below that line
                totalOffset += luckyOffset;
                is_lucky = true;
            }
        }

        Pokemon.Gender gender = getPokemonGenderFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_GENDER_AREA, settings, totalOffset));

        // Name needs help from gender.
        String name = getPokemonNameFromImg(pokemonImage, gender,
                ScanArea.calibratedFromSettings(POKEMON_NAME_AREA, settings)); // Not offset for lucky

        String type = getPokemonTypeFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_TYPE_AREA, settings, totalOffset));

        List<String> candyNames = getCandyNamesFromImg(pokemonImage, gender,
                ScanArea.calibratedFromSettings(CANDY_NAME_AREA, settings, totalOffset));

        Optional<Integer> candyAmount;
        if (requestFullScan && isPokeSpamEnabled) {
            candyAmount = getCandyAmountFromImg(pokemonImage,
                    ScanArea.calibratedFromSettings(POKEMON_CANDY_AMOUNT_AREA, settings, totalOffset));
        } else {
            candyAmount = Optional.absent();
        }

        Optional<Integer> powerUpCandyCost = getPokemonPowerUpCandyCostFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_POWER_UP_CANDY_COST, settings, totalOffset));
        // Check if candy names wrapped to second line (with one row of candies)
        if (!powerUpCandyCost.isPresent()) {
            int candyOffset = (int) (offsetBase * 0.8);
            powerUpCandyCost = getPokemonPowerUpCandyCostFromImg(pokemonImage,
                    ScanArea.calibratedFromSettings(POKEMON_POWER_UP_CANDY_COST, settings, totalOffset + candyOffset));
            if (powerUpCandyCost.isPresent()) {
                // Found successfully; Assume this pokemon has its candy names wrapped to a second line
                totalOffset += candyOffset;
            }
        }
        // Check if there's two rows of candies
        if (!powerUpCandyCost.isPresent()) {
            // Check if candy names wrapped to second line (with one row of candies)
            int candyOffset = (int) (powerUpCandyArea.height * 3.2);
            powerUpCandyCost = getPokemonPowerUpCandyCostFromImg(pokemonImage,
                    ScanArea.calibratedFromSettings(POKEMON_POWER_UP_CANDY_COST, settings, totalOffset + candyOffset));

            if (powerUpCandyCost.isPresent()) {
                // Found successfully; Assume this pokemon has two rows of candies
                totalOffset += candyOffset;
            }
        }

        Optional<Integer> powerUpStardustCost = Optional.absent();
        /*Optional<Integer> powerUpStardustCost = getPokemonPowerUpStardustCostFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_POWER_UP_STARDUST_COST, settings, totalOffset));*/

        Optional<Integer> evolutionCost = getPokemonEvolutionCostFromImg(pokemonImage,
                ScanArea.calibratedFromSettings(POKEMON_EVOLUTION_COST_AREA, settings, totalOffset));

        // Level needs help from candy cost
        double estimatedPokemonLevel = getPokemonLevelFromImg(pokemonImage, trainerLevel);
        LevelRange estimatedLevelRange =
                refineLevelEstimate(trainerLevel, powerUpCandyCost, estimatedPokemonLevel);

        String uniqueIdentifier = name + type + candyNames.get(0) + candyNames.get(1) + hp.toString() + cp
                .toString() + powerUpStardustCost.toString() + powerUpCandyCost.toString();

        return new ScanData(estimatedLevelRange, name, type, candyNames, gender, hp, cp, candyAmount, evolutionCost,
                powerUpStardustCost, powerUpCandyCost, null, null, is_lucky, uniqueIdentifier);
    }

    /**
     * Saves an accent color usable for the GUI overlay in the GUIColorFromPokeType class. The class is async,
     * so it doesnt update the value in GUIColorFromPokeType instantly.
     * @param pokemonImage
     */
    private void rememberGUIAccentColorBasedOnScan(Bitmap pokemonImage) {
//        int top = (int) (pokemonImage.getHeight() * 0.30);
//        int bot = (int) (pokemonImage.getHeight() * 0.35);
//        int left = (int) (pokemonImage.getWidth() * 0.45);
//        int right = (int) (pokemonImage.getWidth() * 0.55);

       int top = (int) (pokemonImage.getHeight() * 0.195);
       int bot = (int) (pokemonImage.getHeight() * 0.35);
       int left = (int) (pokemonImage.getWidth() * 0.4);
       int right = (int) (pokemonImage.getWidth() * 0.6);

        Palette.Filter filter = new Palette.Filter() {
            @Override public boolean isAllowed(int rgb, @NonNull float[] hsl) {
                if (hsl[2] > 0.7){
                    //too bright
                    return false;
                }
                if (hsl[2] < 0.2){
                    //too dark
                    return false;
                }
                if (hsl[0] > (30/360f) && hsl[0] < (60/360f)){
                    //Too yellow
                    return false;
                }

                return true;
            }
        };
        Palette.from(pokemonImage).setRegion(left, top, right, bot).addFilter(filter).generate(new Palette
                .PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                int color = p.getDarkVibrantColor(0);
                if (color == 0) { //failed to find dark vibrant color, try getting a dark muted
                    color = p.getDarkMutedColor(0);
                } //Couldnt auto find color, use default primary color
                if (color == 0) {
                    color = Color.rgb(90, 90, 90);
                }

                GUIColorFromPokeType.getInstance().setColor(color);


            }
        });
    }

    /**
     * Get the range of possible levels using candy upgrade cost, if the level is potentially outside the arc-range.
     */
    private LevelRange refineLevelEstimate(int trainerLevel, Optional<Integer> pokemonPowerUpCandyCost,
                                           double estimatedPokemonLevel) {
        if (Data.MAXIMUM_WILD_POKEMON_LEVEL < Data.trainerLevelToMaxPokeLevel(trainerLevel)
                || estimatedPokemonLevel < Data.trainerLevelToMaxPokeLevel(trainerLevel)) {
            return new LevelRange(estimatedPokemonLevel); // No need for level range, arc captured level perfectly.
        }

        if (!pokemonPowerUpCandyCost.isPresent()) {
            return new LevelRange(estimatedPokemonLevel); // Couldn't read power up cost
        }

        int scannedPowerUpCost = pokemonPowerUpCandyCost.get();
        boolean matches = false;
        for (double level = 1; level < Data.MAXIMUM_POKEMON_LEVEL; level += 0.5) {
            if (Data.costForLevel(level).getAnyCandy() == scannedPowerUpCost) {
                matches = true;
                break;
            }
        }
        if (!matches) {
            return new LevelRange(estimatedPokemonLevel); // The scanned power up candy cost is invalid
        }

        // If scanned arc-level is maxed out, we need to consider that the pokemon might have an even higher level.
        double higherBound = estimatedPokemonLevel;
        for (double level = estimatedPokemonLevel + 0.5; level <= Data.MAXIMUM_WILD_POKEMON_LEVEL; level += 0.5) {
            // It either costs candy or candy xl only!
            int powerUpCostForLevel = Data.costForLevel(level).getAnyCandy();
            if (powerUpCostForLevel == scannedPowerUpCost) {
                if (higherBound < level) {
                    // Found a higher level with the same candy power up cost
                    higherBound = level;
                }
            }
        }

        // We know that the lower bound can't be lower than the actual arc-value scanned
        return new LevelRange(estimatedPokemonLevel, higherBound);
    }


    /**
     * Checks if the user has custom screen calibration, and if so, initiates the arc x,y parameters.
     *
     * @param trainerLevel the trainer level to initiate the arc points to.
     */
    private static void ensureCorrectLevelArcSettings(@NonNull GoIVSettings settings, int trainerLevel) {
        if (settings.hasManualScanCalibration()) {
            ScanPoint arcInit = new ScanPoint(ARC_INIT_POINT, settings);
            int arcRadius = Integer.valueOf(settings.getCalibrationValue(ARC_RADIUS));
            Data.setupArcPoints(arcInit, arcRadius, trainerLevel);
        }
    }

    private static int getNavigationBarHeight() {
        Pokefly pokefly = pokeflyRef.get();
        if (pokefly != null) {
            return pokefly.getCurrentNavigationBarHeight();
        } else {
            return 0;
        }
    }
}
