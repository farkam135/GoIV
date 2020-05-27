
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PokemonUpgrades {

    @SerializedName("upgradesPerLevel")
    @Expose
    private Integer upgradesPerLevel;
    @SerializedName("allowedLevelsAbovePlayer")
    @Expose
    private Integer allowedLevelsAbovePlayer;
    @SerializedName("candyCost")
    @Expose
    private List<Integer> candyCost = null;
    @SerializedName("stardustCost")
    @Expose
    private List<Integer> stardustCost = null;

    public Integer getUpgradesPerLevel() {
        return upgradesPerLevel;
    }

    public void setUpgradesPerLevel(Integer upgradesPerLevel) {
        this.upgradesPerLevel = upgradesPerLevel;
    }

    public Integer getAllowedLevelsAbovePlayer() {
        return allowedLevelsAbovePlayer;
    }

    public void setAllowedLevelsAbovePlayer(Integer allowedLevelsAbovePlayer) {
        this.allowedLevelsAbovePlayer = allowedLevelsAbovePlayer;
    }

    public List<Integer> getCandyCost() {
        return candyCost;
    }

    public void setCandyCost(List<Integer> candyCost) {
        this.candyCost = candyCost;
    }

    public List<Integer> getStardustCost() {
        return stardustCost;
    }

    public void setStardustCost(List<Integer> stardustCost) {
        this.stardustCost = stardustCost;
    }

}
