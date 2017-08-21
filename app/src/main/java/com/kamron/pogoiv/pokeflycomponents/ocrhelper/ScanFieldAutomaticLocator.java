package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.kamron.pogoiv.BuildConfig;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by johan on 2017-07-27.
 * <p>
 * A class which can find points and fields in a bmp of the pokemon screen, such as where the pokemon name is, where
 * the hp is, etc.
 */

public class ScanFieldAutomaticLocator {

    private static final int pureWhite = Color.parseColor("#FFFFFF"); // hp, pokemon name text etc
    private static final int darkTextColorInt = Color.parseColor("#49696c"); // hp, pokemon name text etc
    private static final int hpBarColorInt = Color.parseColor("#6dedb7");  //old color 81ecb6
    private static final int whiteInt = Color.parseColor("#FAFAFA");
    private static final int greenInt = Color.parseColor("#1d8696"); // 1d8696 is the color used in pokemon go as green
    // background rgb (29 134 150)
    private static final Scalar SCALAR_ON = new Scalar(255);
    private static final Scalar SCALAR_OFF = new Scalar(0);
    private static final float[] HSV_GREEN_DARK = new float[] {183f, 0.32f, 0.46f};
    private static final float[] HSV_GREEN_LIGHT = new float[] {183f, 0.13f, 0.67f};
    private static final float[] HSV_GREY_LIGHT = new float[] {0f, 0f, 0.88f};


