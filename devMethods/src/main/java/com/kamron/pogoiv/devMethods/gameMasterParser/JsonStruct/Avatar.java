package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Avatar {
    @Expose
    private Integer avatar;

    public Integer getAvatar() { return avatar; }

    public void setAvatar(Integer avatar) { this.avatar = avatar; }
}
