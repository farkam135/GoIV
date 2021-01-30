package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class XpBoost {
    @Expose
    private Double xpMultiplier;
    @Expose
    private Integer boostDurationMs;

    public Double getXpMultiplier() { return xpMultiplier; }

    public void setXpMultiplier(Double xpMultiplier) { this.xpMultiplier = xpMultiplier; }

    public Integer getBoostDurationMs() { return boostDurationMs; }

    public void setBoostDurationMs(Integer boostDurationMs) { this.boostDurationMs = boostDurationMs; }
}
