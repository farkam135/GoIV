package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BattleSettings {
    @Expose
    private Double retargetSeconds;
    @Expose
    private Double enemyAttackInterval;
    @Expose
    private Double attackServerInterval;
    @Expose
    private Double roundDurationSeconds;
    @Expose
    private Double bonusTimePerAllySeconds;
    @Expose
    private Integer maximumAttackersPerBattle;
    @Expose
    private Double sameTypeAttackBonusMultiplier;
    @Expose
    private Integer maximumEnergy;
    @Expose
    private Double energyDeltaPerHealthLost;
    @Expose
    private Integer dodgeDurationMs;
    @Expose
    private Integer minimumPlayerLevel;
    @Expose
    private Integer swapDurationMs;
    @Expose
    private Double dodgeDamageReductionPercent;
    @Expose
    private Integer minimumRaidPlayerLevel;
    @Expose
    private Double shadowPokemonAttackBonusMultiplier;
    @Expose
    private Double shadowPokemonDefenseBonusMultiplier;
    @Expose
    private Double purifiedPokemonAttackMultiplierVsShadow;

    public Double getRetargetSeconds() { return retargetSeconds; }

    public void setRetargetSeconds(Double retargetSeconds) { this.retargetSeconds = retargetSeconds; }

    public Double getEnemyAttackInterval() { return enemyAttackInterval; }

    public void setEnemyAttackInterval(Double enemyAttackInterval) { this.enemyAttackInterval = enemyAttackInterval; }

    public Double getAttackServerInterval() { return attackServerInterval; }

    public void setAttackServerInterval(Double attackServerInterval) {
        this.attackServerInterval = attackServerInterval;
    }

    public Double getRoundDurationSeconds() { return roundDurationSeconds; }

    public void setRoundDurationSeconds(Double roundDurationSeconds) {
        this.roundDurationSeconds = roundDurationSeconds;
    }

    public Double getBonusTimePerAllySeconds() { return bonusTimePerAllySeconds; }

    public void setBonusTimePerAllySeconds(Double bonusTimePerAllySeconds) {
        this.bonusTimePerAllySeconds = bonusTimePerAllySeconds;
    }

    public Integer getMaximumAttackersPerBattle() { return maximumAttackersPerBattle; }

    public void setMaximumAttackersPerBattle(Integer maximumAttackersPerBattle) {
        this.maximumAttackersPerBattle = maximumAttackersPerBattle;
    }

    public Double getSameTypeAttackBonusMultiplier() { return sameTypeAttackBonusMultiplier; }

    public void setSameTypeAttackBonusMultiplier(Double sameTypeAttackBonusMultiplier) {
        this.sameTypeAttackBonusMultiplier = sameTypeAttackBonusMultiplier;
    }

    public Integer getMaximumEnergy() { return maximumEnergy; }

    public void setMaximumEnergy(Integer maximumEnergy) { this.maximumEnergy = maximumEnergy; }

    public Double getEnergyDeltaPerHealthLost() { return energyDeltaPerHealthLost; }

    public void setEnergyDeltaPerHealthLost(Double energyDeltaPerHealthLost) {
        this.energyDeltaPerHealthLost = energyDeltaPerHealthLost;
    }

    public Integer getDodgeDurationMs() { return dodgeDurationMs; }

    public void setDodgeDurationMs(Integer dodgeDurationMs) { this.dodgeDurationMs = dodgeDurationMs; }

    public Integer getMinimumPlayerLevel() { return minimumPlayerLevel; }

    public void setMinimumPlayerLevel(Integer minimumPlayerLevel) { this.minimumPlayerLevel = minimumPlayerLevel; }

    public Integer getSwapDurationMs() { return swapDurationMs; }

    public void setSwapDurationMs(Integer swapDurationMs) { this.swapDurationMs = swapDurationMs; }

    public Double getDodgeDamageReductionPercent() { return dodgeDamageReductionPercent; }

    public void setDodgeDamageReductionPercent(Double dodgeDamageReductionPercent) {
        this.dodgeDamageReductionPercent = dodgeDamageReductionPercent;
    }

    public Integer getMinimumRaidPlayerLevel() { return minimumRaidPlayerLevel; }

    public void setMinimumRaidPlayerLevel(Integer minimumRaidPlayerLevel) {
        this.minimumRaidPlayerLevel = minimumRaidPlayerLevel;
    }

    public Double getShadowPokemonAttackBonusMultiplier() { return shadowPokemonAttackBonusMultiplier; }

    public void setShadowPokemonAttackBonusMultiplier(Double shadowPokemonAttackBonusMultiplier) {
        this.shadowPokemonAttackBonusMultiplier = shadowPokemonAttackBonusMultiplier;
    }

    public Double getShadowPokemonDefenseBonusMultiplier() { return shadowPokemonDefenseBonusMultiplier; }

    public void setShadowPokemonDefenseBonusMultiplier(Double shadowPokemonDefenseBonusMultiplier) {
        this.shadowPokemonDefenseBonusMultiplier = shadowPokemonDefenseBonusMultiplier;
    }

    public Double getPurifiedPokemonAttackMultiplierVsShadow() { return purifiedPokemonAttackMultiplierVsShadow; }

    public void setPurifiedPokemonAttackMultiplierVsShadow(Double purifiedPokemonAttackMultiplierVsShadow) {
        this.purifiedPokemonAttackMultiplierVsShadow = purifiedPokemonAttackMultiplierVsShadow;
    }
}
