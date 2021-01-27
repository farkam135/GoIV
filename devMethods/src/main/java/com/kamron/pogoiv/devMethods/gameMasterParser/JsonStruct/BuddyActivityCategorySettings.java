package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyActivityCategorySettings {
    @Expose
    private String activityCategory;
    @Expose
    private Integer maxPointsPerDay;

    public String getActivityCategory() { return activityCategory; }

    public void setActivityCategory(String activityCategory) { this.activityCategory = activityCategory; }

    public Integer getMaxPointsPerDay() { return maxPointsPerDay; }

    public void setMaxPointsPerDay(Integer maxPointsPerDay) { this.maxPointsPerDay = maxPointsPerDay; }
}
