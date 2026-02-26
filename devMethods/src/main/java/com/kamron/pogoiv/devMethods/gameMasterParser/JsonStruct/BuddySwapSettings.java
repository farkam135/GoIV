package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddySwapSettings {
    @Expose
    private Integer maxSwapsPerDay;

    public Integer getMaxSwapsPerDay() { return maxSwapsPerDay; }

    public void setMaxSwapsPerDay(Integer maxSwapsPerDay) { this.maxSwapsPerDay = maxSwapsPerDay; }
}
