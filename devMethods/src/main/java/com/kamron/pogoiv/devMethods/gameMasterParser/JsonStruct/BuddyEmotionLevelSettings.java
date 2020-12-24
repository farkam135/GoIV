package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyEmotionLevelSettings {
    @Expose
    private String emotionLevel;
    @Expose
    private String emotionAnimation;
    @Expose
    private Integer minEmotionPointsRequired;

    public String getEmotionLevel() { return emotionLevel; }

    public void setEmotionLevel(String emotionLevel) { this.emotionLevel = emotionLevel; }

    public String getEmotionAnimation() { return emotionAnimation; }

    public void setEmotionAnimation(String emotionAnimation) { this.emotionAnimation = emotionAnimation; }

    public Integer getMinEmotionPointsRequired() { return minEmotionPointsRequired; }

    public void setMinEmotionPointsRequired(Integer minEmotionPointsRequired) { this.minEmotionPointsRequired = minEmotionPointsRequired; }
}
