package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class RankLevel {
    @Expose
    private Integer rankLevel;
    @Expose
    private Integer additionalTotalBattlesRequired;
    @Expose
    private Integer additionalWinsRequired;
    @Expose
    private Integer minRatingRequired;

    public Integer getRankLevel() { return rankLevel; }

    public void setRankLevel(Integer rankLevel) { this.rankLevel = rankLevel; }

    public Integer getAdditionalTotalBattlesRequired() { return additionalTotalBattlesRequired; }

    public void setAdditionalTotalBattlesRequired(Integer additionalTotalBattlesRequired) {
        this.additionalTotalBattlesRequired = additionalTotalBattlesRequired;
    }

    public Integer getAdditionalWinsRequired() { return additionalWinsRequired; }

    public void setAdditionalWinsRequired(Integer additionalWinsRequired) {
        this.additionalWinsRequired = additionalWinsRequired;
    }

    public Integer getMinRatingRequired() { return minRatingRequired; }

    public void setMinRatingRequired(Integer minRatingRequired) { this.minRatingRequired = minRatingRequired; }
}
