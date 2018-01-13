package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.LruCache;

import com.google.common.base.Optional;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.utils.LevelRange;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private static final Scalar TEXT_GREEN_DARK = new Scalar(68, 105, 108, 255);
    private static final Scalar TEXT_GREEN_LIGHT = new Scalar(150, 170, 170, 255);
    private static final Scalar TEXT_RED = new Scalar(255, 115, 115, 255);


    private static OcrHelper instance;
    private static String nidoFemale;
    private static String nidoMale;
    private static String nidoUngendered;
    private static TessBaseAPI tesseract;
    private static boolean isPokeSpamEnabled;
    private static LruCache<String, String> ocrCache;
    private static LruCache<String, String> appraisalCache;
    private static LruCache<ScanArea, Bitmap> cropBitmapCache;
    private static boolean candyWordFirst;
    private static int adaptThreshBlockSize;


    static {
        System.loadLibrary("opencv_java3");
    }


    private OcrHelper() {
    }

    /**
     * Initializes the OCR helper and readies it for use.
     *
     * @param dataPath Path the OCR data files.
     * @return Bitmap with replaced colors
     */
    public static synchronized OcrHelper init(@NonNull String dataPath,
                                              @NonNull PokeInfoCalculator pokeInfoCalculator,
                                              @NonNull GoIVSettings settings) {
        if (instance == null) {
            tesseract = new TessBaseAPI();
            tesseract.init(dataPath, "eng");
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
            tesseract.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!/♀♂");

            nidoFemale = pokeInfoCalculator.get(28).name;
            nidoMale = pokeInfoCalculator.get(31).name;
            nidoUngendered = nidoFemale.replace("♀", "").toLowerCase();

            ocrCache = new LruCache<>(200);
            appraisalCache = new LruCache<>(200);
            cropBitmapCache = new LruCache<>(20);

            candyWordFirst = isCandyWordFirst();

            instance = new OcrHelper();
        }

        isPokeSpamEnabled = settings.isPokeSpamEnabled();

        Map<String, String> appraisalMap = settings.loadAppraisalCache();
        for (Map.Entry<String, String> entry : appraisalMap.entrySet()) {
            appraisalCache.put(entry.getKey(), entry.getValue());
        }

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
        appraisalCache = null;
    }

    /**
     * Sets up adaptive threshold parameters. This can be helpful when the screen capture API returns a screenshot not
     * in full resolution; this make it possible to adjust our adaptive threshold call so it always uses the right block
     * size, even if the screenshot width and DisplayMetrics screen width differs.
     * @param imageWidth    Width in pixels of the screenshot width
     * @param screenWidth   Width in pixels of the device display
     * @param screenDensity Density factor of the device display
     */
    @VisibleForTesting
    static void computeAdaptiveThresholdBlockSize(int imageWidth, int screenWidth, float screenDensity) {
        float screenshotDensity = imageWidth * screenDensity / screenWidth;

        adaptThreshBlockSize = Math.round(5 * screenshotDensity);
        if (adaptThreshBlockSize % 2 == 0) {
            adaptThreshBlockSize++;
        }
    }

    private static boolean isCandyWordFirst() {
        // Check if language makes the pokemon name in candy second; France/Spain/Italy/Portuguese
        // have Bonbon/Caramelos/Caramelle/Doces pokeName
        String language = Locale.getDefault().getLanguage();
        List<String> specialCandyOrderLanguages = Arrays.asList("fr", "es", "it", "pt");
        return specialCandyOrderLanguages.contains(language);
    }

    private static String runOcr(@NonNull ScanArea scanArea,
                                 @NonNull Mat mat,
                                 boolean multiLine) {
        // For each ScanArea we allocate one Bitmap and reuse it in any subsequent call to optimize memory usage
        Bitmap b = cropBitmapCache.get(scanArea);
        if (b == null) {
            b = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            cropBitmapCache.put(scanArea, b);
        }
        Utils.matToBitmap(mat, b);

        tesseract.setImage(b);
        if (multiLine) {
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK); // Set text block mode
        } else {
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE); // Set single line mode
        }
        return tesseract.getUTF8Text().trim();
    }

    private static void threshold(@NonNull Mat src, @NonNull Mat dst, double threshold) {
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(dst, dst, threshold, 255, Imgproc.THRESH_BINARY);
    }

    private static void invertedThreshold(@NonNull Mat src, @NonNull Mat dst) {
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(dst, dst, 240, 255, Imgproc.THRESH_BINARY_INV);
    }

    private static void adaptiveThreshold(@NonNull Mat src, @NonNull Mat dst) {
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(dst, dst, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,
                adaptThreshBlockSize, 3);
    }

    /**
     * Set values to MAX_VAL if rgba1-20 < value < rgba1+20 or rgba2-20 < value < rgba2+20, 0 otherwise.
     * @param src   Source image, must be 4 channels RGBA
     * @param rgba1 First color to be used as threshold
     * @param rgba2 Second color to be used as threshold
     */
    private static void biColorThreshold(@NonNull Mat src, @NonNull Mat dst, Scalar rgba1, Scalar rgba2) {
        Mat a = new Mat();
        colorThreshold(src, a, rgba1);
        Mat b = new Mat();
        colorThreshold(src, b, rgba2);
        Core.bitwise_or(a, b, dst);
    }

    /**
     * Set values to MAX_VAL if rgba-20 < value < rgba+20, 0 otherwise.
     * @param src   Source image, must be 4 channels RGBA
     * @param dst   Destination image
     * @param rgba  Color to be used as threshold
     */
    private static void colorThreshold(@NonNull Mat src, @NonNull Mat dst, @NonNull Scalar rgba) {
        colorThreshold(src, dst, rgba, 40);
    }

    /**
     * Set values to MAX_VAL if rgba-rangeWidth/2 < value < rgba+rangeWidth/2, 0 otherwise.
     * @param src        Source image, must be 4 channels RGBA
     * @param dst        Destination image
     * @param rgba       Color to be used as threshold
     * @param rangeWidth The range of values to be considered valid
     */
    private static void colorThreshold(@NonNull Mat src, @NonNull Mat dst, @NonNull Scalar rgba, int rangeWidth) {
        int d = rangeWidth / 2;
        Scalar lower = new Scalar(rgba.val[0] - d, rgba.val[1] - d, rgba.val[2] - d, rgba.val[3]);
        Scalar upper = new Scalar(rgba.val[0] + d, rgba.val[1] + d, rgba.val[2] + d, rgba.val[3]);
        Core.inRange(src, lower, upper, dst);
    }

    /**
     * Scans the arc and tries to determine the pokemon level, returns 1 if nothing found.
     *
     * @param pokemonImage The image of the entire screen
     * @return the estimated pokemon level, or 1 if nothing found
     */
    @VisibleForTesting
    static double getPokemonLevelFromImg(@NonNull Mat pokemonImage, int trainerLevel) {
        // Take only the upper half
        Mat subMat = pokemonImage.submat(0, pokemonImage.rows() / 2, 0, pokemonImage.cols());

        invertedThreshold(subMat, subMat);

        double estimatedPokemonLevel = Data.trainerLevelToMaxPokeLevel(trainerLevel);
        double previousEstPokemonLevel = estimatedPokemonLevel + 0.5; // Initial value out of range
        int previousLevelDistance = -1; // Initial value indicating no found white pixels
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            int index = Data.maxPokeLevelToIndex(estPokemonLevel);
            int x = Data.arcX[index];
            int y = Data.arcY[index];
            int whiteLineDistance = getCardinalWhiteLineDistFromImg(subMat, x, y);

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
     * @param pokemonImage The image of the entire screen (with inverted colors!)
     * @param x            Horizontal ordinate to scan from
     * @param y            Vertical ordinate to scan from
     * @return -1 if the given coordinate is not a white pixel, otherwise the distance from given
     * coordinate which is white in each cardinal direction.
     */
    private static int getCardinalWhiteLineDistFromImg(@NonNull Mat pokemonImage, int x, int y) {
        final double targetColor = 0; // 0 = black  255 = white  (look for black since the image is inverted)

        Double angle = null;
        int r = -1;
        int i1x = x;
        int i1y = y;
        int i2x = x;
        int i2y = y;

        while (pokemonImage.get(i1y, i1x)[0] == targetColor || pokemonImage.get(i2y, i2x)[0] == targetColor) {
            r++;
            if (angle == null) {
                angle = Math.atan2(Data.arcInitY - y, Data.arcInitX - x);
            }
            i1x = (int) Math.round(x + r * Math.cos(angle));
            i1y = (int) Math.round(y + r * Math.sin(angle));
            i2x = (int) Math.round(x - r * Math.cos(angle));
            i2y = (int) Math.round(y - r * Math.sin(angle));
            if (i1x < 0 || i1x >= pokemonImage.width()
                    || i1y < 0 || i1y >= pokemonImage.height()
                    || i2x < 0 || i2x >= pokemonImage.width()
                    || i2y < 0 || i2y >= pokemonImage.height()) {
                return -1;
            }
        }

        return r;
    }

    /**
     * Get the evolution cost for a pokemon, example, weedle: 12.
     * If there was no detected upgrade cost, returns -1.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the evolution cost (or -1 if absent) wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private static Optional<Integer> getPokemonEvolutionCostFromImg(@NonNull Mat pokemonImage,
                                                                    @Nullable ScanArea evolutionCostArea) {
        if (evolutionCostArea == null) {
            evolutionCostArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.625, 0.815, 0.2, 0.03);
        }

        Mat subMat = getMatCrop(pokemonImage, evolutionCostArea);

        biColorThreshold(subMat, subMat, TEXT_GREEN_DARK, TEXT_RED);

        String hash = "candyCost" + hashMat(subMat);

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

        // If not cached or fully evolved, ocr text
        Optional<Integer> result;
        String ocrText = fixOcrLettersToDigits(runOcr(evolutionCostArea, subMat, false));
        try {
            int evoCost = Integer.parseInt(ocrText);
            if (evoCost == 10 || evoCost == 1) { //second zero hidden behind floating button
                evoCost = 100;
            } else if (evoCost == 40 || evoCost == 4) { //second zero hidden behind floating button
                evoCost = 400; //damn magikarp
            } else if (evoCost == 5) {
                evoCost = 50; //second zero hidden behind button
            } else if (evoCost == 2) {
                evoCost = 25; //5 hidden behind button
            }
            result = Optional.of(evoCost);
        } catch (NumberFormatException e) {
            result = Optional.absent(); //could not ocr text
        }

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
     * Get the power up stardust cost for a pokemon.
     *
     * @param pokemonImage The image of the full pokemon screen
     * @return the power up cost wrapped in Optional.of(), or Optional.absent() on scan failure
     */
    private static Optional<Integer> getPokemonPowerUpStardustCostFromImg(@NonNull Mat pokemonImage,
                                                                          @Nullable ScanArea powerUpStardustCostArea) {
        if (powerUpStardustCostArea == null) {
            powerUpStardustCostArea = new ScanArea(
                    pokemonImage.width(), pokemonImage.height(), 0.544, 0.803, 0.139, 0.0247);
        }
        Mat subMat = getMatCrop(pokemonImage, powerUpStardustCostArea);

        biColorThreshold(subMat, subMat, TEXT_GREEN_DARK, TEXT_RED);

        String hash = "powerUpStardustCost" + hashMat(subMat);

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

        String ocrResult = fixOcrLettersToDigits(runOcr(powerUpStardustCostArea, subMat, false));
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
    private static Optional<Integer> getPokemonPowerUpCandyCostFromImg(@NonNull Mat pokemonImage,
                                                                       @Nullable ScanArea powerUpCandyCostArea) {
        if (powerUpCandyCostArea == null) {
            powerUpCandyCostArea = new ScanArea(
                    pokemonImage.width(), pokemonImage.height(), 0.73, 0.742, 0.092, 0.0247);
        }
        Mat subMat = getMatCrop(pokemonImage, powerUpCandyCostArea);

        biColorThreshold(subMat, subMat, TEXT_GREEN_DARK, TEXT_RED);

        String hash = "powerUpCandyCost" + hashMat(subMat);

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

        String ocrResult = fixOcrLettersToDigits(runOcr(powerUpCandyCostArea, subMat, false));
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
     * Get the hashcode for an OpenCV Mat.
     * @param mat The Mat to be hash coded
     * @return An hexadecimal hashcode
     */
    private static String hashMat(@NonNull Mat mat) {
        int increment = adaptThreshBlockSize / 5; // This is equal to the screen density
        long value = 0;
        for (int i = 0; i < mat.rows(); i += increment) {
            for (int j = 0; j < mat.cols(); j += increment) {
                long nextValue = 0;
                for (int c = 0; c < mat.channels(); c++) {
                    nextValue += mat.get(i, j)[c];
                }
                value += nextValue * i * j;
            }
        }
        return Long.toHexString(value);
    }

    /**
     * Correct some OCR errors in argument where only letters are expected.
     */
    private static String fixOcrDigitsToLetters(String src) {
        StringBuilder sb = new StringBuilder(src.length());
        for (int i = 0; i < src.length(); i++) {
            switch (src.charAt(i)) {
                case '0':
                    sb.append('o');
                    break;
                case '1':
                    sb.append('l');
                    break;
                case '2':
                    sb.append('z');
                    break;
                case '5':
                    sb.append('s');
                    break;
                default:
                    sb.append(src.charAt(i));
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Correct some OCR errors in argument where only numbers are expected.
     */
    private static String fixOcrLettersToDigits(String src) {
        StringBuilder sb = new StringBuilder(src.length());
        for (int i = 0; i < src.length(); i++) {
            switch (src.charAt(i)) {
                case 'O':
                case 'o':
                    sb.append('0');
                    break;
                case 'I':
                case 'i':
                case 'l':
                    sb.append('1');
                    break;
                case 'Z':
                    sb.append('2');
                    break;
                case 'S':
                case 's':
                    sb.append('5');
                    break;
                case 'B':
                    sb.append('8');
                    break;
                default:
                    if (Character.isDigit(src.charAt(i))) {
                        sb.append(src.charAt(i));
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Get the pokemon name as analysed from a pokemon image.
     *
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private static String getPokemonNameFromImg(@NonNull Mat pokemonImage,
                                                @NonNull Pokemon.Gender pokemonGender,
                                                @Nullable ScanArea nameArea) {
        if (nameArea == null) {
            nameArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.1, 0.4125, 0.85, 0.055);
        }
        Mat subMat = getMatCrop(pokemonImage, nameArea);

        threshold(subMat, subMat, 150);

        String hash = "name" + hashMat(subMat);
        String pokemonName = ocrCache.get(hash);

        if (pokemonName == null) {
            pokemonName = runOcr(nameArea, subMat, false);
            if (isNidoranName(fixOcrDigitsToLetters(pokemonName))) {
                pokemonName = getNidoranGenderName(pokemonGender);
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
    private static String getPokemonTypeFromImg(@NonNull Mat pokemonImage, @Nullable ScanArea typeArea) {
        if (typeArea == null) {
            typeArea = new ScanArea(
                    pokemonImage.width(), pokemonImage.height(), 0.365278, 0.572, 0.308333, 0.035156);
        }
        Mat subMat = getMatCrop(pokemonImage, typeArea);

        colorThreshold(subMat, subMat, TEXT_GREEN_LIGHT, 75);

        String hash = "type" + hashMat(subMat);
        String pokemonType = ocrCache.get(hash);

        if (pokemonType == null) {
            pokemonType = fixOcrDigitsToLetters(runOcr(typeArea, subMat, false));
            ocrCache.put(hash, pokemonType);
        }
        return pokemonType;
    }

    /**
     * Get the pokemon gender as analysed from a pokemon image.
     *
     * @param pokemonImage The image of the whole screen
     * @return Gender.M if the pokémon is male, Gender.F if female, Gender.N otherwise
     */
    @VisibleForTesting
    static Pokemon.Gender getPokemonGenderFromImg(@NonNull Mat pokemonImage, @Nullable ScanArea genderArea) {
        if (genderArea == null) {
            genderArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.822, 0.455, 0.0682, 0.03756);
        }
        Mat subMat = getMatCrop(pokemonImage, genderArea);

        adaptiveThreshold(subMat, subMat);

        int width = subMat.width();
        int height = subMat.height();

        // The top left pixel should always be empty
        double bgColor = subMat.get(0, 0)[0];

        // Analyze the gender area to search for ♂ or ♀.
        // Divide it in 2 vertical halves.
        // Scan one line every two and search for the first non white pixel.
        // Sum its X coordinate and repeat. The final sum will be used as score.
        int upperHalfScore = 0;
        int lowerHalfScore = 0;

        // Top
        for (int y = 0; y < height / 2; y += 2) {
            for (int x = 0; x < width; x++) {
                if (subMat.get(y, x)[0] != bgColor) {
                    upperHalfScore += x;
                    break;
                }
            }
        }

        // Bottom
        for (int y = (int) Math.ceil(height / 2f); y < height; y += 2) {
            for (int x = 0; x < width; x++) {
                if (subMat.get(y, x)[0] != bgColor) {
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
     * Get a matrix crop using a scan area.
     *
     * @param mat      The image to crop
     * @param scanArea The area of the image to get
     * @return The crop of the image
     */
    private static Mat getMatCrop(Mat mat, ScanArea scanArea) {
        org.opencv.core.Rect roi = new org.opencv.core.Rect(scanArea.xPoint, scanArea.yPoint,
                scanArea.width, scanArea.height);
        return mat.submat(roi);
    }

    /**
     * Get the correctly gendered name of a pokemon.
     *
     * @param pokemonGender The gender of the nidoranX.
     * @return The correct name of the pokemon, with the gender symbol at the end.
     */
    @NonNull
    private static String getNidoranGenderName(Pokemon.Gender pokemonGender) {
        switch (pokemonGender) {
            case F: return nidoFemale;
            case M: return nidoMale;
            default: return "";
        }
    }

    private static boolean isNidoranName(String pokemonName) {
        return pokemonName.toLowerCase().contains(nidoUngendered);
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
    private static String getCandyNameFromImg(@NonNull Mat pokemonImage,
                                              @NonNull Pokemon.Gender pokemonGender,
                                              @Nullable ScanArea candyNameArea) {
        if (candyNameArea == null) {
            candyNameArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.5, 0.678, 0.47, 0.026);
        }
        Mat subMat = getMatCrop(pokemonImage, candyNameArea);

        colorThreshold(subMat, subMat, TEXT_GREEN_LIGHT, 75);

        String hash = "candy" + hashMat(subMat);
        String candyName = ocrCache.get(hash);

        if (candyName == null) {
            String ocrText = fixOcrDigitsToLetters(runOcr(candyNameArea, subMat, false));
            candyName = removeFirstOrLastWord(ocrText, candyWordFirst);
            if (isNidoranName(candyName)) {
                candyName = getNidoranGenderName(pokemonGender);
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
    private static Optional<Integer> getPokemonHPFromImg(@NonNull Mat pokemonImage, @Nullable ScanArea hpArea) {
        if (hpArea == null) {
            hpArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.357, 0.482, 0.285, 0.0293);
        }
        Mat subMat = getMatCrop(pokemonImage, hpArea);

        colorThreshold(subMat, subMat, TEXT_GREEN_DARK, 150);

        String hash = "hp" + hashMat(subMat);
        String pokemonHPStr = ocrCache.get(hash);

        if (pokemonHPStr == null) {
            pokemonHPStr = runOcr(hpArea, subMat, false);
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

                return Optional.of(Integer.parseInt(fixOcrLettersToDigits(hpStr)));
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
    private Optional<Integer> getPokemonCPFromImg(@NonNull Mat pokemonImage, @Nullable ScanArea cpArea) {
        if (cpArea == null) {
            cpArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.25, 0.059, 0.5, 0.046);
        }
        Mat subMat = getMatCrop(pokemonImage, cpArea);

        threshold(subMat, subMat, 250);

        final int width = subMat.width();
        final int height = subMat.height();

        // Every chunk will contain a character
        ArrayList<Rect> chunks = new ArrayList<>(6);
        Rect currentChunk = null;
        // On devices denser than XHDPI (2x) we can skip a pixel every two (or more) to increase performances
        int increment = (int) Math.max(1, Math.ceil(Resources.getSystem().getDisplayMetrics().density / 2));
        // When we're over a chunk check every pixel instead of skipping so we're sure to find the blank space after it
        for (int x = 0; x < width; x += (currentChunk != null) ? 1 : increment) {
            for (int y = 0; y < height; y += increment) {
                final double pxColor = subMat.get(y, x)[0];

                if (currentChunk == null) {
                    if (pxColor != 0) {
                        // We found a non-black pixel, start a new character chunk
                        currentChunk = new Rect(x, y, x, height - 1);
                        break;
                    } else if (y >= height - increment) {
                        // We reached the end of this column without finding any non-black pixel.
                        // The next one probably wont be the start of a new chunk: skip it.
                        x += increment;
                    }

                } else {
                    if (pxColor != 0) {
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

        if (mergeRect != null) {
            // Zero-out everything outside the detected rect
            Mat mask = Mat.zeros(subMat.size(), CvType.CV_8U);
            MatOfPoint points = new MatOfPoint(
                    new Point(mergeRect.left, mergeRect.top),
                    new Point(mergeRect.right, mergeRect.top),
                    new Point(mergeRect.right, mergeRect.bottom),
                    new Point(mergeRect.left, mergeRect.bottom));
            Imgproc.fillConvexPoly(mask, points, new Scalar(255));
            Core.bitwise_and(subMat, mask, subMat);
        }

        byte[] buff = new byte[(int) (subMat.total() * subMat.channels())];
        subMat.get(0, 0, buff);

        String cpText = fixOcrLettersToDigits(runOcr(cpArea, subMat, false));
        try {
            return Optional.of(Integer.parseInt(cpText));
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
    private static Optional<Integer> getCandyAmountFromImg(@NonNull Mat pokemonImage,
                                                           @Nullable ScanArea candyAmountArea) {
        if (!isPokeSpamEnabled) {
            return Optional.absent();
        }
        if (candyAmountArea == null) {
            candyAmountArea = new ScanArea(pokemonImage.width(), pokemonImage.height(), 0.60, 0.644, 0.20, 0.038);
        }
        Mat subMat = getMatCrop(pokemonImage, candyAmountArea);

        colorThreshold(subMat, subMat, TEXT_GREEN_DARK);

        String hash = "candyAmount" + hashMat(subMat);
        String pokemonCandyStr = ocrCache.get(hash);

        if (pokemonCandyStr == null) {
            pokemonCandyStr = fixOcrLettersToDigits(runOcr(candyAmountArea, subMat, false));
            ocrCache.put(hash, pokemonCandyStr);
        }

        if (pokemonCandyStr.length() > 0) {
            try {
                return Optional.of(Integer.parseInt(pokemonCandyStr));
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
    public ScanResult scanPokemon(@NonNull GoIVSettings settings,
                                  @NonNull Resources res,
                                  @NonNull Bitmap pokemonImage,
                                  int trainerLevel) {
        ensureCorrectLevelArcSettings(settings, trainerLevel); //todo, make it so it doesnt initiate on every scan?

        computeAdaptiveThresholdBlockSize(
                pokemonImage.getWidth(), res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().density);

        // Convert Android Bitmap to OpenCV Matrix
        Mat image = new Mat(pokemonImage.getWidth(), pokemonImage.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(pokemonImage, image);

        // Begin scan
        Optional<Integer> powerUpStardustCost = Optional.absent();
        /*Optional<Integer> powerUpStardustCost = getPokemonPowerUpStardustCostFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_POWER_UP_STARDUST_COST, settings));*/
        Optional<Integer> powerUpCandyCost = getPokemonPowerUpCandyCostFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_POWER_UP_CANDY_COST, settings));

        double estimatedPokemonLevel = getPokemonLevelFromImg(image, trainerLevel);
        LevelRange estimatedLevelRange =
                refineLevelEstimate(trainerLevel, powerUpCandyCost, estimatedPokemonLevel);

        String type = getPokemonTypeFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_TYPE_AREA, settings));
        Pokemon.Gender gender = getPokemonGenderFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_GENDER_AREA, settings));
        String name = getPokemonNameFromImg(image, gender,
                ScanArea.calibratedFromSettings(POKEMON_NAME_AREA, settings));
        String candyName = getCandyNameFromImg(image, gender,
                ScanArea.calibratedFromSettings(CANDY_NAME_AREA, settings));
        Optional<Integer> hp = getPokemonHPFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_HP_AREA, settings));
        Optional<Integer> cp = getPokemonCPFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_CP_AREA, settings));
        Optional<Integer> candyAmount = getCandyAmountFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_CANDY_AMOUNT_AREA, settings));
        Optional<Integer> evolutionCost = getPokemonEvolutionCostFromImg(image,
                ScanArea.calibratedFromSettings(POKEMON_EVOLUTION_COST_AREA, settings));
        String uniqueIdentifier = name + type + candyName + hp.toString() + cp
                .toString() + powerUpStardustCost.toString() + powerUpCandyCost.toString();

        return new ScanResult(estimatedLevelRange, name, type, candyName, gender, hp, cp, candyAmount, evolutionCost,
                powerUpStardustCost, powerUpCandyCost, uniqueIdentifier);
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
        if (!Data.isValidPowerUpCandyCost(scannedPowerUpCost)) {
            return new LevelRange(estimatedPokemonLevel); // The scanned power up candy cost is invalid
        }

        // If scanned arc-level is maxed out, we need to consider that the pokemon might have an even higher level.
        double higherBound = estimatedPokemonLevel;
        for (double level = estimatedPokemonLevel + 0.5; level <= Data.MAXIMUM_WILD_POKEMON_LEVEL; level += 0.5) {
            int powerUpCostForLevel = Data.POWER_UP_CANDY_COSTS[Data.maxPokeLevelToIndex(level)];
            if (powerUpCostForLevel == scannedPowerUpCost) {
                if (higherBound < level) {
                    // Found a higher level with the same candy power up cost
                    higherBound = level;
                }
            } else if (powerUpCostForLevel > scannedPowerUpCost) {
                // Costs are ascending ordered. There won't be a cost equal to the input in the array.
                break;
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


    /**
     * Reads the bottom part of the screen and returns the text there.
     *
     * @param screen The full phone screen.
     * @return String of whats on the bottom of the screen.
     */
    public static String getAppraisalText(@NonNull GoIVSettings settings, @NonNull Bitmap screen) {
        Mat image = new Mat(screen.getWidth(), screen.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(screen, image);

        ScanArea appraisalArea = new ScanArea(image.width(), image.height(), 0.05, 0.822, 0.90, 0.07);
        image = getMatCrop(image, appraisalArea);

        colorThreshold(image, image, TEXT_GREEN_DARK, 100);

        String hash = "appraisal" + hashMat(image);
        String appraisalText = appraisalCache.get(hash);

        if (appraisalText == null) {
            appraisalText = runOcr(appraisalArea, image, true);
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
    public static void removeEntryFromAppraisalCache(@NonNull GoIVSettings settings, @NonNull String hash) {
        appraisalCache.remove(hash);
        settings.saveAppraisalCache(appraisalCache.snapshot());
    }
}
