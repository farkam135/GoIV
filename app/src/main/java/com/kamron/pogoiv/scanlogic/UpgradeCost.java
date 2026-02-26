package com.kamron.pogoiv.scanlogic;

/**
 * Created by Johan on 2016-08-18.
 */
public class UpgradeCost {
    public final int dust;
    public final int candy;
    public final int candyXl;

    public UpgradeCost(int dust, int candy, int candyXl) {
        this.dust = dust;
        this.candy = candy;
        this.candyXl = candyXl;
    }

    public int getAnyCandy() {
        return candy > 0 ? candy : candyXl;
    }
}
