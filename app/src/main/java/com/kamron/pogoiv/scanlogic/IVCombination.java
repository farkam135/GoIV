package com.kamron.pogoiv.scanlogic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
        percentPerfect = Math.round((att + def + sta) / 45f * 100);
    }

    public static IVCombination MAX = new IVCombination(15, 15, 15);
    public static IVCombination MIN = new IVCombination(0, 0, 0);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IVCombination that = (IVCombination) o;

        return att == that.att && def == that.def && sta == that.sta;
    }

    /**
     * Get the highest of the stats in the iv combination.
     * For example, in the iv combination 7 5 11
     * this method would return 11.
     *
     * @return a number between 0 and 15 which is the highest stat in this combination
     */
    public int getHighestStat() {
        return Collections.max(Arrays.asList(att, def, sta));
    }

    /**
     * Returns a boolean array that represent which values are the highest in an iv.
     * Examples: Iv 14-14-7 would be [true,true,false]
     * iv 4,6,1 would be [false, true, false]
     */
    public Boolean[] getHighestStatSignature() {
        Boolean[] attDefSta = new Boolean[3];
        int maxStat = getHighestStat();

        attDefSta[0] = att >= maxStat;
        attDefSta[1] = def >= maxStat;
        attDefSta[2] = sta >= maxStat;
        return attDefSta;
    }

    /**
     * Get total IV (attack + defence + stamina) value.
     *
     * @return total IV
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

    /**
     * A comparator that compares IVCombinations by their total.
     * Note: this comparator imposes orderings that are inconsistent with equals: compare(o1, o2) == 0 does not imply
     * o1.equals(o2).
     */
    public static Comparator<IVCombination> totalComparator = new Comparator<IVCombination>() {
        @Override public int compare(IVCombination o1, IVCombination o2) {
            return o1.getTotal() - o2.getTotal();
        }
    };
}
