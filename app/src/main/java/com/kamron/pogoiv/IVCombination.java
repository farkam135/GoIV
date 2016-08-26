package com.kamron.pogoiv;

import java.util.Arrays;
import java.util.Collections;

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

    /**
     * Returns a boolean array that represent which values are the highest in an iv,
     * Examples: Iv 14-14-7 would be [true,true,false]
     * iv 4,6,1 would be [false, true, false]
     */
    public Boolean[] getHighestStatSignature() {
        Boolean[] attDefSta = new Boolean[3];
        int maxStat = Collections.max(Arrays.asList(att, def, sta));

        attDefSta[0] = att >= maxStat;
        attDefSta[1] = def >= maxStat;
        attDefSta[2] = sta >= maxStat;
        return attDefSta;
    }

    /**
     * get attack + defence + stamina value
     *
     * @return
     */
    public int getTotal() {
        return att + def + sta;
    }

    @Override
    public int hashCode() {
        int result = att;
        result = 31 * result + def;
        result = 31 * result + sta;
        return result;
    }

    public String toString() {
        return "Att: " + this.att + " def: " + this.def + " sta: " + this.sta + " %: " + percentPerfect;
    }
}
