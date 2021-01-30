package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class MegaEvoSettings {
    @Expose
    private String evolutionLengthMs;
    @Expose
    private Double attackBoostFromMegaDifferentType;
    @Expose
    private Double attackBoostFromMegaSameType;
    @Expose
    private Integer maxCandyHoardSize;
    @Expose
    private Boolean enableBuddyWalkingMegaEnergyAward = false;
    @Expose
    private Integer activeMegaBonusCatchCandy;

    public String getEvolutionLengthMs() { return evolutionLengthMs; }

    public void setEvolutionLengthMs(String evolutionLengthMs) { this.evolutionLengthMs = evolutionLengthMs; }

    public Double getAttackBoostFromMegaDifferentType() { return attackBoostFromMegaDifferentType; }

    public void setAttackBoostFromMegaDifferentType(Double attackBoostFromMegaDifferentType) {
        this.attackBoostFromMegaDifferentType = attackBoostFromMegaDifferentType;
    }

    public Double getAttackBoostFromMegaSameType() { return attackBoostFromMegaSameType; }

    public void setAttackBoostFromMegaSameType(Double attackBoostFromMegaSameType) {
        this.attackBoostFromMegaSameType = attackBoostFromMegaSameType;
    }

    public Integer getMaxCandyHoardSize() { return maxCandyHoardSize; }

    public void setMaxCandyHoardSize(Integer maxCandyHoardSize) { this.maxCandyHoardSize = maxCandyHoardSize; }

    public Boolean getEnableBuddyWalkingMegaEnergyAward() { return enableBuddyWalkingMegaEnergyAward; }

    public void setEnableBuddyWalkingMegaEnergyAward(Boolean enableBuddyWalkingMegaEnergyAward) {
        this.enableBuddyWalkingMegaEnergyAward = enableBuddyWalkingMegaEnergyAward;
    }

    public Integer getActiveMegaBonusCatchCandy() { return activeMegaBonusCatchCandy; }

    public void setActiveMegaBonusCatchCandy(Integer activeMegaBonusCatchCandy) {
        this.activeMegaBonusCatchCandy = activeMegaBonusCatchCandy;
    }
}
