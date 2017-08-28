package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import com.kamron.pogoiv.GoIVSettings;

public class ScanPoint {
    public int xCoord = -1;
    public int yCoord = -1;

    public ScanPoint(String calibrationKey, GoIVSettings settings) {
        String[] values = settings.getCalibrationValue(calibrationKey).split(",");
        xCoord = Integer.valueOf(values[0]);
        yCoord = Integer.valueOf(values[1]);
    }

    public ScanPoint(int x, int y) {
        this.xCoord = x;
        this.yCoord = y;
    }

    @Override public String toString() {
        return xCoord + "," + yCoord;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScanPoint scanPoint = (ScanPoint) o;

        if (xCoord != scanPoint.xCoord) {
            return false;
        }
        return yCoord == scanPoint.yCoord;

    }

    @Override public int hashCode() {
        int result = xCoord;
        result = 31 * result + yCoord;
        return result;
    }
}