    /**
     * Method used during debuging  / creation of point finding algorithms, just draws a dot which can be seen on hte
     * bitmap.
     *
     * @param bmp   The bmp to mark with a square "dot"
     * @param point The point on the bitmap to mark
     * @param size  The size of one of the sides of the dot in pixels
     * @param color The color the dot should be (use Color.parseColor("#FF0000") for example)
     */
    private void debugWriteDot(Bitmap bmp, Point point, int size, int color) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int cx = (point.x - size / 2) + x;
                int cy = (point.y - size / 2) + y;
                if (cx >= 0 && cx < bmp.getWidth() && cy >= 0 && cy < bmp.getHeight()) {
                    bmp.setPixel(cx, cy, color);
                }
            }
        }
    }

    private String pointToString(Point p) {
        if (p == null) {
            return "0,0"; //error
        }
        return p.x + "," + p.y;
    }

    /**
     * Get the x,y coordinate of the green pixel in the "hamburger menu" in the bottom right of the pokemon screen.
     * (The dark green color.).
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "123,123"
     */
    String findGreenPokemonScreen(Bitmap bmp) {

        Point bottomPoint = null;
        for (int y = bmp.getHeight() - 1; y > 0; y--) {
            if (bottomPoint != null) {
                break; // already found, stop looping
            }
            for (int x = bmp.getWidth() - 1; x > 0; x--) {
                if (greenInt == bmp.getPixel(x, y)) {
                    System.out.println("Found it");
                    bottomPoint = new Point(x, y);
                    break;
                }
            }
        }

        Point topPoint = null;
        for (int i = 0; i < bmp.getHeight() / 2; i++) {
            int nextPx = bmp.getPixel(bottomPoint.x, bottomPoint.y - i);
            int lumiosity = Color.red(nextPx) + Color.green(nextPx) + Color.blue(nextPx);
            if (lumiosity > 650) {
                topPoint = new Point(bottomPoint.x, bottomPoint.y - i);
                break;
            }
        }

        //Find the dot 3/4ths up from the lowest point

        int newY = (bottomPoint.y - topPoint.y) / 4;

        Point finalPoint = new Point(bottomPoint.x, topPoint.y + newY);


        return pointToString(finalPoint);
    }


    /**
     * Get the x,y coordinate of the white pixel in the top left corner of where the white area (the card under the
     * 3d pokemon screen) begins in the pokemon
     * screen.
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "123,123"
     */
    String findWhitePixelPokemonScreen(Bitmap bmp) {
        Point whitePoint = null;

        int y = (int) (bmp.getHeight() * 0.5);
        for (int x = 0; x < bmp.getWidth(); x++) {
            if (whitePoint != null) {
                break; //Found our white point, stop looking
            }
            if (bmp.getPixel(x, y) == whiteInt) {
                whitePoint = new Point(x * 2, y);
            }
            bmp.setPixel(x, y, Color.parseColor("#00FF00"));

        }

        return pointToString(whitePoint);

    }


    /**
     * Find the "leftmost" part of the level arc. (The left endpoint of the arc). Returns both the radius and the
     * point of starting, in the form of x,y;radius
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate and radiusin the form of "123,123;123"
     */
    String findArcValues(Bitmap bmp) {


        // find where the white area begins
        int whiteCardStartY = 0;
        for (int y = (int) (bmp.getHeight() * 0.5); y > 0; y--) {
            int thisPixel = bmp.getPixel((int) (bmp.getWidth() * 0.07), y);
            if (thisPixel != whiteInt && !isLikelyDarkText(thisPixel)) {
                whiteCardStartY = y;
                break;
            }
        }


        Point finalPoint = null;
        for (int x = 1; x < bmp.getWidth() * 0.25; x++) {
            for (int y = whiteCardStartY - 3; y > bmp.getHeight() * 0.1; y--) {
                int r = Color.red(bmp.getPixel(x, y));
                int g = Color.red(bmp.getPixel(x, y));
                int b = Color.red(bmp.getPixel(x, y));

                //if (bmp.getPixel(x,y) == whiteInt){
                if (r > 235 && g > 235 && b > 235) {
                    finalPoint = new Point(x, y);
                    break;
                }
            }
            if (finalPoint != null) {
                break;
            }
        }

        if (finalPoint != null) {
            debugWriteDot(bmp, finalPoint, 30, Color.parseColor("#0000FF"));


            int arcRadius = bmp.getWidth() / 2 - finalPoint.x;
            int middle = bmp.getWidth() / 2;
            return middle + "," + finalPoint.y + ";" + arcRadius;
        }
        return "1,1;1";
    }


    /**
     * Find the area where the pokemons upgrade cost is listed. (such as 12 for pidgey, empty for lugia)
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonUpgradeCostArea(Bitmap bmp) {
        return "1,1,1,1";
    }

    /**
     * Find the area that lists how much candy the user currently has of a pokemon. Used for the "pokespam"
     * functionallity.
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    @SuppressLint("DefaultLocale")
    String findPokemonCandyArea(Bitmap bmp, Mat image, List<MatOfPoint> contours, List<Rect> boundingRectList) {
        final float screenDensity = Resources.getSystem().getDisplayMetrics().density;
        final float charHeight = 12 * screenDensity;
        final float greyLineHeight = 2 * screenDensity;
        final int width33Percent = bmp.getWidth() / 3;
        final int width50Percent = bmp.getWidth() / 2;
        final int width80Percent = bmp.getWidth() / 5 * 4;
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        Canvas c = null;
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(screenDensity);
            p.setColor(Color.MAGENTA);

            debugPrintRectList(boundingRectList, c, p);
        }

        // Find horizontal grey divider line
        List<Rect> greyLineCandidates = FluentIterable.from(boundingRectList)
                .filter(ByHeight.of(greyLineHeight, screenDensity / 2))
                .filter(ByMinWidth.of(width80Percent))
                .toList();
        Rect greyLine = greyLineCandidates.get(0);

        List<Rect> results = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 50% and 83% of the image width
                .filter(Predicates.and(ByMinX.of(width50Percent), ByMaxX.of(width33Percent + width50Percent)))
                // Keep only bounding rect below the grey divider line
                .filter(ByMinY.of(greyLine.y + greyLine.height))
                // Try to guess the 'mon candy characters basing on their height
                .filter(ByHeight.of(charHeight, screenDensity / 2))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(results, c, p);
        }

        results = FluentIterable.from(results)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(results, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(results, c, p);
        }

        Mat mask = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        results = FluentIterable.from(results)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(ByHsvColor.of(image, mask, contours, boundingRectList, HSV_GREEN_DARK, 3, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(results, c, p);
        }

        Rect result = mergeRectList(results);

        // Ensure the rect is wide at least 33% of the screen width
        if (result.x > width50Percent) {
            result.x = width50Percent;
        }
        if (result.width < width33Percent) {
            result.width = width33Percent;
        }

        return String.format("%d,%d,%d,%d", result.x, result.y, result.width, result.height);
    }


    /**
     * Get the CP field of a pokemon. (The one at the top of the screen, on the form of CP XXX)
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonCPScanArea(Bitmap bmp) {

        int cpStartY = 0;
        for (int y = (int) (bmp.getHeight() * 0.05); y < bmp.getHeight() * 0.5; y++) {

            int half = bmp.getWidth() / 2;
            boolean found1 = bmp.getPixel((int) (half - half * 0.001), y) == pureWhite;
            boolean found2 = bmp.getPixel((int) (half - half * 0.01), y) == pureWhite;
            boolean found3 = bmp.getPixel((int) (half - half * 0.003), y) == pureWhite;
            boolean found4 = bmp.getPixel((int) (half - half * 0.007), y) == pureWhite;
            boolean found5 = bmp.getPixel((int) (half + half * 0.001), y) == pureWhite;
            boolean found6 = bmp.getPixel((int) (half + half * 0.01), y) == pureWhite;
            boolean found7 = bmp.getPixel((int) (half + half * 0.003), y) == pureWhite;
            boolean found8 = bmp.getPixel((int) (half + half * 0.007), y) == pureWhite;
            if (found1 || found2 || found3 || found4 || found5 || found6 || found7 || found8) {
                cpStartY = y;
                break;
            }


        }
        int cpEndY = getEndOfText(bmp, cpStartY, (int) (bmp.getWidth() * 0.2), pureWhite);


        int height = cpEndY - cpStartY;

        int startX = (int) (bmp.getWidth() * 0.333);
        int scanAreaWidth = (int) (bmp.getWidth() * 0.333);

        return startX + "," + cpStartY + "," + scanAreaWidth + "," + height;
    }

    /**
     * Helper method, that tries to find the end of a text field. It does so by looping through from the left, and
     * checking if it "collides with" any text.
     *
     * @param bmp               The image to look in
     * @param textStartY        Where we know there's some text
     * @param startSearchX      the "start" we look from from the left
     * @param searchingForColor The color of the text we want to find the end for
     * @return the first row where we dont encounter the searchingforcolor, or 0 if none found.
     */
    private int getEndOfText(Bitmap bmp, int textStartY, int startSearchX, int searchingForColor) {
        int returner = 0;
        for (int y = textStartY + 3; y < textStartY + (bmp.getHeight() * 0.2); y++) {
            boolean traveledRowEncounteringText = false;
            for (int x = startSearchX; x < bmp.getWidth() / 2; x++) {
                if (bmp.getPixel(x, y) == searchingForColor) {
                    traveledRowEncounteringText = true;
                    break;
                }
            }
            if (traveledRowEncounteringText == false) {
                returner = y;
                break;
            }
        }
        return returner;
    }

    /**
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonHPArea(Bitmap bmp) {
        int hpBarStartY = 0;

        for (int y = 0; y < bmp.getHeight(); y++) {
            if (bmp.getPixel(bmp.getWidth() / 2, y) == hpBarColorInt) {
                hpBarStartY = y;
                break;
            }
        }


        int hpBarEndY = 0;


        for (int y = hpBarStartY; y < bmp.getHeight(); y++) {

            if (bmp.getPixel(bmp.getWidth() / 2, y) != hpBarColorInt) {
                hpBarEndY = y;
                break;
            }
        }


        int hpTextStartY = 0;
        for (int y = hpBarEndY + 5; y < bmp.getHeight() * 0.75; y++) {
            //we could be unlucky and "miss" the hp text by going straight down from the hp bar, by going between two
            // characters, so to decrease that risk, we do it on several parallel lines.
            int half = bmp.getWidth() / 2;
            boolean found1 = bmp.getPixel((int) (half - half * 0.001), y) != whiteInt;
            boolean found2 = bmp.getPixel((int) (half - half * 0.01), y) != whiteInt;
            boolean found3 = bmp.getPixel((int) (half - half * 0.003), y) != whiteInt;
            boolean found4 = bmp.getPixel((int) (half - half * 0.007), y) != whiteInt;
            boolean found5 = bmp.getPixel((int) (half + half * 0.001), y) != whiteInt;
            boolean found6 = bmp.getPixel((int) (half + half * 0.01), y) != whiteInt;
            boolean found7 = bmp.getPixel((int) (half + half * 0.003), y) != whiteInt;
            boolean found8 = bmp.getPixel((int) (half + half * 0.007), y) != whiteInt;

            if (found1 || found2 || found3 || found4 || found5 || found6 || found7 || found8) {
                hpTextStartY = y;
                break;
            }

        }


        int hpTextStartX = 0;

        for (int x = (int) (bmp.getWidth() * 0.1); x < bmp.getWidth() / 2; x++) {
            if (isLikelyDarkText(bmp.getPixel(x, hpTextStartY + 3))) {
                hpTextStartX = x;
                break;
            }
        }


        int hpStartXWithPadding = Math.max(0, (int) (hpTextStartX - (bmp.getWidth() * 0.05)));


        int hpTextEndY = 0;
        for (int y = hpTextStartY + 3; y < hpTextStartY + (bmp.getHeight() * 0.2); y++) {
            boolean traveledRowEncounteringText = false;
            for (int x = hpStartXWithPadding; x < bmp.getWidth() / 2; x++) {
                if (isLikelyDarkText(bmp.getPixel(x, y))) {
                    traveledRowEncounteringText = true;
                    break;
                }
            }
            if (traveledRowEncounteringText == false) {
                hpTextEndY = y;
                break;
            }
        }

        int returnX = hpStartXWithPadding;
        int returnY = hpTextStartY - 3; // -3 for some slight padding upwards
        int returnHeight = hpTextEndY - hpTextStartY;
        int returnWidth = (bmp.getWidth()) - 2 * hpStartXWithPadding;


        return returnX + "," + returnY + "," + returnWidth + "," + returnHeight;

    }

    private boolean isLikelyDarkText(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return (r < 180 && g < 180 && b < 190);
    }

    /**
     * Find the area where the candy name (such as "eevee candy") is listed
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    @SuppressLint("DefaultLocale")
    String findPokemonCandyNameArea(Bitmap bmp, Mat image, List<MatOfPoint> contours, List<Rect> boundingRectList) {
        final float screenDensity = Resources.getSystem().getDisplayMetrics().density;
        final float charHeight = 8 * screenDensity;
        final float greyLineHeight = 2 * screenDensity;
        final int width33Percent = bmp.getWidth() / 3;
        final int width50Percent = bmp.getWidth() / 2;
        final int width80Percent = bmp.getWidth() / 5 * 4;
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        Canvas c = null;
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(screenDensity);
            p.setColor(Color.MAGENTA);

            debugPrintRectList(boundingRectList, c, p);
        }

        // Find horizontal grey divider line
        List<Rect> greyLineCandidates = FluentIterable.from(boundingRectList)
                .filter(ByHeight.of(greyLineHeight, screenDensity / 2))
                .filter(ByMinWidth.of(width80Percent))
                .toList();
        Rect greyLine = greyLineCandidates.get(0);

        List<Rect> results = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 50% and 83% of the image width
                .filter(Predicates.and(ByMinX.of(width50Percent), ByMaxX.of(width33Percent + width50Percent)))
                // Keep only bounding rect below the grey divider line
                .filter(ByMinY.of(greyLine.y + greyLine.height))
                // Try to guess the 'mon candy characters basing on their height
                .filter(ByHeight.of(charHeight, screenDensity / 2))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(results, c, p);
        }

        results = FluentIterable.from(results)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(results, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(results, c, p);
        }

        Mat mask = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        results = FluentIterable.from(results)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(ByHsvColor.of(image, mask, contours, boundingRectList, HSV_GREEN_LIGHT, 3, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(results, c, p);
        }

        Rect result = mergeRectList(results);

        // Ensure the rect is wide at least 33% of the screen width
        if (result.x > width50Percent) {
            result.x = width50Percent;
        }
        if (result.width < width33Percent) {
            result.width = width33Percent;
        }

        return String.format("%d,%d,%d,%d", result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the pokemon type is listed, between weight and height. On the form of "Psychic / flying".
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    @SuppressLint("DefaultLocale")
    String findPokemonTypeArea(Bitmap bmp, Mat image, List<MatOfPoint> contours, List<Rect> boundingRectList) {
        final float screenDensity = Resources.getSystem().getDisplayMetrics().density;
        final float charHeight = 8 * screenDensity;
        final int width33Percent = bmp.getWidth() / 3;
        final int width66Percent = bmp.getWidth() * 2 / 3;
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        Canvas c = null;
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(screenDensity);
            p.setColor(Color.MAGENTA);

            debugPrintRectList(boundingRectList, c, p);
        }

        List<Rect> results = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 33% and 66% of the image width
                .filter(Predicates.and(ByMinX.of(width33Percent), ByMaxX.of(width66Percent)))
                // Try to guess the 'mon name characters basing on their height
                .filter(ByHeight.of(charHeight, screenDensity / 2))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(results, c, p);
        }

        results = FluentIterable.from(results)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(results, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(results, c, p);
        }

        Mat mask = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        results = FluentIterable.from(results)
                // Check if the dominant color of the contour matches the light green hue of PoGO text
                .filter(ByHsvColor.of(image, mask, contours, boundingRectList, HSV_GREEN_LIGHT, 3, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(results, c, p);
        }

        Rect result = mergeRectList(results);

        // Ensure the rect is wide at least 33% of the screen width
        if (result.x > width33Percent) {
            result.x = width33Percent;
        }
        if (result.width < width33Percent) {
            result.width = width33Percent;
        }

        return String.format("%d,%d,%d,%d", result.x, result.y, result.width, result.height);
    }

    /**
     * Find the area where the pokemon name is listed (The part that the user can manually change to a nickname).
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    @SuppressLint("DefaultLocale")
    String findPokemonNameArea(Bitmap bmp, Mat image, List<MatOfPoint> contours, List<Rect> boundingRectList) {
        final float screenDensity = Resources.getSystem().getDisplayMetrics().density;
        final float charUppercaseHeight = 17 * screenDensity;
        final float charLowercaseHeight = 12 * screenDensity;
        final int width33Percent = bmp.getWidth() / 3;
        final int width66Percent = bmp.getWidth() * 2 / 3;
        final boolean debugExecution = false; // Activate this flag to display the onscreen debug graphics

        Canvas c = null;
        Paint p = null;
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            c = new Canvas(bmp);
            p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(screenDensity);
            p.setColor(Color.MAGENTA);

            debugPrintRectList(boundingRectList, c, p);
        }

        List<Rect> results = FluentIterable.from(boundingRectList)
                // Keep only bounding rect between 33% and 66% of the image width
                .filter(Predicates.and(ByMinX.of(width33Percent), ByMaxX.of(width66Percent)))
                // Try to guess the 'mon name characters basing on their height
                .filter(Predicates.or(ByHeight.of(charUppercaseHeight, screenDensity / 2),
                        ByHeight.of(charLowercaseHeight, screenDensity / 2)))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.RED);
            debugPrintRectList(results, c, p);
        }

        results = FluentIterable.from(results)
                // Keep only rect with bottom coordinate inside half of the standard deviation
                .filter(ByStandardDeviationOnBottomY.of(results, 0.5f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.YELLOW);
            debugPrintRectList(results, c, p);
        }

        Mat mask = new Mat(image.rows(), image.cols(), CvType.CV_8U);
        results = FluentIterable.from(results)
                // Check if the dominant color of the contour matches the dark green hue of PoGO text
                .filter(ByHsvColor.of(image, mask, contours, boundingRectList, HSV_GREEN_DARK, 3, 0.275f, 0.325f))
                .toList();

        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && debugExecution) {
            //noinspection ConstantConditions
            p.setColor(Color.GREEN);
            debugPrintRectList(results, c, p);
        }

        Rect result = mergeRectList(results);

        // Ensure the pokemon name rect is wide at least 33% of the screen width
        if (result.x > width33Percent) {
            result.x = width33Percent;
        }
        if (result.width < width33Percent) {
            result.width = width33Percent;
        }

        return String.format("%d,%d,%d,%d", result.x, result.y, result.width, result.height);
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

    private static void debugPrintRectList(List<Rect> rectList, Canvas c, Paint p) {
        for (Rect r : rectList) {
            c.drawRect(r.x, r.y, r.x + r.width, r.y + r.height, p);
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
                if (Math.abs(color[0] - meanHsv[0]) <= dH
                        && Math.abs(color[1] - meanHsv[1]) <= dS
                        && Math.abs(color[2] - meanHsv[2]) <= dV) {
                    return true;
                }
            }
            return false;
        }
    }
}
