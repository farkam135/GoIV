package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CombatSettings {
    @Expose
    private Double roundDurationSeconds;
    @Expose
    private Double turnDurationSeconds;
    @Expose
    private Double minigameDurationSeconds;
    @Expose
    private Double sameTypeAttackBonusMultiplier;
    @Expose
    private Double fastAttackBonusMultiplier;
    @Expose
    private Double chargeAttackBonusMultiplier;
    @Expose
    private Double defenseBonusMultiplier;
    @Expose
    private Double minigameBonusBaseMultiplier;
    @Expose
    private Double minigameBonusVariableMultiplier;
    @Expose
    private Integer maxEnergy;
    @Expose
    private Double defenderMinigameMultiplier;
    @Expose
    private Double changePokemonDurationSeconds;
    @Expose
    private Double minigameSubmitScoreDurationSeconds;
    @Expose
    private Double quickSwapCooldownDurationSeconds;
    @Expose
    private Double chargeScoreBase;
    @Expose
    private Double chargeScoreNice;
    @Expose
    private Double chargeScoreGreat;
    @Expose
    private Double chargeScoreExcellent;
    @Expose
    private Integer superEffectiveFlyoutDurationTurns;
    @Expose
    private Integer notVeryEffectiveFlyoutDurationTurns;
    @Expose
    private Integer blockedFlyoutDurationTurns;
    @Expose
    private Integer normalEffectiveFlyoutDurationTurns;
    @Expose
    private Double shadowPokemonAttackBonusMultiplier;
    @Expose
    private Double shadowPokemonDefenseBonusMultiplier;
    @Expose
    private Double purifiedPokemonAttackMultiplierVsShadow;

    public Double getRoundDurationSeconds() { return roundDurationSeconds; }

    public void setRoundDurationSeconds(Double roundDurationSeconds) {
        this.roundDurationSeconds = roundDurationSeconds;
    }

    public Double getTurnDurationSeconds() { return turnDurationSeconds; }

    public void setTurnDurationSeconds(Double turnDurationSeconds) {
        this.turnDurationSeconds = turnDurationSeconds;
    }

    public Double getMinigameDurationSeconds() { return minigameDurationSeconds; }

    public void setMinigameDurationSeconds(Double minigameDurationSeconds) {
        this.minigameDurationSeconds = minigameDurationSeconds;
    }

    public Double getSameTypeAttackBonusMultiplier() { return sameTypeAttackBonusMultiplier; }

    public void setSameTypeAttackBonusMultiplier(Double sameTypeAttackBonusMultiplier) {
        this.sameTypeAttackBonusMultiplier = sameTypeAttackBonusMultiplier;
    }

    public Double getFastAttackBonusMultiplier() { return fastAttackBonusMultiplier; }

    public void setFastAttackBonusMultiplier(Double fastAttackBonusMultiplier) {
        this.fastAttackBonusMultiplier = fastAttackBonusMultiplier;
    }

    public Double getChargeAttackBonusMultiplier() { return chargeAttackBonusMultiplier; }

    public void setChargeAttackBonusMultiplier(Double chargeAttackBonusMultiplier) {
        this.chargeAttackBonusMultiplier = chargeAttackBonusMultiplier;
    }

    public Double getDefenseBonusMultiplier() { return defenseBonusMultiplier; }

    public void setDefenseBonusMultiplier(Double defenseBonusMultiplier) {
        this.defenseBonusMultiplier = defenseBonusMultiplier;
    }

    public Double getMinigameBonusBaseMultiplier() { return minigameBonusBaseMultiplier; }

    public void setMinigameBonusBaseMultiplier(Double minigameBonusBaseMultiplier) {
        this.minigameBonusBaseMultiplier = minigameBonusBaseMultiplier;
    }

    public Double getMinigameBonusVariableMultiplier() { return minigameBonusVariableMultiplier; }

    public void setMinigameBonusVariableMultiplier(Double minigameBonusVariableMultiplier) {
        this.minigameBonusVariableMultiplier = minigameBonusVariableMultiplier;
    }

    public Integer getMaxEnergy() { return maxEnergy; }

    public void setMaxEnergy(Integer maxEnergy) { this.maxEnergy = maxEnergy; }

    public Double getDefenderMinigameMultiplier() { return defenderMinigameMultiplier; }

    public void setDefenderMinigameMultiplier(Double defenderMinigameMultiplier) {
        this.defenderMinigameMultiplier = defenderMinigameMultiplier;
    }

    public Double getChangePokemonDurationSeconds() { return changePokemonDurationSeconds; }

    public void setChangePokemonDurationSeconds(Double changePokemonDurationSeconds) {
        this.changePokemonDurationSeconds = changePokemonDurationSeconds;
    }

    public Double getMinigameSubmitScoreDurationSeconds() { return minigameSubmitScoreDurationSeconds; }

    public void setMinigameSubmitScoreDurationSeconds(Double minigameSubmitScoreDurationSeconds) {
        this.minigameSubmitScoreDurationSeconds = minigameSubmitScoreDurationSeconds;
    }

    public Double getQuickSwapCooldownDurationSeconds() { return quickSwapCooldownDurationSeconds; }

    public void setQuickSwapCooldownDurationSeconds(Double quickSwapCooldownDurationSeconds) {
        this.quickSwapCooldownDurationSeconds = quickSwapCooldownDurationSeconds;
    }

    public Double getChargeScoreBase() { return chargeScoreBase; }

    public void setChargeScoreBase(Double chargeScoreBase) { this.chargeScoreBase = chargeScoreBase; }

    public Double getChargeScoreNice() { return chargeScoreNice; }

    public void setChargeScoreNice(Double chargeScoreNice) { this.chargeScoreNice = chargeScoreNice; }

    public Double getChargeScoreGreat() { return chargeScoreGreat; }

    public void setChargeScoreGreat(Double chargeScoreGreat) { this.chargeScoreGreat = chargeScoreGreat; }

    public Double getChargeScoreExcellent() { return chargeScoreExcellent; }

    public void setChargeScoreExcellent(Double chargeScoreExcellent) {
        this.chargeScoreExcellent = chargeScoreExcellent;
    }

    public Integer getSuperEffectiveFlyoutDurationTurns() { return superEffectiveFlyoutDurationTurns; }

    public void setSuperEffectiveFlyoutDurationTurns(Integer superEffectiveFlyoutDurationTurns) {
        this.superEffectiveFlyoutDurationTurns = superEffectiveFlyoutDurationTurns;
    }

    public Integer getNotVeryEffectiveFlyoutDurationTurns() { return notVeryEffectiveFlyoutDurationTurns; }

    public void setNotVeryEffectiveFlyoutDurationTurns(Integer notVeryEffectiveFlyoutDurationTurns) {
        this.notVeryEffectiveFlyoutDurationTurns = notVeryEffectiveFlyoutDurationTurns;
    }

    public Integer getBlockedFlyoutDurationTurns() { return blockedFlyoutDurationTurns; }

    public void setBlockedFlyoutDurationTurns(Integer blockedFlyoutDurationTurns) {
        this.blockedFlyoutDurationTurns = blockedFlyoutDurationTurns;
    }

    public Integer getNormalEffectiveFlyoutDurationTurns() { return normalEffectiveFlyoutDurationTurns; }

    public void setNormalEffectiveFlyoutDurationTurns(Integer normalEffectiveFlyoutDurationTurns) {
        this.normalEffectiveFlyoutDurationTurns = normalEffectiveFlyoutDurationTurns;
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
