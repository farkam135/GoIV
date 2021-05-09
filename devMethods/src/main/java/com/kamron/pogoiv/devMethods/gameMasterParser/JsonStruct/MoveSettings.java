package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class MoveSettings {
    @Expose
    private String movementId;
    @Expose
    private Integer animationId;
    @Expose
    private String pokemonType;
    @Expose
    private Double power;
    @Expose
    private Double accuracyChance;
    @Expose
    private Double criticalChance;
    @Expose
    private Double staminaLossScalar;
    @Expose
    private String trainerLevelMin;
    @Expose
    private String trainerLevelMax;
    @Expose
    private String vfxName;
    @Expose
    private Integer durationMs;
    @Expose
    private Integer damageWindowStartMs;
    @Expose
    private Integer damageWindowEndMs;
    @Expose
    private Integer energyDelta;
    @Expose
    private Double healScalar;
    @Expose
    private Boolean isLocked = false;

    public String getMovementId() { return movementId; }

    public void setMovementId(String movementId) { this.movementId = movementId; }

    public Integer getAnimationId() { return animationId; }

    public void setAnimationId(Integer animationId) { this.animationId = animationId; }

    public String getPokemonType() { return pokemonType; }

    public void setPokemonType(String pokemonType) { this.pokemonType = pokemonType; }

    public Double getPower() { return power; }

    public void setPower(Double power) { this.power = power; }

    public Double getAccuracyChance() { return accuracyChance; }

    public void setAccuracyChance(Double accuracyChance) { this.accuracyChance = accuracyChance; }

    public Double getCriticalChance() { return criticalChance; }

    public void setCriticalChance(Double criticalChance) { this.criticalChance = criticalChance; }

    public Double getStaminaLossScalar() { return staminaLossScalar; }

    public void setStaminaLossScalar(Double staminaLossScalar) { this.staminaLossScalar = staminaLossScalar; }

    public String getTrainerLevelMin() { return trainerLevelMin; }

    public void setTrainerLevelMin(String trainerLevelMin) { this.trainerLevelMin = trainerLevelMin; }

    public String getTrainerLevelMax() { return trainerLevelMax; }

    public void setTrainerLevelMax(String trainerLevelMax) { this.trainerLevelMax = trainerLevelMax; }

    public String getVfxName() { return vfxName; }

    public void setVfxName(String vfxName) { this.vfxName = vfxName; }

    public Integer getDurationMs() { return durationMs; }

    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public Integer getDamageWindowStartMs() { return damageWindowStartMs; }

    public void setDamageWindowStartMs(Integer damageWindowStartMs) { this.damageWindowStartMs = damageWindowStartMs; }

    public Integer getDamageWindowEndMs() { return damageWindowEndMs; }

    public void setDamageWindowEndMs(Integer damageWindowEndMs) { this.damageWindowEndMs = damageWindowEndMs; }

    public Integer getEnergyDelta() { return energyDelta; }

    public void setEnergyDelta(Integer energyDelta) { this.energyDelta = energyDelta; }

    public Double getHealScalar() { return healScalar; }

    public void setHealScalar(Double healScalar) { this.healScalar = healScalar; }

    public Boolean getLocked() { return isLocked; }

    public void setLocked(Boolean locked) { isLocked = locked; }
}
