package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Encounter {
    @Expose
    private Double baseCaptureRate;
    @Expose
    private Double baseFleeRate;
    @Expose
    private Double collisionRadiusM;
    @Expose
    private Double collisionHeightM;
    @Expose
    private Double collisionHeadRadiusM;
    @Expose
    private String movementType;
    @Expose
    private Double movementTimerS;
    @Expose
    private Double jumpTimeS;
    @Expose
    private Double attackTimerS;
    @Expose
    private Double attackProbability;
    @Expose
    private Double dodgeProbability;
    @Expose
    private Double dodgeDurationS;
    @Expose
    private Double dodgeDistance;
    @Expose
    private Double cameraDistance;
    @Expose
    private Double minPokemonActionFrequencyS;
    @Expose
    private Double maxPokemonActionFrequencyS;
    @Expose
    private Integer bonusCandyCaptureReward;
    @Expose
    private Integer bonusStardustCaptureReward;
    @Expose
    private Integer bonusXlCandyCaptureReward;

    public Double getBaseCaptureRate() {
        return baseCaptureRate;
    }

    public void setBaseCaptureRate(Double baseCaptureRate) {
        this.baseCaptureRate = baseCaptureRate;
    }

    public Double getBaseFleeRate() {
        return baseFleeRate;
    }

    public void setBaseFleeRate(Double baseFleeRate) {
        this.baseFleeRate = baseFleeRate;
    }

    public Double getCollisionRadiusM() {
        return collisionRadiusM;
    }

    public void setCollisionRadiusM(Double collisionRadiusM) {
        this.collisionRadiusM = collisionRadiusM;
    }

    public Double getCollisionHeightM() {
        return collisionHeightM;
    }

    public void setCollisionHeightM(Double collisionHeightM) {
        this.collisionHeightM = collisionHeightM;
    }

    public Double getCollisionHeadRadiusM() {
        return collisionHeadRadiusM;
    }

    public void setCollisionHeadRadiusM(Double collisionHeadRadiusM) {
        this.collisionHeadRadiusM = collisionHeadRadiusM;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Double getMovementTimerS() {
        return movementTimerS;
    }

    public void setMovementTimerS(Double movementTimerS) {
        this.movementTimerS = movementTimerS;
    }

    public Double getJumpTimeS() {
        return jumpTimeS;
    }

    public void setJumpTimeS(Double jumpTimeS) {
        this.jumpTimeS = jumpTimeS;
    }

    public Double getAttackTimerS() {
        return attackTimerS;
    }

    public void setAttackTimerS(Double attackTimerS) {
        this.attackTimerS = attackTimerS;
    }

    public Double getAttackProbability() {
        return attackProbability;
    }

    public void setAttackProbability(Double attackProbability) {
        this.attackProbability = attackProbability;
    }

    public Double getDodgeProbability() {
        return dodgeProbability;
    }

    public void setDodgeProbability(Double dodgeProbability) {
        this.dodgeProbability = dodgeProbability;
    }

    public Double getDodgeDurationS() {
        return dodgeDurationS;
    }

    public void setDodgeDurationS(Double dodgeDurationS) {
        this.dodgeDurationS = dodgeDurationS;
    }

    public Double getDodgeDistance() {
        return dodgeDistance;
    }

    public void setDodgeDistance(Double dodgeDistance) {
        this.dodgeDistance = dodgeDistance;
    }

    public Double getCameraDistance() {
        return cameraDistance;
    }

    public void setCameraDistance(Double cameraDistance) {
        this.cameraDistance = cameraDistance;
    }

    public Double getMinPokemonActionFrequencyS() {
        return minPokemonActionFrequencyS;
    }

    public void setMinPokemonActionFrequencyS(Double minPokemonActionFrequencyS) {
        this.minPokemonActionFrequencyS = minPokemonActionFrequencyS;
    }

    public Double getMaxPokemonActionFrequencyS() {
        return maxPokemonActionFrequencyS;
    }

    public void setMaxPokemonActionFrequencyS(Double maxPokemonActionFrequencyS) {
        this.maxPokemonActionFrequencyS = maxPokemonActionFrequencyS;
    }

    public Integer getBonusCandyCaptureReward() { return bonusCandyCaptureReward; }

    public void setBonusCandyCaptureReward(Integer bonusCandyCaptureReward) {
        this.bonusCandyCaptureReward = bonusCandyCaptureReward;
    }

    public Integer getBonusStardustCaptureReward() { return bonusStardustCaptureReward; }

    public void setBonusStardustCaptureReward(Integer bonusStardustCaptureReward) {
        this.bonusStardustCaptureReward = bonusStardustCaptureReward;
    }

    public Integer getBonusXlCandyCaptureReward() {
        return bonusXlCandyCaptureReward;
    }

    public void setBonusXlCandyCaptureReward(Integer bonusXlCandyCaptureReward) {
        this.bonusXlCandyCaptureReward = bonusXlCandyCaptureReward;
    }
}
