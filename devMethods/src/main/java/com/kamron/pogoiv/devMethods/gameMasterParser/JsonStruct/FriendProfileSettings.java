package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class FriendProfileSettings {
    @Expose
    private Boolean enableSwiping = false;

    public Boolean getEnableSwiping() { return enableSwiping = false; }

    public void setEnableSwiping(Boolean enableSwiping) { this.enableSwiping = enableSwiping = false; }
}
