package com.kamron.pogoiv.utils;

/**
 * Created by Johan on 2017-12-15.
 *
 * A simple class to represent a minimum and a maximum.
 */

public class Range {

    public double min;
    public double max;



    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a range with both min and max value being the single input
     * @param both
     */
    public Range(double both) {
        this.min = both;
        this.max = both;
    }

    public void dec() {
        min =- 0.5;
        max =- 0.5;
    }

    public void inc() {
        min =+ 0.5;
        max =+ 0.5;
    }

    public String getTextRepresentation() {
        if (min == max){
            return String.valueOf(min);
        } else{
            return String.valueOf(min) + " - " + String.valueOf(max) ;
        }
    }
}
