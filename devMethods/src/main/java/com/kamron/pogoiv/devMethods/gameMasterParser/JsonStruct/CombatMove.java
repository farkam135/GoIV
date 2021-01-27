package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CombatMove {
    @Expose
    private String uniqueId;
    @Expose
    private String type;
    @Expose
    private Double power;
    @Expose
    private String vfxName;
    @Expose
    private Integer energyDelta;
    @Expose
    private Buff buffs;
    @Expose
    private Integer durationTurns;

    public String getUniqueId() { return uniqueId; }

    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Double getPower() { return power; }

    public void setPower(Double power) { this.power = power; }

    public String getVfxName() { return vfxName; }

    public void setVfxName(String vfxName) { this.vfxName = vfxName; }

    public Integer getEnergyDelta() { return energyDelta; }

    public Buff getBuffs() { return buffs; }

    public void setBuffs(Buff buffs) { this.buffs = buffs; }

    public void setEnergyDelta(Integer energyDelta) { this.energyDelta = energyDelta; }

    public Integer getDurationTurns() { return durationTurns; }

    public void setDurationTurns(Integer durationTurns) { this.durationTurns = durationTurns; }
}
