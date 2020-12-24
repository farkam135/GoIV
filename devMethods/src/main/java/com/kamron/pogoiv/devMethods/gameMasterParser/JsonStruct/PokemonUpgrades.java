package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonUpgrades {
    @Expose
    private Integer upgradesPerLevel;
    @Expose
    private Integer allowedLevelsAbovePlayer;
    @SerializedName("candyCost")
    @Expose
    private List<Integer> candyCosts = null;
    @SerializedName("stardustCost")
    @Expose
    private List<Integer> stardustCosts = null;
    @Expose
    private Double shadowStardustMultiplier;
    @Expose
    private Double shadowCandyMultiplier;
    @Expose
    private Double purifiedStardustMultiplier;
    @Expose
    private Double purifiedCandyMultiplier;
    @Expose
    private Integer maxNormalUpgradeLevel;
    @Expose
    private Integer defaultCpBoostAdditionalLevel;
    @Expose
    private Integer xlCandyMinPlayerLevel;
    @SerializedName("xlCandyCost")
    @Expose
    private List<Integer> xlCandyCosts = null;

    public Integer getUpgradesPerLevel() { return upgradesPerLevel; }

    public void setUpgradesPerLevel(Integer upgradesPerLevel) { this.upgradesPerLevel = upgradesPerLevel; }

    public Integer getAllowedLevelsAbovePlayer() { return allowedLevelsAbovePlayer; }

    public void setAllowedLevelsAbovePlayer(Integer allowedLevelsAbovePlayer) { this.allowedLevelsAbovePlayer = allowedLevelsAbovePlayer; }

    public List<Integer> getCandyCosts() { return candyCosts; }

    public void setCandyCosts(List<Integer> candyCosts) { this.candyCosts = candyCosts; }

    public List<Integer> getStardustCosts() { return stardustCosts; }

    public void setStardustCosts(List<Integer> stardustCosts) { this.stardustCosts = stardustCosts; }

    public Double getShadowStardustMultiplier() { return shadowStardustMultiplier; }

    public void setShadowStardustMultiplier(Double shadowStardustMultiplier) { this.shadowStardustMultiplier = shadowStardustMultiplier; }

    public Double getShadowCandyMultiplier() { return shadowCandyMultiplier; }

    public void setShadowCandyMultiplier(Double shadowCandyMultiplier) { this.shadowCandyMultiplier = shadowCandyMultiplier; }

    public Double getPurifiedStardustMultiplier() { return purifiedStardustMultiplier; }

    public void setPurifiedStardustMultiplier(Double purifiedStardustMultiplier) { this.purifiedStardustMultiplier = purifiedStardustMultiplier; }

    public Double getPurifiedCandyMultiplier() { return purifiedCandyMultiplier; }

    public void setPurifiedCandyMultiplier(Double purifiedCandyMultiplier) { this.purifiedCandyMultiplier = purifiedCandyMultiplier; }

    public Integer getMaxNormalUpgradeLevel() { return maxNormalUpgradeLevel; }

    public void setMaxNormalUpgradeLevel(Integer maxNormalUpgradeLevel) { this.maxNormalUpgradeLevel = maxNormalUpgradeLevel; }

    public Integer getDefaultCpBoostAdditionalLevel() { return defaultCpBoostAdditionalLevel; }

    public void setDefaultCpBoostAdditionalLevel(Integer defaultCpBoostAdditionalLevel) { this.defaultCpBoostAdditionalLevel = defaultCpBoostAdditionalLevel; }

    public Integer getXlCandyMinPlayerLevel() { return xlCandyMinPlayerLevel; }

    public void setXlCandyMinPlayerLevel(Integer xlCandyMinPlayerLevel) { this.xlCandyMinPlayerLevel = xlCandyMinPlayerLevel; }

    public List<Integer> getXlCandyCosts() { return xlCandyCosts; }

    public void setXlCandyCosts(List<Integer> xlCandyCosts) { this.xlCandyCosts = xlCandyCosts; }
}
