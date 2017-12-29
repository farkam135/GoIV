package com.kamron.pogoiv.utils;

import com.kamron.pogoiv.scanlogic.Data;

/**
 * Created by Johan on 2017-12-15.
 * <p>
 * A simple class to represent a minimum and a maximum pokemon level.
 */

public class LevelRange {

    public double min;
    public double max;


    public LevelRange(double min, double max) {
        if (min < Data.MINIMUM_POKEMON_LEVEL) {
            throw new IllegalArgumentException("Minimum level can't be lesser than " + Data.MINIMUM_POKEMON_LEVEL);
        }
        if (max > Data.MAXIMUM_POKEMON_LEVEL) {
            throw new IllegalArgumentException("Maximum level can't be greater than " + Data.MAXIMUM_POKEMON_LEVEL);
        }
        if (max < min) {
            throw new IllegalArgumentException("Maximum level " + max  + " can't be lesser than minimum level" + min);
        }

        this.min = min;
        this.max = max;
    }

    /**
     * Creates a range with both min and max value being the single input.
     */
    public LevelRange(double both) {
        if (both < Data.MINIMUM_POKEMON_LEVEL) {
            throw new IllegalArgumentException("Level can't be lesser than " + Data.MINIMUM_POKEMON_LEVEL);
        }
        if (both > Data.MAXIMUM_POKEMON_LEVEL) {
            throw new IllegalArgumentException("Level can't be greater than " + Data.MAXIMUM_POKEMON_LEVEL);
        }

        this.min = both;
        this.max = both;
    }

    /**
     * Removes the 'range' and sets both min and max to the same value, based on the previous low value -0.5.
     * Example use :
     * before: lower:5 - higher:7,
     * after-> lower:4.5 - higher: 4.5.
     */
    public void dec() {
        min = Math.max(min - 0.5, Data.MINIMUM_POKEMON_LEVEL);
        max = min;
    }

    /**
     * Removes the 'range' and sets both min and max to the same value, based on the previous low value +0.5.
     * Example use :
     * before: lower:5 - higher:7,
     * after-> lower:5.5 - higher: 5.5.
     */
    public void inc() {
        min = Math.min(min + 0.5, Data.MAXIMUM_POKEMON_LEVEL);
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
