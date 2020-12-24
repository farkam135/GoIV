package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CrossGameSocialSettings {
    @Expose
    private Boolean onlineStatusEnabledOverrideLevel = false;
    @Expose
    private Boolean nianticProfileEnabledOverrideLevel = false;

    public Boolean getOnlineStatusEnabledOverrideLevel() { return onlineStatusEnabledOverrideLevel = false; }

    public void setOnlineStatusEnabledOverrideLevel(Boolean onlineStatusEnabledOverrideLevel) { this.onlineStatusEnabledOverrideLevel = onlineStatusEnabledOverrideLevel = false; }

    public Boolean getNianticProfileEnabledOverrideLevel() { return nianticProfileEnabledOverrideLevel = false; }

    public void setNianticProfileEnabledOverrideLevel(Boolean nianticProfileEnabledOverrideLevel) { this.nianticProfileEnabledOverrideLevel = nianticProfileEnabledOverrideLevel = false; }
}
