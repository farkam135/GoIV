
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeSettings {

    @SerializedName("badgeType")
    @Expose
    private String badgeType;
    @SerializedName("badgeRank")
    @Expose
    private Integer badgeRank;
    @SerializedName("targets")
    @Expose
    private List<Integer> targets = null;
    @SerializedName("eventBadge")
    @Expose
    private Boolean eventBadge;

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getBadgeRank() {
        return badgeRank;
    }

    public void setBadgeRank(Integer badgeRank) {
        this.badgeRank = badgeRank;
    }

    public List<Integer> getTargets() {
        return targets;
    }

    public void setTargets(List<Integer> targets) {
        this.targets = targets;
    }

    public Boolean getEventBadge() {
        return eventBadge;
    }

    public void setEventBadge(Boolean eventBadge) {
        this.eventBadge = eventBadge;
    }

}
