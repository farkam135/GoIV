
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyQuest {

    @SerializedName("bucketsPerDay")
    @Expose
    private Integer bucketsPerDay;
    @SerializedName("streakLength")
    @Expose
    private Integer streakLength;
    @SerializedName("bonusMultiplier")
    @Expose
    private Double bonusMultiplier;
    @SerializedName("streakBonusMultiplier")
    @Expose
    private Double streakBonusMultiplier;

    public Integer getBucketsPerDay() {
        return bucketsPerDay;
    }

    public void setBucketsPerDay(Integer bucketsPerDay) {
        this.bucketsPerDay = bucketsPerDay;
    }

    public Integer getStreakLength() {
        return streakLength;
    }

    public void setStreakLength(Integer streakLength) {
        this.streakLength = streakLength;
    }

    public Double getBonusMultiplier() {
        return bonusMultiplier;
    }

    public void setBonusMultiplier(Double bonusMultiplier) {
        this.bonusMultiplier = bonusMultiplier;
    }

    public Double getStreakBonusMultiplier() {
        return streakBonusMultiplier;
    }

    public void setStreakBonusMultiplier(Double streakBonusMultiplier) {
        this.streakBonusMultiplier = streakBonusMultiplier;
    }

}
