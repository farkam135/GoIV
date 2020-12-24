package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class FriendshipMilestoneSettings {
    @Expose
    private Integer milestoneXpReward;
    @Expose
    private Double attackBonusPercentage;
    @Expose
    private List<String> unlockedTrading = null;
    @Expose
    private Integer minPointsToReach;
    @Expose
    private Integer raidBallBonus;
    @Expose
    private Double tradingDiscount;

    public Integer getMilestoneXpReward() {
        return milestoneXpReward;
    }

    public void setMilestoneXpReward(Integer milestoneXpReward) {
        this.milestoneXpReward = milestoneXpReward;
    }

    public Double getAttackBonusPercentage() {
        return attackBonusPercentage;
    }

    public void setAttackBonusPercentage(Double attackBonusPercentage) { this.attackBonusPercentage = attackBonusPercentage; }

    public List<String> getUnlockedTrading() {
        return unlockedTrading;
    }

    public void setUnlockedTrading(List<String> unlockedTrading) {
        this.unlockedTrading = unlockedTrading;
    }

    public Integer getMinPointsToReach() {
        return minPointsToReach;
    }

    public void setMinPointsToReach(Integer minPointsToReach) {
        this.minPointsToReach = minPointsToReach;
    }

    public Integer getRaidBallBonus() {
        return raidBallBonus;
    }

    public void setRaidBallBonus(Integer raidBallBonus) {
        this.raidBallBonus = raidBallBonus;
    }

    public Double getTradingDiscount() {
        return tradingDiscount;
    }

    public void setTradingDiscount(Double tradingDiscount) {
        this.tradingDiscount = tradingDiscount;
    }

}

