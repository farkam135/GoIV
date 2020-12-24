package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class PlayerLevel {
    @Expose
    private List<Integer> rankNum = null;
    @Expose
    private List<Integer> requiredExperience = null;
    @Expose
    private List<Double> cpMultiplier = null;
    @Expose
    private Integer maxEggPlayerLevel;
    @Expose
    private Integer maxEncounterPlayerLevel;
    @Expose
    private Integer maxQuestEncounterPlayerLevel;

    public List<Integer> getRankNum() { return rankNum; }

    public void setRankNum(List<Integer> rankNum) { this.rankNum = rankNum; }

    public List<Integer> getRequiredExperience() { return requiredExperience; }

    public void setRequiredExperience(List<Integer> requiredExperience) { this.requiredExperience = requiredExperience; }

    public List<Double> getCpMultiplier() { return cpMultiplier; }

    public void setCpMultiplier(List<Double> cpMultiplier) { this.cpMultiplier = cpMultiplier; }

    public Integer getMaxEggPlayerLevel() { return maxEggPlayerLevel; }

    public void setMaxEggPlayerLevel(Integer maxEggPlayerLevel) { this.maxEggPlayerLevel = maxEggPlayerLevel; }

    public Integer getMaxEncounterPlayerLevel() { return maxEncounterPlayerLevel; }

    public void setMaxEncounterPlayerLevel(Integer maxEncounterPlayerLevel) { this.maxEncounterPlayerLevel = maxEncounterPlayerLevel; }

    public Integer getMaxQuestEncounterPlayerLevel() { return maxQuestEncounterPlayerLevel; }

    public void setMaxQuestEncounterPlayerLevel(Integer maxQuestEncounterPlayerLevel) { this.maxQuestEncounterPlayerLevel = maxQuestEncounterPlayerLevel; }
}
