package com.kamron.pogoiv.scanlogic;

/**
 * Represent a range of CP.
 * Created by Johan on 2016-08-18.
 */
public class CPRange {
    public final int low;
    public final int high;

    public CPRange(int low, int high) {
        this.high = high;
        this.low = low;
    }

    /**
     * Compute integer average of the CP range.
     *
     * @return Integer average of this CP range, truncated.
     */
    public int getAvg() {
        return (low + high) / 2;
    }

    /**
     * Compute floating-point average of the CP range.
     *
     * @return Double average of this CP range.
     */
    public double getFloatingAvg() {
        return (low + high) / 2f;
    }
}
