package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PlatypusRolloutSettings {
    @Expose
    private Integer buddyV2MinPlayerLevel;
    @Expose
    private Integer buddyMultiplayerMinPlayerLevel;
    @Expose
    private WallabySettings wallabySettings;

    public Integer getBuddyV2MinPlayerLevel() { return buddyV2MinPlayerLevel; }

    public void setBuddyV2MinPlayerLevel(Integer buddyV2MinPlayerLevel) {
        this.buddyV2MinPlayerLevel = buddyV2MinPlayerLevel;
    }

    public Integer getBuddyMultiplayerMinPlayerLevel() { return buddyMultiplayerMinPlayerLevel; }

    public void setBuddyMultiplayerMinPlayerLevel(Integer buddyMultiplayerMinPlayerLevel) {
        this.buddyMultiplayerMinPlayerLevel = buddyMultiplayerMinPlayerLevel;
    }

    public WallabySettings getWallabySettings() { return wallabySettings; }

    public void setWallabySettings(WallabySettings wallabySettings) { this.wallabySettings = wallabySettings; }
}
