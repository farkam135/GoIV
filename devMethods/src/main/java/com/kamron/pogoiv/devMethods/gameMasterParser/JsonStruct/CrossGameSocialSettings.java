package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CrossGameSocialSettings {
    @Expose
    private Boolean onlineStatusEnabledOverrideLevel = false;
    @Expose
    private Boolean nianticProfileEnabledOverrideLevel = false;

    public Boolean getOnlineStatusEnabledOverrideLevel() { return onlineStatusEnabledOverrideLevel; }

    public void setOnlineStatusEnabledOverrideLevel(Boolean onlineStatusEnabledOverrideLevel) {
        this.onlineStatusEnabledOverrideLevel = onlineStatusEnabledOverrideLevel;
    }

    public Boolean getNianticProfileEnabledOverrideLevel() { return nianticProfileEnabledOverrideLevel; }

    public void setNianticProfileEnabledOverrideLevel(Boolean nianticProfileEnabledOverrideLevel) {
        this.nianticProfileEnabledOverrideLevel = nianticProfileEnabledOverrideLevel;
    }
}
