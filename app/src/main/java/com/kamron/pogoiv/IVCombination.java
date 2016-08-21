package com.kamron.pogoiv;

/**
 * Created by Johan on 2016-08-18.
 * A class which represents an IV value
 */
public class IVCombination {
    public final int att;
    public final int def;
    public final int sta;
    public final int percentPerfect;

    public IVCombination(int att, int def, int sta) {
        this.att = att;
        this.def = def;
        this.sta = sta;
        percentPerfect = (int) Math.round(((att + def + sta) / 45.0) * 100);
    }

}
