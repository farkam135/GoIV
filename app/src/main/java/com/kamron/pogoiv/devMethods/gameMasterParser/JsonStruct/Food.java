
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Food {

    @SerializedName("itemEffect")
    @Expose
    private List<String> itemEffect = null;
    @SerializedName("itemEffectPercent")
    @Expose
    private List<Double> itemEffectPercent = null;
    @SerializedName("growthPercent")
    @Expose
    private Double growthPercent;
    @SerializedName("berryMultiplier")
    @Expose
    private Double berryMultiplier;

    public List<String> getItemEffect() {
        return itemEffect;
    }

    public void setItemEffect(List<String> itemEffect) {
        this.itemEffect = itemEffect;
    }

    public List<Double> getItemEffectPercent() {
        return itemEffectPercent;
    }

    public void setItemEffectPercent(List<Double> itemEffectPercent) {
        this.itemEffectPercent = itemEffectPercent;
    }

    public Double getGrowthPercent() {
        return growthPercent;
    }

    public void setGrowthPercent(Double growthPercent) {
        this.growthPercent = growthPercent;
    }

    public Double getBerryMultiplier() {
        return berryMultiplier;
    }

    public void setBerryMultiplier(Double berryMultiplier) {
        this.berryMultiplier = berryMultiplier;
    }

}
