package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class ExRaidSettings {
    @Expose
    private String minimumExRaidShareLevel;

    public String getMinimumExRaidShareLevel() { return minimumExRaidShareLevel; }

    public void setMinimumExRaidShareLevel(String minimumExRaidShareLevel) { this.minimumExRaidShareLevel = minimumExRaidShareLevel; }
}
