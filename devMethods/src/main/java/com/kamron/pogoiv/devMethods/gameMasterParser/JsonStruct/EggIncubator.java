package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class EggIncubator {
    @Expose
    private String incubatorType;
    @Expose
    private Integer uses;
    @Expose
    private Double distanceMultiplier;

    public String getIncubatorType() { return incubatorType; }

    public void setIncubatorType(String incubatorType) { this.incubatorType = incubatorType; }

    public Integer getUses() { return uses; }

    public void setUses(Integer uses) { this.uses = uses; }

    public Double getDistanceMultiplier() { return distanceMultiplier; }

    public void setDistanceMultiplier(Double distanceMultiplier) { this.distanceMultiplier = distanceMultiplier; }
}
