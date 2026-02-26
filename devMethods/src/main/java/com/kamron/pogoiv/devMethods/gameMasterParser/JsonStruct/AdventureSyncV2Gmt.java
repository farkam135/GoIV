package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class AdventureSyncV2Gmt {
    @Expose
    private Boolean featureEnabled = false;

    public Boolean getFeatureEnabled() { return featureEnabled; }

    public void setFeatureEnabled(Boolean featureEnabled) { this.featureEnabled = featureEnabled; }
}
