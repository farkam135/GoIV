package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class DeepLinkingSettings {
    @Expose
    private Integer minPlayerLevelForExternalLink;
    @Expose
    private Integer minPlayerLevelForNotificationLink;

    public Integer getMinPlayerLevelForExternalLink() { return minPlayerLevelForExternalLink; }

    public void setMinPlayerLevelForExternalLink(Integer minPlayerLevelForExternalLink) { this.minPlayerLevelForExternalLink = minPlayerLevelForExternalLink; }

    public Integer getMinPlayerLevelForNotificationLink() { return minPlayerLevelForNotificationLink; }

    public void setMinPlayerLevelForNotificationLink(Integer minPlayerLevelForNotificationLink) { this.minPlayerLevelForNotificationLink = minPlayerLevelForNotificationLink; }
}
