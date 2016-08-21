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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IVCombination that = (IVCombination) o;

        if (att != that.att) return false;
        if (def != that.def) return false;
        return sta == that.sta;

    }

    @Override
    public int hashCode() {
        int result = att;
        result = 31 * result + def;
        result = 31 * result + sta;
        return result;
    }

    public String toString(){
        return "Att: " + this.att + " def: " + this.def + " sta: " + this.sta +" %: " + percentPerfect;
    }
}
