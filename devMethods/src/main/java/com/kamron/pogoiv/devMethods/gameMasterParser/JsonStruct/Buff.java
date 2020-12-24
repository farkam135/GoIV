package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Buff {
    @Expose
    private Integer targetDefenseStatStageChange;
    @Expose
    private Double buffActivationChance;
    @Expose
    private Integer attackerAttackStatStageChange;
    @Expose
    private Integer targetAttackStatStageChange;
    @Expose
    private Integer attackerDefenseStatStageChange;

    public Integer getTargetDefenseStatStageChange() { return targetDefenseStatStageChange; }

    public void setTargetDefenseStatStageChange(Integer targetDefenseStatStageChange) { this.targetDefenseStatStageChange = targetDefenseStatStageChange; }

    public Double getBuffActivationChance() { return buffActivationChance; }

    public void setBuffActivationChance(Double buffActivationChance) { this.buffActivationChance = buffActivationChance; }

    public Integer getAttackerAttackStatStageChange() { return attackerAttackStatStageChange; }

    public void setAttackerAttackStatStageChange(Integer attackerAttackStatStageChange) { this.attackerAttackStatStageChange = attackerAttackStatStageChange; }

    public Integer getTargetAttackStatStageChange() { return targetAttackStatStageChange; }

    public void setTargetAttackStatStageChange(Integer targetAttackStatStageChange) { this.targetAttackStatStageChange = targetAttackStatStageChange; }

    public Integer getAttackerDefenseStatStageChange() { return attackerDefenseStatStageChange; }

    public void setAttackerDefenseStatStageChange(Integer attackerDefenseStatStageChange) { this.attackerDefenseStatStageChange = attackerDefenseStatStageChange; }
}
