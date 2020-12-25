package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Badge {
    @Expose
    private String badgeType;
    @Expose
    private Integer badgeRanks;
    @Expose
    private List<Integer> targets = null;
    @SerializedName("eventBadge")
    @Expose
    private Boolean isEventBadge = false;

    public String getBadgeType() { return badgeType; }

    public void setBadgeType(String badgeType) { this.badgeType = badgeType; }

    public Integer getBadgeRanks() { return badgeRanks; }

    public void setBadgeRanks(Integer badgeRanks) { this.badgeRanks = badgeRanks; }

    public List<Integer> getTargets() { return targets; }

    public void setTargets(List<Integer> targets) { this.targets = targets; }

    public Boolean getEventBadge() { return isEventBadge; }

    public void setEventBadge(Boolean eventBadge) { isEventBadge = eventBadge; }
}
