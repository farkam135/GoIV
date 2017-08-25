package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.kamron.pogoiv.BuildConfig;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by johan on 2017-07-27.
 * <p>
 * A class which can find points and fields in a bmp of the pokemon screen, such as where the pokemon name is, where
 * the hp is, etc.
 */

public class ScanFieldAutomaticLocator {

    static {
        System.loadLibrary("opencv_java3");
    }

    private static final int pureWhite = Color.parseColor("#FFFFFF"); // hp, pokemon name text etc
    private static final int hpBarColorInt = Color.parseColor("#6dedb7");  //old color 81ecb6
    private static final int whiteInt = Color.parseColor("#FAFAFA");
    private static final int greenInt = Color.parseColor("#1d8696"); // 1d8696 is the color used in pokemon go as green
    // background rgb (29 134 150)
    private static final Scalar SCALAR_ON = new Scalar(255);
    private static final Scalar SCALAR_OFF = new Scalar(0);
    private static final float[] HSV_GREEN_DARK_SMALL = new float[] {170, 0.16f, 0.62f};
    private static final float[] HSV_GREEN_DARK = new float[] {183f, 0.32f, 0.46f};
    private static final float[] HSV_GREEN_LIGHT = new float[] {183f, 0.04f, 0.85f};
    private static final float[] HSV_TEXT_RED = new float[] {2f, 0.39f, 0.96f};
    private static final float[] HSV_BUTTON_ENABLED = new float[] {147, 0.45f, 0.84f};
    private static final float[] HSV_HP_BAR = new float[] {155, 0.54f, 0.93f};
    private static final float[] HSV_DIVIDER = new float[] {0, 0, 0.88f};
    private static final float[] HSV_FAB = new float[] {181, 0.68f, 0.62f};


    private final Bitmap bmp;
    private final Mat image;
    private final List<MatOfPoint> contours; // Detected with adaptive threshold
    private final List<MatOfPoint> contoursT; // Detected with binary threshold
    private final ArrayList<Rect> boundingRectList; // Detected with adaptive threshold
    private final ArrayList<Rect> boundingRectListT; // Detected with binary threshold
    private final Mat mask1;
    private final Mat mask2;
    private final Rect hpBar;
    private final Rect greyHorizontalLine;
    private final Rect greyVerticalLineLeft;
    private final Rect greyVerticalLineRight;
    private final Rect powerUpButton;

    private final float screenDensity;
    private final int width20Percent;
    private final int width25Percent;
    private final int width33Percent;
    private final int width50Percent;
    private final int width66Percent;
    private final int width80Percent;

    private final float buttonHeight;


    public ScanFieldAutomaticLocator(Bitmap bmp, float screenDensity) {
        this.bmp = bmp;
        this.screenDensity = screenDensity;
        width20Percent = bmp.getWidth() / 5;
        width25Percent = bmp.getWidth() / 4;
        width33Percent = bmp.getWidth() / 3;
        width50Percent = bmp.getWidth() / 2;
        width66Percent = bmp.getWidth() / 3 * 2;
        width80Percent = bmp.getWidth() / 5 * 4;
        buttonHeight = 41f * screenDensity;


        // Computer vision parameters
        int adaptThreshBlockSize = Math.round(5 * screenDensity);
        if (adaptThreshBlockSize % 2 == 0) {
            adaptThreshBlockSize++;
        }
        double minArea = 25 * screenDensity;
        double maxArea = 172800 * screenDensity; // 1/4 of 16:9 mdpi screen


        // Prepare image
        image = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, image);

        Mat imageHsv = new Mat(image.size(), CvType.CV_8UC4);
        Imgproc.cvtColor(image, imageHsv, Imgproc.COLOR_BGR2GRAY);

        Mat imageA = new Mat(image.size(), CvType.CV_32F);
        Imgproc.adaptiveThreshold(imageHsv, imageA, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,
                adaptThreshBlockSize, 3);

        Mat imageT = new Mat(image.size(), CvType.CV_32F);
        Imgproc.threshold(imageHsv, imageT, 250, 255, Imgproc.THRESH_BINARY_INV);

