package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyActivitySettings {
    @Expose
    private String activity;
    @Expose
    private String activityCategory;
    @Expose
    private Integer maxTimesPerDay;
    @Expose
    private Integer numPointsPerAction;
    @Expose
    private Integer numEmotionPointsPerAction;
    @Expose
    private String emotionCooldownDurationMs;

    public String getActivity() { return activity; }

    public void setActivity(String activity) { this.activity = activity; }

    public String getActivityCategory() { return activityCategory; }

    public void setActivityCategory(String activityCategory) { this.activityCategory = activityCategory; }

    public Integer getMaxTimesPerDay() { return maxTimesPerDay; }

    public void setMaxTimesPerDay(Integer maxTimesPerDay) { this.maxTimesPerDay = maxTimesPerDay; }

    public Integer getNumPointsPerAction() { return numPointsPerAction; }

    public void setNumPointsPerAction(Integer numPointsPerAction) { this.numPointsPerAction = numPointsPerAction; }

    public Integer getNumEmotionPointsPerAction() { return numEmotionPointsPerAction; }

    public void setNumEmotionPointsPerAction(Integer numEmotionPointsPerAction) { this.numEmotionPointsPerAction = numEmotionPointsPerAction; }

    public String getEmotionCooldownDurationMs() { return emotionCooldownDurationMs; }

    public void setEmotionCooldownDurationMs(String emotionCooldownDurationMs) { this.emotionCooldownDurationMs = emotionCooldownDurationMs; }
}
