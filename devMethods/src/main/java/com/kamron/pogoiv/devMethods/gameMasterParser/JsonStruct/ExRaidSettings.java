package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class ExRaidSettings {
    @Expose
    private String minimumExRaidShareLevel;
    @Expose
    private Integer undefinedExRaidSetting;

    public String getMinimumExRaidShareLevel() { return minimumExRaidShareLevel; }

    public void setMinimumExRaidShareLevel(String minimumExRaidShareLevel) {
        this.minimumExRaidShareLevel = minimumExRaidShareLevel;
    }

    public Integer getUndefinedExRaidSetting() {
        return undefinedExRaidSetting;
    }

    public void setUndefinedExRaidSetting(Integer undefinedExRaidSetting) {
        this.undefinedExRaidSetting = undefinedExRaidSetting;
    }
}
