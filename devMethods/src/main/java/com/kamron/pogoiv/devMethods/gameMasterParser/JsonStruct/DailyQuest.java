package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class DailyQuest {
    @Expose
    private Integer bucketsPerDay;
    @Expose
    private Integer streakLength;
    @Expose
    private Double bonusMultiplier;
    @Expose
    private Double streakBonusMultiplier;

    public Integer getBucketsPerDay() { return bucketsPerDay; }

    public void setBucketsPerDay(Integer bucketsPerDay) { this.bucketsPerDay = bucketsPerDay; }

    public Integer getStreakLength() { return streakLength; }

    public void setStreakLength(Integer streakLength) { this.streakLength = streakLength; }

    public Double getBonusMultiplier() { return bonusMultiplier; }

    public void setBonusMultiplier(Double bonusMultiplier) { this.bonusMultiplier = bonusMultiplier; }

    public Double getStreakBonusMultiplier() { return streakBonusMultiplier; }

    public void setStreakBonusMultiplier(Double streakBonusMultiplier) { this.streakBonusMultiplier = streakBonusMultiplier; }
}
