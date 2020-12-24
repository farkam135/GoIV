package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class EncounterSettings {
    @Expose
    private Double spinBonusThreshold;
    @Expose
    private Double excellentThrowThreshold;
    @Expose
    private Double greatThrowThreshold;
    @Expose
    private Double niceThrowThreshold;
    @Expose
    private Integer milestoneThreshold;
    @Expose
    private Boolean arPlusModeEnabled = false;
    @Expose
    private Double arCloseProximityThreshold;
    @Expose
    private Double arLowAwarenessThreshold;

    public Double getSpinBonusThreshold() { return spinBonusThreshold; }

    public void setSpinBonusThreshold(Double spinBonusThreshold) { this.spinBonusThreshold = spinBonusThreshold; }

    public Double getExcellentThrowThreshold() { return excellentThrowThreshold; }

    public void setExcellentThrowThreshold(Double excellentThrowThreshold) { this.excellentThrowThreshold = excellentThrowThreshold; }

    public Double getGreatThrowThreshold() { return greatThrowThreshold; }

    public void setGreatThrowThreshold(Double greatThrowThreshold) { this.greatThrowThreshold = greatThrowThreshold; }

    public Double getNiceThrowThreshold() { return niceThrowThreshold; }

    public void setNiceThrowThreshold(Double niceThrowThreshold) { this.niceThrowThreshold = niceThrowThreshold; }

    public Integer getMilestoneThreshold() { return milestoneThreshold; }

    public void setMilestoneThreshold(Integer milestoneThreshold) { this.milestoneThreshold = milestoneThreshold; }

    public Boolean getArPlusModeEnabled() { return arPlusModeEnabled = false; }

    public void setArPlusModeEnabled(Boolean arPlusModeEnabled) { this.arPlusModeEnabled = arPlusModeEnabled = false; }

    public Double getArCloseProximityThreshold() { return arCloseProximityThreshold; }

    public void setArCloseProximityThreshold(Double arCloseProximityThreshold) { this.arCloseProximityThreshold = arCloseProximityThreshold; }

    public Double getArLowAwarenessThreshold() { return arLowAwarenessThreshold; }

    public void setArLowAwarenessThreshold(Double arLowAwarenessThreshold) { this.arLowAwarenessThreshold = arLowAwarenessThreshold; }
}
