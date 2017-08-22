package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import com.kamron.pogoiv.GoIVSettings;

public class ScanPoint {
    public int x = -1;
    public int y = -1;

    public ScanPoint(String calibrationKey, GoIVSettings settings) {
        String[] values = settings.getCalibrationValue(calibrationKey).split(",");
        x = Integer.valueOf(values[0]);
        y = Integer.valueOf(values[1]);
    }

    public ScanPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public String toString() {
        return x + "," + y;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScanPoint scanPoint = (ScanPoint) o;

        if (x != scanPoint.x) {
            return false;
        }
        return y == scanPoint.y;

    }

    @Override public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
