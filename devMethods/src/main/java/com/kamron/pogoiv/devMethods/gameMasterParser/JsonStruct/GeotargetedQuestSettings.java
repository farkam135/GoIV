package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class GeotargetedQuestSettings {
    @Expose
    private Boolean enableGeotargetedQuests = false;

    public Boolean getEnableGeotargetedQuests() { return enableGeotargetedQuests = false; }

    public void setEnableGeotargetedQuests(Boolean enableGeotargetedQuests) { this.enableGeotargetedQuests = enableGeotargetedQuests = false; }
}
