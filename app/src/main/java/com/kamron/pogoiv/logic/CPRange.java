package com.kamron.pogoiv.logic;

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
    public int getAvg() {
        return (low + high) / 2;
    }
}
