package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CombatStatStageSettings {
    @Expose
    private Integer minimumStatStage;
    @Expose
    private Integer maximumStatStage;
    @Expose
    private List<Double> attackBuffMultiplier = null;
    @Expose
    private List<Double> defenseBuffMultiplier = null;

    public Integer getMinimumStatStage() { return minimumStatStage; }

    public void setMinimumStatStage(Integer minimumStatStage) { this.minimumStatStage = minimumStatStage; }

    public Integer getMaximumStatStage() { return maximumStatStage; }

    public void setMaximumStatStage(Integer maximumStatStage) { this.maximumStatStage = maximumStatStage; }

    public List<Double> getAttackBuffMultiplier() { return attackBuffMultiplier; }

    public void setAttackBuffMultiplier(List<Double> attackBuffMultiplier) { this.attackBuffMultiplier = attackBuffMultiplier; }

    public List<Double> getDefenseBuffMultiplier() { return defenseBuffMultiplier; }

    public void setDefenseBuffMultiplier(List<Double> defenseBuffMultiplier) { this.defenseBuffMultiplier = defenseBuffMultiplier; }
}
