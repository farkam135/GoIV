
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendshipMilestoneSettings {

    @SerializedName("milestoneXpReward")
    @Expose
    private Integer milestoneXpReward;
    @SerializedName("attackBonusPercentage")
    @Expose
    private Double attackBonusPercentage;
    @SerializedName("unlockedTrading")
    @Expose
    private List<String> unlockedTrading = null;
    @SerializedName("minPointsToReach")
    @Expose
    private Integer minPointsToReach;
    @SerializedName("raidBallBonus")
    @Expose
    private Integer raidBallBonus;
    @SerializedName("tradingDiscount")
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

    public void setAttackBonusPercentage(Double attackBonusPercentage) {
        this.attackBonusPercentage = attackBonusPercentage;
    }

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
