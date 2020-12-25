package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Food {
    @SerializedName("itemEffect")
    @Expose
    private List<String> itemEffects = null;
    @SerializedName("itemEffectPercent")
    @Expose
    private List<Double> itemEffectPercentages = null;
    @Expose
    private Double growthPercent;
    @Expose
    private Double berryMultiplier;
    @Expose
    private Double remoteBerryMultiplier;
    @Expose
    private Integer numBuddyAffectionPoints;
    @Expose
    private String mapDurationMs;
    @Expose
    private String activeDurationMs;
    @Expose
    private Integer numBuddyHungerPoints;

    public List<String> getItemEffects() { return itemEffects; }

    public void setItemEffects(List<String> itemEffects) { this.itemEffects = itemEffects; }

    public List<Double> getItemEffectPercentages() { return itemEffectPercentages; }

    public void setItemEffectPercentages(List<Double> itemEffectPercentages) {
        this.itemEffectPercentages = itemEffectPercentages;
    }

    public Double getGrowthPercent() { return growthPercent; }

    public void setGrowthPercent(Double growthPercent) { this.growthPercent = growthPercent; }

    public Double getBerryMultiplier() { return berryMultiplier; }

    public void setBerryMultiplier(Double berryMultiplier) { this.berryMultiplier = berryMultiplier; }

    public Double getRemoteBerryMultiplier() { return remoteBerryMultiplier; }

    public void setRemoteBerryMultiplier(Double remoteBerryMultiplier) {
        this.remoteBerryMultiplier = remoteBerryMultiplier;
    }

    public Integer getNumBuddyAffectionPoints() { return numBuddyAffectionPoints; }

    public void setNumBuddyAffectionPoints(Integer numBuddyAffectionPoints) {
        this.numBuddyAffectionPoints = numBuddyAffectionPoints;
    }

    public String getMapDurationMs() { return mapDurationMs; }

    public void setMapDurationMs(String mapDurationMs) { this.mapDurationMs = mapDurationMs; }

    public String getActiveDurationMs() { return activeDurationMs; }

    public void setActiveDurationMs(String activeDurationMs) { this.activeDurationMs = activeDurationMs; }

    public Integer getNumBuddyHungerPoints() { return numBuddyHungerPoints; }

    public void setNumBuddyHungerPoints(Integer numBuddyHungerPoints) {
        this.numBuddyHungerPoints = numBuddyHungerPoints;
    }
}
