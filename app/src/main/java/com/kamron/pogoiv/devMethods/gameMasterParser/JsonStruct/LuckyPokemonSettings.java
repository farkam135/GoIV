
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LuckyPokemonSettings {

    @SerializedName("powerUpStardustDiscountPercent")
    @Expose
    private Double powerUpStardustDiscountPercent;

    public Double getPowerUpStardustDiscountPercent() {
        return powerUpStardustDiscountPercent;
    }

    public void setPowerUpStardustDiscountPercent(Double powerUpStardustDiscountPercent) {
        this.powerUpStardustDiscountPercent = powerUpStardustDiscountPercent;
    }

}
