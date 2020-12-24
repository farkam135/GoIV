package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class RequiredForRewards {
    @Expose
    private Integer rankLevel;
    @Expose
    private Integer additionalTotalBattlesRequired;

    public Integer getRankLevel() { return rankLevel; }

    public void setRankLevel(Integer rankLevel) { this.rankLevel = rankLevel; }

    public Integer getAdditionalTotalBattlesRequired() { return additionalTotalBattlesRequired; }

    public void setAdditionalTotalBattlesRequired(Integer additionalTotalBattlesRequired) { this.additionalTotalBattlesRequired = additionalTotalBattlesRequired; }
}
