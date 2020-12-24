package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyHungerSettings {
    @Expose
    private Integer numHungerPointsRequiredForFull;
    @Expose
    private Integer decayPointsPerBucket;
    @Expose
    private String millisecondsPerBucket;
    @Expose
    private String cooldownDurationMs;
    @Expose
    private String decayDurationAfterFullMs;

    public Integer getNumHungerPointsRequiredForFull() { return numHungerPointsRequiredForFull; }

    public void setNumHungerPointsRequiredForFull(Integer numHungerPointsRequiredForFull) { this.numHungerPointsRequiredForFull = numHungerPointsRequiredForFull; }

    public Integer getDecayPointsPerBucket() { return decayPointsPerBucket; }

    public void setDecayPointsPerBucket(Integer decayPointsPerBucket) { this.decayPointsPerBucket = decayPointsPerBucket; }

    public String getMillisecondsPerBucket() { return millisecondsPerBucket; }

    public void setMillisecondsPerBucket(String millisecondsPerBucket) { this.millisecondsPerBucket = millisecondsPerBucket; }

    public String getCooldownDurationMs() { return cooldownDurationMs; }

    public void setCooldownDurationMs(String cooldownDurationMs) { this.cooldownDurationMs = cooldownDurationMs; }

    public String getDecayDurationAfterFullMs() { return decayDurationAfterFullMs; }

    public void setDecayDurationAfterFullMs(String decayDurationAfterFullMs) { this.decayDurationAfterFullMs = decayDurationAfterFullMs; }
}
