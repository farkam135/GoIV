package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

/**
 * Created by johan on 2017-07-28.
 */

import com.kamron.pogoiv.GoIVSettings;

/**
 * A class that represents an area, used for quickly loading user screen calibration settings.
 */
public class ScanArea {
    public int xPoint = -1;
    public int yPoint = -1;
    public int width = -1;
    public int height = -1;

    /**
     * Create a screen area by reading a setting for a certain part. For example, loading the screen area where
     * the pokemon HP might be.
     *
     * @param calibrationValue The key value used to find the saved user setting for the area, in the form of
     *                         "x,y,x2,y2".
     */
    public ScanArea(String calibrationValue, GoIVSettings settings) {
        String[] values = settings.getCalibrationValue(calibrationValue).split(",");
        xPoint = Integer.valueOf(values[0]);
        yPoint = Integer.valueOf(values[1]);
        width = Integer.valueOf(values[2]);
        height = Integer.valueOf(values[3]);
    }

    public ScanArea(int xPoint, int yPoint, int width, int height) {
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.width = width;
        this.height = height;
    }
}
