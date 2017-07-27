package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by johan on 2017-07-27.
 * <p>
 * A class which can find points and fields in a bmp of the pokemon screen, such as where the pokemon name is, where
 * the hp is, etc.
 */

public class ScanFieldAutomaticLocator {


    private static final int greenInt = Color.parseColor("#1d8696"); // 1d8696 is the color used in pokemon go as green
    // background rgb (29 134 150)


    /**
     * Method used during debuging  / creation of point finding algorithms, just draws a dot which can be seen on hte
     * bitmap.
     *
     * @param bmp   The bmp to mark with a square "dot"
     * @param point The point on the bitmap to mark
     * @param size  The size of one of the sides of the dot in pixels
     * @param color The color the dot should be (use Color.parseColor("#FF0000") for example)
     */
    void debugWriteDot(Bitmap bmp, Point point, int size, int color) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                if (((point.x - size / 2) + x) < bmp.getWidth() && ((point.y - size / 2) + y) < bmp.getHeight()) {
                    bmp.setPixel((point.x - size / 2) + x, (point.y - size / 2) + y, color);
                }

            }
        }
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
                    debugWriteDot(bmp, bottomPoint, 30, Color.parseColor("#FF0000"));
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
                debugWriteDot(bmp, topPoint, 30, Color.parseColor("#00FF00"));
                break;
            }
        }

        //Find the dot 3/4ths up from the lowest point

        int newY = (bottomPoint.y - topPoint.y) / 4;

        Point finalPoint = new Point(bottomPoint.x, topPoint.y + newY);
        debugWriteDot(bmp, finalPoint, 20, Color.parseColor("#00FFFF"));


        return finalPoint.x + "," + finalPoint.y;
    }



    /**
     * Get the x,y coordinate of the white pixel in the top left corner of where the white area begins in the pokemon
     * screen.
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "123,123"
     */
    String findWhitePixelPokemonScreen(Bitmap bmp) {
        return "120,120";
    }


    /**
     * Find the "leftmost" part of the level arc. (The left endpoint of the arc).
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "123,123"
     */
    String findArcInit(Bitmap bmp) {
        return "200,1000";
    }

    /**
     * Find the radius of the level arc.
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "123,123"
     */
    String findArcRadius(Bitmap bmp) {
        return "123";
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
    String findPokemonCandyArea(Bitmap bmp) {
        return "1,1,1,1";
    }


    /**
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonScanArea(Bitmap bmp) {
        return "1,1,1,1";
    }

    /**
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonHPArea(Bitmap bmp) {
        return "1,1,1,1";
    }

    /**
     * Find the area where the candy name (such as "eevee candy") is listed
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonCandyNameArea(Bitmap bmp) {
        return "1,1,1,1";
    }

    /**
     * Find the area where the pokemon type is listed, between weight and height. On the form of "Psychic / flying".
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonTypeArea(Bitmap bmp) {
        return "1,1,1,1";
    }

    /**
     * Find the area where the pokemon name is listed (The part that the user can manually change to a nickname).
     *
     * @param bmp The image to analyze.
     * @return A string representation of the x,y coordinate in the form of "x,y,x2,y2"
     */
    String findPokemonNameArea(Bitmap bmp) {
        return "1,1,1,1";
    }
}
