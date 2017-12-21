package com.kamron.pogoiv.utils;

/**
 * Created by Johan on 2017-12-15.
 * <p>
 * A simple class to represent a minimum and a maximum pokemon level.
 */

public class LeveLRange {

    public double min;
    public double max;


    public LeveLRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a range with both min and max value being the single input
     */
    public LeveLRange(double both) {
        this.min = both;
        this.max = both;
    }

    /**
     * Removes the 'range' and sets both min and max to the same value, based on the previous low value -0.5.
     * Example use :
     * before: lower:5 - higher:7,
     * after-> lower:4,5 - higher: 4.5
     */
    public void dec() {
        min -= 0.5;
        max = min;
    }
    /**
     * Removes the 'range' and sets both min and max to the same value, based on the previous low value +0.5.
     * Example use :
     * before: lower:5 - higher:7,
     * after-> lower:5,5 - higher: 5.5
     */
    public void inc() {
        min += 0.5;
        max = min;
    }

    @Override
    public String toString() {
        if (min == max) {
            return String.valueOf(min);
        } else {
            return String.valueOf(min) + " - " + String.valueOf(max);
        }
    }
}
