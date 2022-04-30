package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.R;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.abs;


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

    private static final Scalar SCALAR_ON = new Scalar(255);
    private static final Scalar SCALAR_OFF = new Scalar(0);
    private static final float[] HSV_WHITE_BACKGROUND = new float[] {0, 0f, 0.97f};
    private static final float[] HSV_GREEN_DARK_SMALL = new float[] {166, 0.13f, 0.66f};
    private static final float[] HSV_GREEN_DARK = new float[] {183f, 0.32f, 0.46f};
    private static final float[] HSV_GREEN_LIGHT = new float[] {183f, 0.04f, 0.85f};
    private static final float[] HSV_BLUE_LIGHT = new float[] {197f, 0.12f, 0.93f};
    private static final float[] HSV_TEXT_RED = new float[] {2f, 0.39f, 0.96f};
    private static final float[] HSV_BUTTON_ENABLED = new float[] {147, 0.45f, 0.84f};
    private static final float[] HSV_BUTTON_DISABLED = new float[] {143, 0.05f, 0.90f};
    private static final float[] HSV_HP_BAR = new float[] {155, 0.54f, 0.93f};
    private static final float[] HSV_DIVIDER = new float[] {0, 0, 0.88f};
    private static final float[] HSV_FAB = new float[] {181, 0.68f, 0.62f};


    private final Bitmap bmp;
    private final Mat image;
    private final Mat imageGray;
    private final List<MatOfPoint> contours; // Detected with adaptive threshold
    private final ArrayList<Rect> boundingRectList; // Detected with adaptive threshold
    private final ArrayList<Rect> boundingRectListT; // Detected with binary threshold
    private final Mat mask1;
    private final Mat mask2;
    private final Rect hpBar;
    private final Rect greyHorizontalLine;
    private final Rect greyVerticalLineLeft;
    private final Rect greyVerticalLineRight;
    private final Rect powerUpButton;

    private final float screenshotDensity;
    private final int width20Percent;
    private final int width25Percent;
    private final int width33Percent;
    private final int width50Percent;
    private final int width66Percent;
    private final int width80Percent;
    private final int width90Percent;


    public ScanFieldAutomaticLocator(@NonNull Bitmap bmp, int displayWidth, float displayDensity) {
        this.bmp = bmp;
        // Compute scaled density since the acquired screenshot might be at a lower resolution than the screen
        screenshotDensity = bmp.getWidth() * displayDensity / displayWidth;
        width20Percent = bmp.getWidth() / 5;
        width25Percent = bmp.getWidth() / 4;
        width33Percent = bmp.getWidth() / 3;
        width50Percent = bmp.getWidth() / 2;
        width66Percent = bmp.getWidth() / 3 * 2;
        width80Percent = bmp.getWidth() / 5 * 4;
        width90Percent = bmp.getWidth() / 10 * 9;
        final float buttonHeight = 41f * screenshotDensity;


        // Computer vision parameters
        int adaptThreshBlockSize = Math.round(5 * screenshotDensity);
        if (adaptThreshBlockSize % 2 == 0) {
            adaptThreshBlockSize++;
        }
        double minArea = 25 * screenshotDensity;
        double maxArea = 172800 * screenshotDensity; // 1/4 of 16:9 mdpi screen


        // Prepare image
        image = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, image);

        imageGray = new Mat(image.size(), CvType.CV_8UC4);
        Imgproc.cvtColor(image, imageGray, Imgproc.COLOR_BGR2GRAY);

        Mat imageA = new Mat(image.size(), CvType.CV_32F);
        Imgproc.adaptiveThreshold(imageGray, imageA, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,
                adaptThreshBlockSize, 3);

        Mat imageT = new Mat(image.size(), CvType.CV_32F);
        Imgproc.threshold(imageGray, imageT, 248, 255, Imgproc.THRESH_BINARY_INV);

        // Prepare masks for later (average color computation)
        mask1 = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        mask2 = new Mat(image.rows(), image.cols(), CvType.CV_8U);


        // Find contours
        contours = new ArrayList<>();
        Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> contoursT = new ArrayList<>();
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
                .filter(Predicates.and(ByMinWidth.of(width20Percent), ByMaxHeight.of(8 * screenshotDensity)))
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
                .filter(Predicates.and(ByMinWidth.of(width80Percent), ByMaxHeight.of(5 * screenshotDensity)))
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_DIVIDER, 3, 0.1f, 0.25f))
                .toList();
        if (greyLineCandidates.size() >= 1) {
            Rect maxRect = null;
            for (Rect r : greyLineCandidates) {
                if (r.width >= width90Percent) {
                    // Exclude lines wider than the 90% of the view
                    continue;
                }
                if (maxRect == null) {
                    // Take the first candidate
                    maxRect = r;
                } else if (r.width - maxRect.width > screenshotDensity) {
                    // Take the largest
                    maxRect = r;
                } else if (abs(r.width - maxRect.width) < screenshotDensity && r.y < maxRect.y) {
                    // Take the upper with same width
                    maxRect = r;
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
                    .filter(ByMaxWidth.of(5 * screenshotDensity))
                    .filter(ByMinHeight.of(12.5f * screenshotDensity))
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
                .filter(ByMinY.of(bmp.getHeight() / 2))
                .filter(ByMinHeight.of(buttonHeight))
                .filter(ByMinWidth.of(buttonHeight * 2))
                .filter(Predicates.or(
                        ByHsvColor.of(image, mask1, contours, boundingRectList,
                                HSV_BUTTON_ENABLED, 3, 0.15f, 0.15f),
                        ByHsvColor.of(image, mask2, contours, boundingRectList,
                                HSV_BUTTON_DISABLED, 3, 0.15f,0.15f)))
                .toList();

        if (powerUpButtonCandidates.size() > 0) {
            // Take the upper button
            Rect candidate = powerUpButtonCandidates.get(0);
            for (int i = 1; i < powerUpButtonCandidates.size(); i++) {
                Rect currentCandidate = powerUpButtonCandidates.get(i);
                if (currentCandidate.y < candidate.y) {
                    candidate = currentCandidate;
                }
            }

            // Disabled buttons are OCR'ed merged with the text label at their right: crop it
            if (candidate.width > width50Percent) {
                candidate.width /= 2;
            }

            powerUpButton = candidate;
        } else {
            powerUpButton = null;
        }
    }

    public ScanFieldResults scan(@NonNull Handler mainThreadHandler, @NonNull WeakReference<ProgressDialog> dialog,
                                 @NonNull WeakReference<Context> contextRef) {
        String findingName = null;
        String findingType = null;
        String findingGender = null;
        String findingStardustLabel = null;
        String findingCandyName = null;
        String findingHp = null;
        String findingCp = null;
        String findingCandyAmount = null;
        String findingEvolutionCost = null;
        String findingPowerUpStardustCost = null;
        String findingPowerUpCandyCost = null;
        String findingLevelArc = null;
        String findingWhiteMarker = null;
        String findingGreenMarker = null;
        Context context = contextRef.get();
        if (context != null) {
            findingName = context.getString(R.string.ocr_finding_name);
            findingType = context.getString(R.string.ocr_finding_type);
            findingGender = context.getString(R.string.ocr_finding_gender);
            findingStardustLabel = "Finding STARDUST label";
            findingCandyName = context.getString(R.string.ocr_finding_candy_name);
            findingHp = context.getString(R.string.ocr_finding_hp);
            findingCp = context.getString(R.string.ocr_finding_cp);
            findingCandyAmount = context.getString(R.string.ocr_finding_candy_amount);
            findingEvolutionCost = context.getString(R.string.ocr_finding_evo_cost);
            findingPowerUpStardustCost = context.getString(R.string.ocr_finding_power_up_stardust_cost);
            findingPowerUpCandyCost = context.getString(R.string.ocr_finding_power_up_candy_cost);
            findingLevelArc = context.getString(R.string.ocr_finding_arc_center_and_radius);
            findingWhiteMarker = context.getString(R.string.ocr_finding_white_pixel);
            findingGreenMarker = context.getString(R.string.ocr_finding_green_pixel);
        }

        final ScanFieldResults results = new ScanFieldResults();

        postMessage(mainThreadHandler, dialog.get(), findingName);
        findPokemonNameArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingType);
        findPokemonTypeArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingGender);
        findPokemonGenderArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingCandyAmount);
        findPokemonCandyAmountArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingStardustLabel);
        int stardustHeight = findStardustLabelHeight(results);

        postMessage(mainThreadHandler, dialog.get(), findingCandyName);
        findPokemonCandyNameArea(results, stardustHeight);

        postMessage(mainThreadHandler, dialog.get(), findingHp);
        findPokemonHPArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingCp);
        findPokemonCPScanArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingEvolutionCost);
        findPokemonEvolutionCostArea(results);

        postMessage(mainThreadHandler, dialog.get(), findingPowerUpCandyCost);
        findPokemonPowerUpCandyCostArea(results); // Always call after findPokemonEvolutionCostArea

        postMessage(mainThreadHandler, dialog.get(), findingPowerUpStardustCost);
        findPokemonPowerUpStardustCostArea(results); // Always call after findPokemonEvolutionCostArea

        postMessage(mainThreadHandler, dialog.get(), findingLevelArc);
        findArcValues(results);

        postMessage(mainThreadHandler, dialog.get(), findingWhiteMarker);
        findWhitePixelPokemonScreen(results);

        postMessage(mainThreadHandler, dialog.get(), findingGreenMarker);
        findGreenPixelPokemonScreen(results);

        return results;
    }

    private static void postMessage(@NonNull Handler handler, @Nullable final ProgressDialog dialog,
                                    @Nullable final String message) {
        if (dialog != null && message != null) {
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
                .filter(Predicates.and(ByMinWidth.of(50f * screenshotDensity), ByMinHeight.of(50f * screenshotDensity)))
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
        float[] hsv = new float[3];
        for (int x = 0, y = bmp.getHeight() / 2; x < bmp.getWidth() / 2; x++) {
            Color.colorToHSV(bmp.getPixel(x, y), hsv);
            if (hsv[0] < 3 && hsv[1] < 0.05 && hsv[2] > HSV_WHITE_BACKGROUND[2]) {
                whitePoint = new ScanPoint(x * 2, y);
                break;
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

        if (candidates.size() == 0) {
            return;
        }

        Rect maxBoundingRect = candidates.get(0);
        for (int i = 1; i < candidates.size(); i++) {
            Rect boundingRect = candidates.get(i);
            if (maxBoundingRect.area() < boundingRect.area()) {
                maxBoundingRect = boundingRect;
            }
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRect(maxBoundingRect, c, p);
        }

        // Inspect the stroke width of the arc; start 2.5dp higher
        int y = (int) ((maxBoundingRect.y - 1) + maxBoundingRect.height - Math.round(2.5 * screenshotDensity));
        int arcEndX = maxBoundingRect.x + 2; // Start 2px to the right
        for (; arcEndX < bmp.getWidth() / 2; arcEndX++) {
            //noinspection PointlessBooleanExpression
            if (BuildConfig.DEBUG && debugExecution) {
                bmp.setPixel(arcEndX, y, Color.RED);
            }
            // Beware: OpenCV Mat coordinates are reversed!
            if (imageGray.get(y, arcEndX)[0] < 235) { // Check if the grayscale color is dropping below a threshold
                break; // We're at the arc stroke end! Stop searching!
            }
        }

        double arcStrokeHalfWidth = (arcEndX - (maxBoundingRect.x + 1)) / 2.0;

        results.arcCenter = new ScanPoint(bmp.getWidth() / 2,
                (int) Math.round((maxBoundingRect.y - 2) + maxBoundingRect.height + arcStrokeHalfWidth));
        results.arcRadius = (int) Math.round(bmp.getWidth() / 2.0 - ((maxBoundingRect.x + 2) + arcStrokeHalfWidth));
    }


    /**
     * Find the area where the Pokémons evolution cost is listed. (such as 12 for Pidgey, empty for Lugia)
     */
    private void findPokemonEvolutionCostArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
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
                .filter(ByMinX.of(width50Percent))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        List<Rect> digitsCandidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(Predicates.or(
                        ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK_SMALL, 13, 0.25f,0.25f),
                        ByHsvColor.of(image, mask2, contours, boundingRectList, HSV_TEXT_RED, 10, 0.25f, 0.25f)))
                .toList();

        if (digitsCandidates.size() > 0) {
            candidates = digitsCandidates;
        } else {
            // Didn't find any character for evolution cost. Maybe they are covered by the hamburger menu icon.
            // Try to detect the "candy" icon instead. Its height and width are around 1/3 of its enclosing button.
            candidates  = FluentIterable.from(candidates)
                    .filter(ByHeight.of(powerUpButton.height / 2.75f, powerUpButton.height / 25f))
                    .filter(ByWidth.of(powerUpButton.height / 2.75f, powerUpButton.height / 25f))
                    .toList();
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
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
     * Find the area where the Pokémons power up stardust cost is listed.
     */
    private void findPokemonPowerUpStardustCostArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
        Paint p = null;

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (powerUpButton == null || results.pokemonEvolutionCostArea == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only rect that are between the power up button Y coordinates
                .filter(ByMinY.of(powerUpButton.y))
                .filter(ByMaxY.of(powerUpButton.y + powerUpButton.height))
                // Keep only bounding rect between half width and evolution cost rect start
                .filter(ByMinX.of(width50Percent))
                .filter(ByMaxX.of(results.pokemonEvolutionCostArea.xPoint))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(Predicates.or(
                        ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK_SMALL, 13, 0.25f,0.25f),
                        ByHsvColor.of(image, mask2, contours, boundingRectList, HSV_TEXT_RED, 10, 0.25f, 0.25f)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Increase the width of 10% on the left and 20% on the right
        result.x -= result.width * 0.1;
        result.width += result.width * 0.3;

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonPowerUpStardustCostArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the Pokémons power up candy cost is listed.
     */
    private void findPokemonPowerUpCandyCostArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
        Paint p = null;

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (powerUpButton == null || results.pokemonEvolutionCostArea == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only rect that are between the power up button Y coordinates
                .filter(ByMinY.of(powerUpButton.y))
                .filter(ByMaxY.of(powerUpButton.y + powerUpButton.height))
                // Keep only bounding rect between the candy amount area X coordinates
                .filter(ByMinX.of(results.pokemonEvolutionCostArea.xPoint))
                .filter(ByMaxX.of(results.pokemonEvolutionCostArea.xPoint + results.pokemonEvolutionCostArea.width))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(Predicates.or(
                        ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK_SMALL, 13, 0.25f,0.25f),
                        ByHsvColor.of(image, mask2, contours, boundingRectList, HSV_TEXT_RED, 10, 0.25f, 0.25f)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                /// Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Increase the width of 10% on the left
        result.x -= result.width * 0.1;
        // Make it end exactly where Pokémon evolution cost ends
        result.width = (results.pokemonEvolutionCostArea.xPoint + results.pokemonEvolutionCostArea.width) - result.x;

        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height += result.height * 0.4;

        results.pokemonPowerUpCandyCostArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area that lists how much candy the user currently has of a pokemon. Used for the "pokespam"
     * functionality.
     */
    private void findPokemonCandyAmountArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
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
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.BLUE);
            debugPrintRect(greyHorizontalLine, c, p);
            debugPrintRect(powerUpButton, c, p);
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
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the dark green hue of PoGO text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_DARK, 5, 0.275f, 0.275f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
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
                .filter(ByMinHeight.of(17.5f * screenshotDensity))


                //attempt at cropping out the 'camera' icon so it doesnt make the CP field huge.
                .filter(ByMaxX.of((int) (bmp.getWidth() * 0.7)))
                .filter(ByMinX.of((int) (bmp.getWidth() * 0.3)))
                .filter(ByMaxHeight.of(35f * screenshotDensity))


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
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
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
     * Find the height of the STARDUST label (used fixing the candy name area height)
     */
    private int findStardustLabelHeight(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
        Paint p = null;

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            //debugPrintRectList(boundingRectList, c, p);
        }

        if (  (greyHorizontalLine == null && results.pokemonCandyAmountArea == null)
            || powerUpButton == null) {
            return 0;
        }

        int upperBound;
        if (results.pokemonCandyAmountArea != null) {
            upperBound = results.pokemonCandyAmountArea.yPoint + results.pokemonCandyAmountArea.height;
        } else {
            upperBound = greyHorizontalLine.y + greyHorizontalLine.height;
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.BLUE);
            debugPrintRectList(Collections.singletonList(greyHorizontalLine), c, p);
            debugPrintRectList(Collections.singletonList(powerUpButton), c, p);
            debugPrintLineVertical(width33Percent, c, p);
            debugPrintLineVertical(greyHorizontalLine.x, c, p);
            debugPrintLineHorizontal(upperBound, c, p);
            debugPrintLineHorizontal(powerUpButton.y, c, p);
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 50% of the image width, before the end of the horizontal grey
                // divider line and below it and above the power up button
                .filter(Predicates.and(ByMinX.of(greyHorizontalLine.x),
                        ByMaxX.of(width33Percent),
                        ByMinY.of(upperBound),
                        ByMaxY.of(powerUpButton.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO small text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_LIGHT, 5, 0.125f, 0.090f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return 0;
        }

        Rect result = mergeRectList(candidates);

        // increase the height by 20% on each side (like candy name)
        return result.height;
    }

    /**
     * Find the area where the candy name (such as "eevee candy") is listed.
     */
    private void findPokemonCandyNameArea(ScanFieldResults results, int stardustHeight) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
        Paint p = null;

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (  (greyHorizontalLine == null && results.pokemonCandyAmountArea == null)
            || powerUpButton == null) {
            return;
        }

        int upperBound;
        if (results.pokemonCandyAmountArea != null) {
            upperBound = results.pokemonCandyAmountArea.yPoint + results.pokemonCandyAmountArea.height;
        } else {
            upperBound = greyHorizontalLine.y + greyHorizontalLine.height;
        }

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.BLUE);
            debugPrintRectList(Collections.singletonList(greyHorizontalLine), c, p);
            debugPrintRectList(Collections.singletonList(powerUpButton), c, p);
            debugPrintLineVertical(width33Percent, c, p);
            debugPrintLineVertical(greyHorizontalLine.x + greyHorizontalLine.width, c, p);
            debugPrintLineHorizontal(upperBound, c, p);
            debugPrintLineHorizontal(powerUpButton.y, c, p);
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 50% of the image width, before the end of the horizontal grey
                // divider line and below it and above the power up button
                .filter(Predicates.and(ByMinX.of(width33Percent),
                        ByMaxX.of(greyHorizontalLine.x + greyHorizontalLine.width),
                        ByMinY.of(upperBound),
                        ByMaxY.of(powerUpButton.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO small text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_LIGHT, 5, 0.125f, 0.090f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.GREEN);
            debugPrintRectList(candidates, c, p);
        }

        if (candidates.size() == 0) {
            return;
        }

        Rect result = mergeRectList(candidates);

        // Ensure the rect starts at 33% of the width
        if (result.x > width33Percent) {
            result.x = width33Percent;
        }
        // Ensure the rect reaches the 90% of the width
        if (result.x + result.width < width90Percent) {
            result.width = width90Percent - result.x;
        }

        // Using the height of the stardust label allows us to get a good calibration on wrapped text
        if (stardustHeight > 0) {
            if (abs(stardustHeight - result.height) > Math.min(stardustHeight, result.height) * 0.1) {
                results.candyNameWrapped = true;
            }
            result.height = stardustHeight;
        }
        // Increase the height of 20% on top and 20% below
        result.y -= result.height * 0.2;
        result.height *= 1.4;

        results.candyNameArea = new ScanArea(result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the pokemon type is listed, between weight and height. On the form of "Psychic / flying".
     */
    private void findPokemonTypeArea(ScanFieldResults results) {
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics
        Canvas c = null;
        Paint p = null;

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = getDebugPaint();
            p.setColor(Color.MAGENTA);
            debugPrintRectList(boundingRectList, c, p);
        }

        if (greyHorizontalLine == null || greyVerticalLineLeft == null || greyVerticalLineRight == null) {
            return;
        }
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.BLUE);
            debugPrintRect(greyHorizontalLine, c, p);
            debugPrintRect(greyVerticalLineLeft, c, p);
            debugPrintRect(greyVerticalLineRight, c, p);
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between the two vertical dividers, and the horizontal divider
                .filter(Predicates.and(
                        ByMinX.of(greyVerticalLineLeft.x + greyVerticalLineLeft.width),
                        ByMaxX.of(greyVerticalLineRight.x),
                        ByMinY.of((greyVerticalLineLeft.y + greyVerticalLineRight.y) / 2),
                        ByMaxY.of(greyHorizontalLine.y)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.RED);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the light green hue of PoGO small text
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_GREEN_LIGHT, 5, 0.125f, 0.090f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Remove the outliers
                .filter(ByChauvenetCriterionOnBottomY.of(candidates))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
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
     * Find the area where the pokemon gender is listed.
     */
    private void findPokemonGenderArea(ScanFieldResults results) {
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

        if (hpBar == null || powerUpButton == null) {
            return;
        }

        List<Rect> candidates = FluentIterable.from(boundingRectList)
                // Keep only bounding rect at the right of the hp bar end
                .filter(ByMinX.of(hpBar.x + hpBar.width))
                // Keep only bounding rect that are big enough
                .filter(ByMinY.of(hpBar.y - powerUpButton.height / 2))
                .filter(ByMaxY.of(hpBar.y + hpBar.height + powerUpButton.height / 2))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(candidates, c, p);
        }

        candidates = FluentIterable.from(candidates)
                // Check if the dominant color of the contour matches the color of the hamburger floating action button
                .filter(ByHsvColor.of(image, mask1, contours, boundingRectList, HSV_BLUE_LIGHT, 5, 0.125f, 0.090f))
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

        // Gender area is a rect whose side is at least the power up button half height
        int minSide = powerUpButton.height / 2;
        if (result.width < minSide) {
            result.x -= (minSide - result.width) / 2;
            result.width = minSide;
        }
        if (result.height < minSide) {
            result.y -= (minSide - result.height) / 2;
            result.height = minSide;
        }

        results.pokemonGenderArea = new ScanArea(result.x, result.y, result.width, result.height);
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
        p.setStrokeWidth(screenshotDensity);
        p.setTextSize(10 * screenshotDensity);
        return p;
    }

    private static Rect mergeRectList(List<Rect> rectList) {
        Rect boundingRect = rectList.get(0).clone();

        for (int i = 1; i < rectList.size(); i++) {
            Rect r = rectList.get(i);

            if (r.x < boundingRect.x) {
                boundingRect.width += boundingRect.x - r.x;
                boundingRect.x = r.x;
            }
            if (r.y < boundingRect.y) {
                boundingRect.height += boundingRect.y - r.y;
                boundingRect.y = r.y;
            }
            if (r.x + r.width > boundingRect.x + boundingRect.width) {
                boundingRect.width +=  r.x + r.width - (boundingRect.x + boundingRect.width);
            }
            if (r.y + r.height > boundingRect.y + boundingRect.height) {
                boundingRect.height += r.y + r.height - (boundingRect.y + boundingRect.height);
            }
        }

        return boundingRect;
    }

    @SuppressLint("DefaultLocale")
    private static void debugPrintRect(Rect r, Canvas c, Paint p) {
        c.drawRect(r.x, r.y, r.x + r.width, r.y + r.height, p);
        c.save();
        c.rotate(270f, r.x, r.y);
        c.drawText(String.format("%d,%d %dx%d", r.x, r.y, r.width, r.height), r.x, r.y, p);
        c.restore();
    }

    @SuppressLint("DefaultLocale")
    private static void debugPrintRectList(List<Rect> rectList, Canvas c, Paint p) {
        for (Rect r : rectList) {
            debugPrintRect(r, c, p);
        }
    }

    private static void debugPrintLineVertical(int x, Canvas c, Paint p) {
        c.drawLine(x, 0, x, c.getHeight(), p);
    }

    private static void debugPrintLineHorizontal(int y, Canvas c, Paint p) {
        c.drawLine(0, y, c.getWidth(), y, p);
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
            return input != null && abs(input.width - targetWidth) < delta;
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
            return input != null && abs(input.height - targetHeight) < delta;
        }
    }

    /**
     * The idea behind Chauvenet's criterion is to find a probability band, centered on the mean of a normal
     * distribution, that should reasonably contain all n samples of a data set.
     * Values outside this range are considered outliers and discarded.
     */
    private static class ByChauvenetCriterionOnBottomY implements Predicate<Rect> {
        private NormalDistribution normalDistribution;
        private float significanceLevel;

        private ByChauvenetCriterionOnBottomY(int avgBottom, int stdDeviation, float significanceLevel) {
            if (stdDeviation > 0) {
                this.normalDistribution = new NormalDistribution(avgBottom, stdDeviation);
            }
            this.significanceLevel = significanceLevel;
        }

        public static ByChauvenetCriterionOnBottomY of(List<Rect> rectCollection) {
            if (rectCollection.size() == 0) {
                return new ByChauvenetCriterionOnBottomY(0, 1, 0.5f);
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

            return new ByChauvenetCriterionOnBottomY(avgBottom, stdDeviation, 0.5f / rectCollection.size());
        }

        @Override public boolean apply(@Nullable Rect input) {
            if (input == null) {
                return false;
            }
            if (normalDistribution == null) {
                return true;
            }
            double probabilityValue = normalDistribution.cumulativeProbability(input.y + input.height);
            return probabilityValue > significanceLevel; // Return false if outlier
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
                if ((abs(color[0] - meanHsv[0]) <= dH
                        || meanHsv[0] > 360 + color[0] - dH
                        || meanHsv[0] < -color[0] + dH)
                        && abs(color[1] - meanHsv[1]) <= dS
                        && abs(color[2] - meanHsv[2]) <= dV) {
                    return true;
                }
            }
            return false;
        }
    }
}
