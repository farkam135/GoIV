package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class StardustBoost {
    @Expose
    private Double stardustMultiplier;
    @Expose
    private Integer boostDurationMs;

    public Double getStardustMultiplier() { return stardustMultiplier; }

    public void setStardustMultiplier(Double stardustMultiplier) { this.stardustMultiplier = stardustMultiplier; }

    public Integer getBoostDurationMs() { return boostDurationMs; }

    public void setBoostDurationMs(Integer boostDurationMs) { this.boostDurationMs = boostDurationMs; }
}
