
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherBonusSettings {

    @SerializedName("cpBaseLevelBonus")
    @Expose
    private Integer cpBaseLevelBonus;
    @SerializedName("guaranteedIndividualValues")
    @Expose
    private Integer guaranteedIndividualValues;
    @SerializedName("stardustBonusMultiplier")
    @Expose
    private Double stardustBonusMultiplier;
    @SerializedName("attackBonusMultiplier")
    @Expose
    private Double attackBonusMultiplier;
    @SerializedName("raidEncounterCpBaseLevelBonus")
    @Expose
    private Integer raidEncounterCpBaseLevelBonus;
    @SerializedName("raidEncounterGuaranteedIndividualValues")
    @Expose
    private Integer raidEncounterGuaranteedIndividualValues;

    public Integer getCpBaseLevelBonus() {
        return cpBaseLevelBonus;
    }

    public void setCpBaseLevelBonus(Integer cpBaseLevelBonus) {
        this.cpBaseLevelBonus = cpBaseLevelBonus;
    }

    public Integer getGuaranteedIndividualValues() {
        return guaranteedIndividualValues;
    }

    public void setGuaranteedIndividualValues(Integer guaranteedIndividualValues) {
        this.guaranteedIndividualValues = guaranteedIndividualValues;
    }

    public Double getStardustBonusMultiplier() {
        return stardustBonusMultiplier;
    }

    public void setStardustBonusMultiplier(Double stardustBonusMultiplier) {
        this.stardustBonusMultiplier = stardustBonusMultiplier;
    }

    public Double getAttackBonusMultiplier() {
        return attackBonusMultiplier;
    }

    public void setAttackBonusMultiplier(Double attackBonusMultiplier) {
        this.attackBonusMultiplier = attackBonusMultiplier;
    }

    public Integer getRaidEncounterCpBaseLevelBonus() {
        return raidEncounterCpBaseLevelBonus;
    }

    public void setRaidEncounterCpBaseLevelBonus(Integer raidEncounterCpBaseLevelBonus) {
        this.raidEncounterCpBaseLevelBonus = raidEncounterCpBaseLevelBonus;
    }

    public Integer getRaidEncounterGuaranteedIndividualValues() {
        return raidEncounterGuaranteedIndividualValues;
    }

    public void setRaidEncounterGuaranteedIndividualValues(Integer raidEncounterGuaranteedIndividualValues) {
        this.raidEncounterGuaranteedIndividualValues = raidEncounterGuaranteedIndividualValues;
    }

}
