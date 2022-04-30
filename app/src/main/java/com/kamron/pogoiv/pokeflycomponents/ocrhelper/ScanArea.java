package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

/**
 * Created by johan on 2017-07-28.
 */

import android.graphics.Rect;
import androidx.annotation.Nullable;

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
     * @param calibrationKey The key value used to find the saved user setting for the area, in the form of
     *                       "x,y,x2,y2".
     */

    @Nullable
    public static ScanArea calibratedFromSettings(String calibrationKey, GoIVSettings settings) {
        return calibratedFromSettings(calibrationKey, settings, 0);
    }

    /**
     * Create a screen area by reading a setting for a certain part. For example, loading the screen area where
     * the pokemon HP might be.
     *
     * @param calibrationKey The key value used to find the saved user setting for the area, in the form of
     *                       "x,y,x2,y2".
     * @param offset Amount to offset the scan region downward to deal with various dynamic UI changes (e.g. lucky)
     */

    @Nullable
    public static ScanArea calibratedFromSettings(String calibrationKey, GoIVSettings settings, int offset) {
        if (settings.hasManualScanCalibration()) {
            try {
                String[] values = settings.getCalibrationValue(calibrationKey).split(",");
                return new ScanArea(Integer.valueOf(values[0]), Integer.valueOf(values[1]) + offset,
                        Integer.valueOf(values[2]), Integer.valueOf(values[3]));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public ScanArea(int xPoint, int yPoint, int width, int height) {
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.width = width;
        this.height = height;
    }

    public boolean contains(Rect r) {
        return xPoint <= r.left && yPoint <= r.top && xPoint + width >= r.right && yPoint + height >= r.bottom;
    }

    @Override public String toString() {
        return xPoint + "," + yPoint + "," + width + "," + height;
    }

    public String toRectString() {
        return "Rect(" + xPoint + ", " + yPoint + " - " + (xPoint + width) + ", " + (yPoint + height) + ")";
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScanArea scanArea = (ScanArea) o;

        if (xPoint != scanArea.xPoint) {
            return false;
        }
        if (yPoint != scanArea.yPoint) {
            return false;
        }
        if (width != scanArea.width) {
            return false;
        }
        return height == scanArea.height;

    }

    @Override public int hashCode() {
        int result = xPoint;
        result = 31 * result + yPoint;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
