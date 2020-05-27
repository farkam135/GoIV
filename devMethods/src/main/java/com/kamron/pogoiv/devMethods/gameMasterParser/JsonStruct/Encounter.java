
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Encounter {

    @SerializedName("baseCaptureRate")
    @Expose
    private Double baseCaptureRate;
    @SerializedName("baseFleeRate")
    @Expose
    private Double baseFleeRate;
    @SerializedName("collisionRadiusM")
    @Expose
    private Double collisionRadiusM;
    @SerializedName("collisionHeightM")
    @Expose
    private Double collisionHeightM;
    @SerializedName("collisionHeadRadiusM")
    @Expose
    private Double collisionHeadRadiusM;
    @SerializedName("movementType")
    @Expose
    private String movementType;
    @SerializedName("movementTimerS")
    @Expose
    private Double movementTimerS;
    @SerializedName("jumpTimeS")
    @Expose
    private Double jumpTimeS;
    @SerializedName("attackTimerS")
    @Expose
    private Double attackTimerS;
    @SerializedName("attackProbability")
    @Expose
    private Double attackProbability;
    @SerializedName("dodgeProbability")
    @Expose
    private Double dodgeProbability;
    @SerializedName("dodgeDurationS")
    @Expose
    private Double dodgeDurationS;
    @SerializedName("dodgeDistance")
    @Expose
    private Double dodgeDistance;
    @SerializedName("cameraDistance")
    @Expose
    private Double cameraDistance;
    @SerializedName("minPokemonActionFrequencyS")
    @Expose
    private Double minPokemonActionFrequencyS;
    @SerializedName("maxPokemonActionFrequencyS")
    @Expose
    private Double maxPokemonActionFrequencyS;

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

}
