package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BackgroundModeSettings {
    @Expose
    private Double weeklyFitnessGoalLevel1DistanceKm;
    @Expose
    private Double weeklyFitnessGoalLevel2DistanceKm;
    @Expose
    private Double weeklyFitnessGoalLevel3DistanceKm;
    @Expose
    private Double weeklyFitnessGoalLevel4DistanceKm;

    public Double getWeeklyFitnessGoalLevel1DistanceKm() { return weeklyFitnessGoalLevel1DistanceKm; }

    public void setWeeklyFitnessGoalLevel1DistanceKm(Double weeklyFitnessGoalLevel1DistanceKm) {
        this.weeklyFitnessGoalLevel1DistanceKm = weeklyFitnessGoalLevel1DistanceKm;
    }

    public Double getWeeklyFitnessGoalLevel2DistanceKm() { return weeklyFitnessGoalLevel2DistanceKm; }

    public void setWeeklyFitnessGoalLevel2DistanceKm(Double weeklyFitnessGoalLevel2DistanceKm) {
        this.weeklyFitnessGoalLevel2DistanceKm = weeklyFitnessGoalLevel2DistanceKm;
    }

    public Double getWeeklyFitnessGoalLevel3DistanceKm() { return weeklyFitnessGoalLevel3DistanceKm; }

    public void setWeeklyFitnessGoalLevel3DistanceKm(Double weeklyFitnessGoalLevel3DistanceKm) {
        this.weeklyFitnessGoalLevel3DistanceKm = weeklyFitnessGoalLevel3DistanceKm;
    }

    public Double getWeeklyFitnessGoalLevel4DistanceKm() { return weeklyFitnessGoalLevel4DistanceKm; }

    public void setWeeklyFitnessGoalLevel4DistanceKm(Double weeklyFitnessGoalLevel4DistanceKm) {
        this.weeklyFitnessGoalLevel4DistanceKm = weeklyFitnessGoalLevel4DistanceKm;
    }
}
