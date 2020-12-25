package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class WeatherBonusSettings {
    @Expose
    private Integer cpBaseLevelBonus;
    @Expose
    private Integer guaranteedIndividualValues;
    @Expose
    private Double stardustBonusMultiplier;
    @Expose
    private Double attackBonusMultiplier;
    @Expose
    private Integer raidEncounterCpBaseLevelBonus;
    @Expose
    private Integer raidEncounterGuaranteedIndividualValues;

    public Integer getCpBaseLevelBonus() { return cpBaseLevelBonus; }

    public void setCpBaseLevelBonus(Integer cpBaseLevelBonus) { this.cpBaseLevelBonus = cpBaseLevelBonus; }

    public Integer getGuaranteedIndividualValues() { return guaranteedIndividualValues; }

    public void setGuaranteedIndividualValues(Integer guaranteedIndividualValues) {
        this.guaranteedIndividualValues = guaranteedIndividualValues;
    }

    public Double getStardustBonusMultiplier() { return stardustBonusMultiplier; }

    public void setStardustBonusMultiplier(Double stardustBonusMultiplier) {
        this.stardustBonusMultiplier = stardustBonusMultiplier;
    }

    public Double getAttackBonusMultiplier() { return attackBonusMultiplier; }

    public void setAttackBonusMultiplier(Double attackBonusMultiplier) {
        this.attackBonusMultiplier = attackBonusMultiplier;
    }

    public Integer getRaidEncounterCpBaseLevelBonus() { return raidEncounterCpBaseLevelBonus; }

    public void setRaidEncounterCpBaseLevelBonus(Integer raidEncounterCpBaseLevelBonus) {
        this.raidEncounterCpBaseLevelBonus = raidEncounterCpBaseLevelBonus;
    }

    public Integer getRaidEncounterGuaranteedIndividualValues() { return raidEncounterGuaranteedIndividualValues; }

    public void setRaidEncounterGuaranteedIndividualValues(Integer raidEncounterGuaranteedIndividualValues) {
        this.raidEncounterGuaranteedIndividualValues = raidEncounterGuaranteedIndividualValues;
    }
}
