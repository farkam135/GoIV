package com.kamron.pogoiv;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.google.common.base.Optional;
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
public class OcrHelper {

    private static OcrHelper instance = null;
    private TessBaseAPI tesseract = null;
    private final LruCache<String, String> ocrCache = new LruCache<>(200);
    private final int heightPixels;
    private final int widthPixels;
    private final boolean candyWordFirst;
    private final String nidoFemale;
    private final String nidoMale;
    private final boolean isPokeSpamEnabled;

    private OcrHelper(String dataPath, int widthPixels, int heightPixels, String nidoFemale, String nidoMale,
                      boolean isPokeSpamEnabled) {
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
        this.isPokeSpamEnabled = isPokeSpamEnabled;
    }

    /**
     * init
     * Initializes the OCR helper and readies it for use
     *
     * @param dataPath Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static OcrHelper init(String dataPath, int widthPixels, int heightPixels, String nidoFemale,
                                 String nidoMale, boolean isPokeSpamEnabled) {
        if (instance == null) {
            instance = new OcrHelper(dataPath, widthPixels, heightPixels, nidoFemale, nidoMale, isPokeSpamEnabled);
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
        //Check if language makes the pokemon name in candy second; France/Spain/Italy have Bonbon/Caramelos pokeName.
        String language = Locale.getDefault().getLanguage();
        HashSet<String> specialCandyOrderLangs = new HashSet<>(Arrays.asList("fr", "es", "it"));
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
     * Get the evolution cost for a pokemon, like getPokemonEvolutionCostFromImg, but without caching.
     *
     * @param evolutionCostImage The precut image of the evolution cost area.
     * @return the evolution cost (or -1 if absent) wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private Optional<Integer> getPokemonEvolutionCostFromImgUncached(Bitmap evolutionCostImage) {
        //clean the image
        //the dark color used for text in pogo is approximately rgb 76,112,114 if you can afford evo
        //and the red color is rgb 255 95 100 when you cant afford the evolution
        Bitmap evolutionCostImageCanAfford = replaceColors(evolutionCostImage, false, 68, 105, 108, Color.WHITE, 28,
                false);
        Bitmap evolutionCostImageCannotAfford = replaceColors(evolutionCostImage, false, 255, 95, 100, Color.WHITE, 17,
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
        Bitmap evolutionCostImage = getImageCrop(pokemonImage, 0.625, 0.88, 0.2, 0.03);
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
        return src.replace("S", "5").replace("s", "5").replace("O", "0").replace("o",
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
        Bitmap name = getImageCrop(pokemonImage, 0.1, 0.45, 0.85, 0.055);
        String hash = "name" + hashBitmap(name);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            name = replaceColors(name, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(name);
            pokemonName = fixOcrNumsToLetters(tesseract.getUTF8Text().replace(" ", ""));
            if (pokemonName.toLowerCase().contains("nidora")) {
                pokemonName = getNidoranGenderName(pokemonImage);
            }
            name.recycle();
            ocrCache.put(hash, pokemonName);
        }
        return pokemonName;
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
        Bitmap candy = getImageCrop(pokemonImage, 0.5, 0.73, 0.47, 0.026);
        String hash = "candy" + hashBitmap(candy);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            candy = replaceColors(candy, true, 68, 105, 108, Color.WHITE, 200, true);
            tesseract.setImage(candy);
            candyName = fixOcrNumsToLetters(
                    removeFirstOrLastWord(tesseract.getUTF8Text().trim().replace("-", " "), candyWordFirst));
            candy.recycle();
            if (candyName.toLowerCase().contains("nidora")) {
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
        Bitmap hp = getImageCrop(pokemonImage, 0.357, 0.52, 0.285, 0.0293);
        String hash = "hp" + hashBitmap(hp);
        String pokemonHPStr = ocrCache.get(hash);

        if (pokemonHPStr == null) {
            hp = replaceColors(hp, true, 55, 66, 61, Color.WHITE, 200, true);
            tesseract.setImage(hp);
            pokemonHPStr = tesseract.getUTF8Text();
            ocrCache.put(hash, pokemonHPStr);
        }
        hp.recycle();

        if (pokemonHPStr.contains("/")) {
            try {
                //If "/" comes at the end we'll get an array with only one component.
                String[] hpParts = pokemonHPStr.split("/");
                String hpStr;
                if (hpParts.length >= 2) {
                    hpStr = hpParts[1];
                } else if (hpParts.length == 1) {
                    hpStr = hpParts[0];
                } else {
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
        Bitmap cp = getImageCrop(pokemonImage, 0.33, 0.064, 0.3, 0.046);
        cp = replaceColors(cp, true, 255, 255, 255, Color.BLACK, 30, false);
        tesseract.setImage(cp);
        String cpText = tesseract.getUTF8Text();

        /*
         * Always remove the two first characters instead of non-numbers: the "CP" text is 
         * sometimes OCR'ed to something containing numbers (e.g. cp, cP, Cp, c3, s3, 73, 53 etc),
         * depending on backgrounds/screen sizes, but it's always OCRed as two characters.
         * This also appears true for translations.
         */
        if (cpText.length() >= 2) { //gastly can block the "cp" text, so its not visible...
            cpText = cpText.substring(2); //remove "cp".
        }

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
                (int)Math.round(widthPixels * .1f), (int)Math.round(heightPixels / 1.714286f),
                (int)Math.round(widthPixels * .8f), (int)Math.round(heightPixels / 25.26316f));
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

        Bitmap candyAmount = getImageCrop(pokemonImage, 0.65, 0.7, 0.15, 0.035);
        String hash = "candyAmount" + hashBitmap(candyAmount);
        String pokemonCandyStr = ocrCache.get(hash);

        if (pokemonCandyStr == null) {
            candyAmount = replaceColors(candyAmount, true, 55, 66, 61, Color.WHITE, 200, true);
            tesseract.setImage(candyAmount);
            pokemonCandyStr = tesseract.getUTF8Text();
            ocrCache.put(hash, pokemonCandyStr);
        }
        candyAmount.recycle();

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
    public ScanResult scanPokemon(Bitmap pokemonImage, int trainerLevel) {
        double estimatedPokemonLevel = getPokemonLevelFromImg(pokemonImage, trainerLevel);
        String pokemonName = getPokemonNameFromImg(pokemonImage);
        String candyName = getCandyNameFromImg(pokemonImage);
        Optional<Integer> pokemonHP = getPokemonHPFromImg(pokemonImage);
        Optional<Integer> pokemonCP = getPokemonCPFromImg(pokemonImage);
        Optional<Integer> pokemonCandyAmount = getCandyAmountFromImg(pokemonImage);
        Optional<Integer> pokemonUpgradeCost = getPokemonEvolutionCostFromImg(pokemonImage);
        String pokemonUniqueIdentifier = getPokemonIdentifierFromImg(pokemonImage);

        return new ScanResult(estimatedPokemonLevel, pokemonName, candyName, pokemonHP, pokemonCP,
                pokemonCandyAmount, pokemonUpgradeCost, pokemonUniqueIdentifier);
    }
}
