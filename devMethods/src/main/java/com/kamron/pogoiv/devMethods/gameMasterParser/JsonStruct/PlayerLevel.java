
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayerLevel {

    @SerializedName("rankNum")
    @Expose
    private List<Integer> rankNum = null;
    @SerializedName("requiredExperience")
    @Expose
    private List<Integer> requiredExperience = null;
    @SerializedName("cpMultiplier")
    @Expose
    private List<Double> cpMultiplier = null;
    @SerializedName("maxEggPlayerLevel")
    @Expose
    private Integer maxEggPlayerLevel;
    @SerializedName("maxEncounterPlayerLevel")
    @Expose
    private Integer maxEncounterPlayerLevel;
    @SerializedName("maxQuestEncounterPlayerLevel")
    @Expose
    private Integer maxQuestEncounterPlayerLevel;

    public List<Integer> getRankNum() {
        return rankNum;
    }

    public void setRankNum(List<Integer> rankNum) {
        this.rankNum = rankNum;
    }

    public List<Integer> getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(List<Integer> requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public List<Double> getCpMultiplier() {
        return cpMultiplier;
    }

    public void setCpMultiplier(List<Double> cpMultiplier) {
        this.cpMultiplier = cpMultiplier;
    }

    public Integer getMaxEggPlayerLevel() {
        return maxEggPlayerLevel;
    }

    public void setMaxEggPlayerLevel(Integer maxEggPlayerLevel) {
        this.maxEggPlayerLevel = maxEggPlayerLevel;
    }

    public Integer getMaxEncounterPlayerLevel() {
        return maxEncounterPlayerLevel;
    }

    public void setMaxEncounterPlayerLevel(Integer maxEncounterPlayerLevel) {
        this.maxEncounterPlayerLevel = maxEncounterPlayerLevel;
    }

    public Integer getMaxQuestEncounterPlayerLevel() {
        return maxQuestEncounterPlayerLevel;
    }

    public void setMaxQuestEncounterPlayerLevel(Integer maxQuestEncounterPlayerLevel) {
        this.maxQuestEncounterPlayerLevel = maxQuestEncounterPlayerLevel;
    }

}
