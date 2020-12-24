package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class BuddyLevelSettings {
    @Expose
    private String level;
    @Expose
    private Integer minNonCumulativePointsRequired;
    @Expose
    private List<String> unlockedTraits = null;

    public String getLevel() { return level; }

    public void setLevel(String level) { this.level = level; }

    public Integer getMinNonCumulativePointsRequired() { return minNonCumulativePointsRequired; }

    public void setMinNonCumulativePointsRequired(Integer minNonCumulativePointsRequired) { this.minNonCumulativePointsRequired = minNonCumulativePointsRequired; }

    public List<String> getUnlockedTraits() { return unlockedTraits; }

    public void setUnlockedTraits(List<String> unlockedTraits) { this.unlockedTraits = unlockedTraits; }
}