        // Prepare masks for later (average color computation)
        mask1 = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        mask2 = new Mat(image.rows(), image.cols(), CvType.CV_8U);


        // Find contours
        contours = new ArrayList<>();
        Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        contoursT = new ArrayList<>();
        Imgproc.findContours(imageT, contoursT, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Remove contours too small or too large
        Iterator<MatOfPoint> contoursIterator = contours.iterator();
        while (contoursIterator.hasNext()) {
            MatOfPoint contour = contoursIterator.next();
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea < minArea || contourArea > maxArea) {
                contoursIterator.remove();
            }
        }

        contoursIterator = contoursT.iterator();
        while (contoursIterator.hasNext()) {
            MatOfPoint contour = contoursIterator.next();
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea < minArea || contourArea > maxArea) {
                contoursIterator.remove();
            }
        }


        // Find contours bounding boxes
        boundingRectList = new ArrayList<>(contours.size());
        for (MatOfPoint contour : contours) {
            boundingRectList.add(Imgproc.boundingRect(contour));
        }

        boundingRectListT = new ArrayList<>(contoursT.size());
        for (MatOfPoint contour : contoursT) {
            boundingRectListT.add(Imgproc.boundingRect(contour));
        }


        // Find hp bar
        List<Rect> hpBarCandidates = FluentIterable.from(boundingRectList)
                .filter(Predicates.and(ByMinX.of(width20Percent), ByMaxX.of(width80Percent)))
                .filter(Predicates.and(ByMinWidth.of(width20Percent), ByMaxHeight.of(8 * screenDensity)))
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_HP_BAR, 5, 0.15f, 0.15f))
                .toList();
        if (hpBarCandidates.size() >= 1) { // Take the largest
            Rect maxRect = null;
            for (Rect r : hpBarCandidates) {
                if (maxRect == null || r.area() > maxRect.area()) {
                    maxRect = hpBarCandidates.get(0);
                }
            }
            hpBar = maxRect;
        } else {
            hpBar = null;
        }

        // Find horizontal grey divider line
        List<Rect> greyLineCandidates = FluentIterable.from(boundingRectList)
                .filter(Predicates.and(ByMinWidth.of(width80Percent), ByMaxHeight.of(5 * screenDensity)))
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_DIVIDER, 3, 0.1f, 0.25f))
                .toList();
        if (greyLineCandidates.size() >= 1) { // Take the largest
            Rect maxRect = null;
            for (Rect r : greyLineCandidates) {
                if (maxRect == null || r.area() > maxRect.area()) {
                    maxRect = greyLineCandidates.get(0);
                }
            }
            greyHorizontalLine = maxRect;
        } else {
            greyHorizontalLine = null;
        }

        // Find vertical grey divider lines
        if (hpBar != null && greyHorizontalLine != null) {
            greyLineCandidates = FluentIterable.from(boundingRectList)
                    .filter(Predicates.and(ByMinY.of(hpBar.y + hpBar.height), ByMaxY.of(greyHorizontalLine.y)))
                    .filter(ByMaxWidth.of(5 * screenDensity))
                    .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_DIVIDER, 3, 0.1f, 0.25f))
                    .toList();
            if (greyLineCandidates.size() == 2) {
                if (greyLineCandidates.get(0).x < greyLineCandidates.get(1).x) {
                    greyVerticalLineLeft = greyLineCandidates.get(0);
                    greyVerticalLineRight = greyLineCandidates.get(1);
                } else {
                    greyVerticalLineLeft = greyLineCandidates.get(1);
                    greyVerticalLineRight = greyLineCandidates.get(0);
                }
            } else {
                greyVerticalLineLeft = null;
                greyVerticalLineRight = null;
            }
        } else {
            greyVerticalLineLeft = null;
            greyVerticalLineRight = null;
        }

        // Find power up button. This is always visible, as opposed to evolve button
        List<Rect> powerUpButtonCandidates = FluentIterable.from(boundingRectList)
                .filter(ByHeight.of(buttonHeight, screenDensity))
                .filter(ByMinWidth.of(buttonHeight * 2))
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_BUTTON_ENABLED, 3, 0.15f, 0.15f))
                .toList();

        if (powerUpButtonCandidates.size() > 0) {
            // Take the lower button
            Rect candidate = powerUpButtonCandidates.get(0);
            for (int i = 1; i < powerUpButtonCandidates.size(); i++) {
                Rect currentCandidate = powerUpButtonCandidates.get(i);
                if (currentCandidate.y + currentCandidate.height > candidate.y + candidate.height) {
                    candidate = currentCandidate;
                }
            }
            powerUpButton = candidate;
        } else {
            powerUpButton = null;
        }
    }

    public ScanFieldResults scan(@Nullable Handler mainThreadHandler, @Nullable ProgressDialog dialog) {
        final ScanFieldResults results = new ScanFieldResults();

        postMessage(mainThreadHandler, dialog, "Finding name area");
        findPokemonNameArea(results);

        postMessage(mainThreadHandler, dialog, "Finding type area");
        findPokemonTypeArea(results);

        postMessage(mainThreadHandler, dialog, "Finding candy name area");
        findPokemonCandyNameArea(results);

        postMessage(mainThreadHandler, dialog, "Finding hp area");
        findPokemonHPArea(results);

        postMessage(mainThreadHandler, dialog, "Finding cp area");
        findPokemonCPScanArea(results);

        postMessage(mainThreadHandler, dialog, "Finding candy amount area");
        findPokemonCandyArea(results);

        postMessage(mainThreadHandler, dialog, "Finding evolution cost area");
        findPokemonEvolutionCostArea(results);

        postMessage(mainThreadHandler, dialog, "Finding level arc starting point and radius");
        findArcValues(results);

        postMessage(mainThreadHandler, dialog, "Finding white marker pixel");
        findWhitePixelPokemonScreen(results);

        postMessage(mainThreadHandler, dialog, "Finding green marker pixel");
        findGreenPixelPokemonScreen(results);

        return results;
    }

    private static void postMessage(@Nullable Handler handler, @Nullable final ProgressDialog dialog,
                                    @NonNull final String message) {
        if (handler != null && dialog != null) {
            handler.post(new Runnable() {
                @Override public void run() {
                    dialog.setMessage(message);
                }
            });
        }
    }

    /**
     * Get the x,y coordinate of the green pixel in the "hamburger menu" in the bottom right of the pokemon screen.
     * (The dark green color.).
     */
    private void findGreenPixelPokemonScreen(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (hpBar == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect after the 66% of the image width and height
                .filter(Predicates.and(ByMinX.of(width66Percent), ByMinY.of((int) (bmp.getHeight() / 3f * 2f))))
                // Keep only bounding rect that are big enough
                .filter(Predicates.and(ByMinWidth.of(50f * screenDensity), ByMinHeight.of(50f * screenDensity)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the color of the hamburger floating action button
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_FAB, 5, 0.125f, 0.125f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        results.infoScreenFabGreenPixelPoint = new ScanPoint(
                result.x + result.width / 2, (int) (result.y + result.height / 4f * 3f));
        results.infoScreenFabGreenPixelColor =
                bmp.getPixel(results.infoScreenFabGreenPixelPoint.xCoord, results.infoScreenFabGreenPixelPoint.yCoord);
    }


    /**
     * Get the x,y coordinate of the white pixel in the top left corner of where the white area (the card under the
     * 3d pokemon screen) begins in the pokemon
     * screen.
     */
    private void findWhitePixelPokemonScreen(ScanFieldResults results) {
        ScanPoint whitePoint = null;

        int y = (int) (bmp.getHeight() * 0.5);
        for (int x = 0; x < bmp.getWidth(); x++) {
            if (whitePoint != null) {
                break; //Found our white point, stop looking
            }
            if (bmp.getPixel(x, y) == whiteInt) {
                whitePoint = new ScanPoint(x * 2, y);
            }
        }

        if (whitePoint != null) {
            results.infoScreenCardWhitePixelPoint = whitePoint;
            results.infoScreenCardWhitePixelColor = bmp.getPixel(results.infoScreenCardWhitePixelPoint.xCoord,
                    results.infoScreenCardWhitePixelPoint.yCoord);
        }
    }


    /**
     * Find the "leftmost" part of the level arc. (The left endpoint of the arc). Returns both the radius and the
     * point of starting, in the form of x,y;radius
     */
    private void findArcValues(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectListT, c, p);
        }

        if (boundingRectListT.size() == 0) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectListT)
                // The level arc must be in the upper half of the screen
                .filter(ByMaxY.of(bmp.getHeight() / 2))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        Rect maxBoundingRect = null;
        for (Rect boundingRect : candidates) {
            if (maxBoundingRect == null || maxBoundingRect.area() < boundingRect.area()) {
                maxBoundingRect = boundingRect;
            }
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(Collections.singletonList(maxBoundingRect), c, p);
        }

        //noinspection ConstantConditions
        results.arcCenter = new ScanPoint(bmp.getWidth() / 2, maxBoundingRect.y + maxBoundingRect.height);
        results.arcRadius = bmp.getWidth() / 2 - maxBoundingRect.x;
    }


    /**
     * Find the area where the Pokémons evolution cost is listed. (such as 12 for Pidgey, empty for Lugia)
     */
    private void findPokemonEvolutionCostArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (powerUpButton == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only rect that are below the power up button and above the height of the evolution button
                .filter(ByMinY.of(powerUpButton.y + powerUpButton.height))
                .filter(ByMaxY.of((int) (powerUpButton.y + powerUpButton.height * 2.5f)))
                // Keep only bounding rect in the right 50% of the image
                .filter(ByMinX.of(width50Percent))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        List<Rect> digitsCandidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(Predicates.or(
                        ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK_SMALL, 3, 0.15f, 0.15f),
                        ByHsvColor.of(image, mask2, contours, boundingRectList, HSV_TEXT_RED, 3, 0.15f, 0.15f)))
                .toList();

        if (digitsCandidates.size() > 0) {
            candidates = digitsCandidates;
        } else {
            // Didn't find any character for evolution cost. Maybe they are covered by the hamburger menu icon.
            // Try to detect the "candy" icon instead. Its height is around one third of its enclosing button.
            candidates  = FluentIterable.from(candidates)
                    .filter(ByHeight.of(powerUpButton.height / 2.75f, powerUpButton.height / 25f))
                    .filter(ByWidth.of(powerUpButton.height / 2.75f, powerUpButton.height / 25f))
                    .toList();
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(candidates, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the rect starts at 66% of the width and is wide at least 20% of the screen width
        if (result.x > width66Percent) {
            result.x = width66Percent;
        }
        if (result.width < width20Percent) {
            result.width = width20Percent;
        }

        if (digitsCandidates.size() > 0) {
            // Increase the height of 20% on top and 20% below only if the area wasn't detected through evolution
            // cost digits and not with the candy icon
            result.y -= result.height * 0.2;
            result.height += result.height * 0.4;
        }

        results.pokemonEvolutionCostArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area that lists how much candy the user currently has of a pokemon. Used for the "pokespam"
     * functionality.
     */
    private void findPokemonCandyArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (greyHorizontalLine == null || powerUpButton == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect below the grey divider line and above the power up button
                .filter(Predicates.and(ByMinY.of(greyHorizontalLine.y + greyHorizontalLine.height),
                        ByMaxY.of(powerUpButton.y)))
                // Keep only bounding at the right half of the screen
                .filter(ByMinX.of(width50Percent))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK, 3, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(candidates, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the rect starts at 66% of the width and is wide at least 20% of the screen width
        if (result.x > width66Percent) {
            result.x = width66Percent;
        }
        if (result.width < width20Percent) {
            result.width = width20Percent;
        }

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonCandyAmountArea = new ScanArea(result.x, result.y, result.width, result.height);
    }


    /**
     * Get the CP field of a pokemon. (The one at the top of the screen, on the form of CP XXX)
     */
    private void findPokemonCPScanArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectListT, c, p);
        }

        if (hpBar == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectListT)
                // Keep only bounding rect inside the first 20% of the image height
                .filter(ByMaxY.of(bmp.getHeight() / 5))
                // Ensure it is high enough (this filters out the status bar notification icons)
                .filter(ByMinHeight.of(17.5f * screenDensity))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the pokemon name rect is wide at least 50% of the screen width
        if (result.x > width25Percent) {
            result.x = width25Percent;
        }
        if (result.width < width50Percent) {
            result.width = width50Percent;
        }

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonCpArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Looks for the Pokémon HP area.
     */
    private void findPokemonHPArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (hpBar == null || greyVerticalLineLeft == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between the left and the right of the HP bar
                .filter(Predicates.and(ByMinX.of(hpBar.x), ByMaxX.of(hpBar.x + hpBar.width)))
                // Keep only bounding rect below the hp bar and above the vertical dividers
                .filter(Predicates.and(ByMinY.of(hpBar.y + hpBar.height), ByMaxY.of(greyVerticalLineLeft.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the dark green hue of PoGO text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK, 5, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(candidates, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the pokemon HP rect is wide at least 50% of the HP bar
        if (result.x > width50Percent - hpBar.width / 4) {
            result.x = width50Percent - hpBar.width / 4;
        }
        if (result.width < hpBar.width / 2) {
            result.width = hpBar.width / 2;
        }

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonHpArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the candy name (such as "eevee candy") is listed.
     */
    private void findPokemonCandyNameArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (greyHorizontalLine == null || powerUpButton == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 50% of the image width, before the end of the horizontal grey
                // divider line and below it and above the power up button
                .filter(Predicates.and(ByMinX.of(width50Percent),
                        ByMaxX.of(greyHorizontalLine.x + greyHorizontalLine.width),
                        ByMinY.of(greyHorizontalLine.y + greyHorizontalLine.height),
                        ByMaxY.of(powerUpButton.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO small text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_LIGHT, 5, 0.125f, 0.062f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(candidates, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the rect starts at 50% of the width growing the rect in both directions (to the left and to the right)
        if (result.x > width50Percent) {
            result.width += (result.x - width50Percent) * 2;
            result.x = width50Percent;
        }

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.candyNameArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the pokemon type is listed, between weight and height. On the form of "Psychic / flying".
     */
    private void findPokemonTypeArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (hpBar == null || greyHorizontalLine == null
                || greyVerticalLineLeft == null || greyVerticalLineRight == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between the two vertical dividers, the HP bar and the horizontal divider
                .filter(Predicates.and(
                        ByMinX.of(greyVerticalLineLeft.x + greyVerticalLineLeft.width),
                        ByMaxX.of(greyVerticalLineRight.x),
                        ByMinY.of(hpBar.y + hpBar.height),
                        ByMaxY.of(greyHorizontalLine.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO small text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_LIGHT, 5, 0.125f, 0.062f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(candidates, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the rect is wide at least the space between the two vertical dividers
        if (result.x > greyVerticalLineLeft.x + greyVerticalLineLeft.width) {
            result.x = greyVerticalLineLeft.x + greyVerticalLineLeft.width;
        }
        if (result.x + result.width < greyVerticalLineRight.x) {
            result.width = greyVerticalLineRight.x - result.x;
        }

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonTypeArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the pokemon name is listed (The part that the user can manually change to a nickname).
     */
    private void findPokemonNameArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        //noinspection UnusedAssignment
        Canvas c = null;
        //noinspection UnusedAssignment
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (hpBar == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 20% and 80% of the image width
                .filter(Predicates.and(ByMinX.of(width20Percent), ByMaxX.of(width80Percent)))
                // Keep only bounding rect above the hp bar and below the pokemon area (more or less)
                .filter(Predicates.and(ByMinY.of((int) (hpBar.y * 8.5f / 10f)), ByMaxY.of(hpBar.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the dark green hue of PoGO text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK, 5, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the pokemon name rect is wide at least 50% of the screen width
        if (result.x > width25Percent) {
            result.x = width25Percent;
        }
        if (result.width < width50Percent) {
            result.width = width50Percent;
        }

        // Increase the height of 20% on top and 30% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.5;

        results.pokemonNameArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    private Paint getDebugPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(screenDensity);
        p.setTextSize(10 * screenDensity);
        return p;
    }

    private static Rect mergeRectList(List<Rect> rectList) {
        org.opencv.core.Point[] allEdgesArray = new org.opencv.core.Point[rectList.size() * 2];
        for (int i = 0; i < rectList.size(); i++) {
            Rect r = rectList.get(i);
            allEdgesArray[i * 2] = r.tl();
            allEdgesArray[i * 2 + 1] = r.br();
        }
        MatOfPoint allEdgesMat = new MatOfPoint();
        allEdgesMat.fromArray(allEdgesArray);

        return Imgproc.boundingRect(allEdgesMat);
    }

    @SuppressLint("DefaultLocale")
    private static void debugPrintRectList(List<Rect> rectList, Canvas c, Paint p) {
        for (Rect r : rectList) {
            c.drawRect(r.x, r.y, r.x + r.width, r.y + r.height, p);
            c.save();
            c.rotate(270f, r.x, r.y);
            c.drawText(String.format("%d,%d %dx%d", r.x, r.y, r.width, r.height), r.x, r.y, p);
            c.restore();
        }
    }


    private static class ByMinX implements Predicate<Rect> {
        private int minX;

        private ByMinX(int minX) {
            this.minX = minX;
        }

        public static ByMinX of(int minX) {
            return new ByMinX(minX);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.x >= minX;
        }
    }

    private static class ByMaxX implements Predicate<Rect> {
        private int maxX;

        private ByMaxX(int maxX) {
            this.maxX = maxX;
        }

        public static ByMaxX of(int maxX) {
            return new ByMaxX(maxX);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.x + input.width <= maxX;
        }
    }

    private static class ByMinY implements Predicate<Rect> {
        private int minY;

        private ByMinY(int minY) {
            this.minY = minY;
        }

        public static ByMinY of(int minY) {
            return new ByMinY(minY);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.y >= minY;
        }
    }

    private static class ByMaxY implements Predicate<Rect> {
        private int maxY;

        private ByMaxY(int maxY) {
            this.maxY = maxY;
        }

        public static ByMaxY of(int maxY) {
            return new ByMaxY(maxY);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.y + input.height <= maxY;
        }
    }

    private static class ByMinWidth implements Predicate<Rect> {
        private float minWidth;

        private ByMinWidth(float minWidth) {
            this.minWidth = minWidth;
        }

        public static ByMinWidth of(float minWidth) {
            return new ByMinWidth(minWidth);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.width >= minWidth;
        }
    }

    private static class ByMaxWidth implements Predicate<Rect> {
        private float maxWidth;

        private ByMaxWidth(float maxWidth) {
            this.maxWidth = maxWidth;
        }

        public static ByMaxWidth of(float maxWidth) {
            return new ByMaxWidth(maxWidth);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.width <= maxWidth;
        }
    }

    private static class ByMinHeight implements Predicate<Rect> {
        private float minHeight;

        private ByMinHeight(float minHeight) {
            this.minHeight = minHeight;
        }

        public static ByMinHeight of(float minHeight) {
            return new ByMinHeight(minHeight);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.height >= minHeight;
        }
    }

    private static class ByMaxHeight implements Predicate<Rect> {
        private float maxHeight;

        private ByMaxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
        }

        public static ByMaxHeight of(float maxHeight) {
            return new ByMaxHeight(maxHeight);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && input.height <= maxHeight;
        }
    }

    private static class ByWidth implements Predicate<Rect> {
        private float targetWidth;
        private float delta;

        private ByWidth(float targetWidth, float delta) {
            this.targetWidth = targetWidth;
            this.delta = delta;
        }

        public static ByWidth of(float targetWidth, float delta) {
            return new ByWidth(targetWidth, delta);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && Math.abs(input.width - targetWidth) < delta;
        }
    }

    private static class ByHeight implements Predicate<Rect> {
        private float targetHeight;
        private float delta;

        private ByHeight(float targetHeight, float delta) {
            this.targetHeight = targetHeight;
            this.delta = delta;
        }

        public static ByHeight of(float targetHeight, float delta) {
            return new ByHeight(targetHeight, delta);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && Math.abs(input.height - targetHeight) < delta;
        }
    }

    private static class ByStandardDeviationOnBottomY implements Predicate<Rect> {
        private int avgBottom;
        private int stdDeviation;
        private float deviations;

        private ByStandardDeviationOnBottomY(int avgBottom, int stdDeviation, float deviations) {
            this.avgBottom = avgBottom;
            this.stdDeviation = stdDeviation;
            this.deviations = deviations;
        }

        public static ByStandardDeviationOnBottomY of(List<Rect> rectCollection, float deviations) {
            if (rectCollection.size() == 0) {
                return new ByStandardDeviationOnBottomY(0, 0, deviations);
            }

            // Compute the average bottom Y coordinate
            int sum = 0;
            for (Rect boundRect : rectCollection) {
                sum += boundRect.y + boundRect.height;
            }
            int avgBottom = sum / rectCollection.size();

            // Compute the standard deviation
            double variancesSum = 0;
            for (Rect boundRect : rectCollection) {
                variancesSum += Math.pow(boundRect.y + boundRect.height - avgBottom, 2);
            }
            int stdDeviation = (int) Math.round(Math.sqrt(variancesSum / rectCollection.size()));

            return new ByStandardDeviationOnBottomY(avgBottom, stdDeviation, deviations);
        }

        @Override public boolean apply(@Nullable Rect input) {
            return input != null && (input.y + input.height >= avgBottom - stdDeviation * deviations
                    && input.y + input.height <= avgBottom + stdDeviation * deviations);
        }
    }

    private static class ByHsvColor implements Predicate<Rect> {
        private Mat image;
        private Mat mask;
        private List<MatOfPoint> contours;
        private List<Rect> allBoundingRectList;
        private float[] color;
        private float dH;
        private float dS;
        private float dV;

        private ByHsvColor(Mat image, Mat mask, List<MatOfPoint> contours, List<Rect> allBoundingRectList,
                           float[] hsvColor, float deltaH, float deltaS, float deltaV) {
            this.image = image;
            this.mask = mask;
            this.contours = contours;
            this.allBoundingRectList = allBoundingRectList;
            this.color = hsvColor;
            this.dH = deltaH;
            this.dS = deltaS;
            this.dV = deltaV;
        }

        public static ByHsvColor of(Mat image, Mat mask, List<MatOfPoint> contours, List<Rect> allBoundingRectList,
                                    float[] hsvColor, float deltaH, float deltaS, float deltaV) {
            return new ByHsvColor(image, mask, contours, allBoundingRectList, hsvColor, deltaH, deltaS, deltaV);
        }

        @Override public boolean apply(@Nullable Rect input) {
            if (input != null) {
                float[] meanHsv = new float[3];
                MatOfPoint contour = contours.get(allBoundingRectList.indexOf(input));
                mask.setTo(SCALAR_OFF);
                Imgproc.drawContours(mask, contours, contours.indexOf(contour), SCALAR_ON, -1);
                Scalar meanColor = Core.mean(image, mask);
                Color.RGBToHSV((int) meanColor.val[0], (int) meanColor.val[1], (int) meanColor.val[2], meanHsv);
                if ((Math.abs(color[0] - meanHsv[0]) <= dH
                        || meanHsv[0] > 360 + color[0] - dH
                        || meanHsv[0] < -color[0] + dH)
                        && Math.abs(color[1] - meanHsv[1]) <= dS
                        && Math.abs(color[2] - meanHsv[2]) <= dV) {
                    return true;
                }
            }
            return false;
        }
    }
}
