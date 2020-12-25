package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CombatCompetitiveSeasonSettings {
    @Expose
    private List<String> seasonEndTimeTimestamp = null;
    @Expose
    private Double ratingAdjustmentPercentage;
    @Expose
    private Double rankingAdjustmentPercentage;

    public List<String> getSeasonEndTimeTimestamp() { return seasonEndTimeTimestamp; }

    public void setSeasonEndTimeTimestamp(List<String> seasonEndTimeTimestamp) {
        this.seasonEndTimeTimestamp = seasonEndTimeTimestamp;
    }

    public Double getRatingAdjustmentPercentage() { return ratingAdjustmentPercentage; }

    public void setRatingAdjustmentPercentage(Double ratingAdjustmentPercentage) {
        this.ratingAdjustmentPercentage = ratingAdjustmentPercentage;
    }

    public Double getRankingAdjustmentPercentage() { return rankingAdjustmentPercentage; }

    public void setRankingAdjustmentPercentage(Double rankingAdjustmentPercentage) {
        this.rankingAdjustmentPercentage = rankingAdjustmentPercentage;
    }
}
