package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvatarGroupOrderSettings {
    @SerializedName("group")
    @Expose
    private List<Group> groups = null;

    public List<Group> getGroups() { return groups; }

    public void setGroups(List<Group> groups) { this.groups = groups; }
}
