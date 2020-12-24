package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CombatRankingProtoSettings {
    @SerializedName("rankLevel")
    @Expose
    private List<RankLevel> rankLevels = null;
    @Expose
    private RequiredForRewards requiredForRewards;
    @Expose
    private Integer minRankToDisplayRating;
    @Expose
    private Integer seasonNumber;

    public List<RankLevel> getRankLevels() { return rankLevels; }

    public void setRankLevels(List<RankLevel> rankLevels) { this.rankLevels = rankLevels; }

    public RequiredForRewards getRequiredForRewards() { return requiredForRewards; }

    public void setRequiredForRewards(
            RequiredForRewards requiredForRewards) { this.requiredForRewards = requiredForRewards; }

    public Integer getMinRankToDisplayRating() { return minRankToDisplayRating; }

    public void setMinRankToDisplayRating(Integer minRankToDisplayRating) { this.minRankToDisplayRating = minRankToDisplayRating; }

    public Integer getSeasonNumber() { return seasonNumber; }

    public void setSeasonNumber(Integer seasonNumber) { this.seasonNumber = seasonNumber; }
}
