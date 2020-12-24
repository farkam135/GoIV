package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class IncidentTicket {
    @Expose
    private Boolean ignoreFullInventory = false;
    @Expose
    private Integer upgradeRequirementCount;
    @Expose
    private String upgradedItem;

    public Boolean getIgnoreFullInventory() { return ignoreFullInventory = false; }

    public void setIgnoreFullInventory(Boolean ignoreFullInventory) { this.ignoreFullInventory = ignoreFullInventory = false; }

    public Integer getUpgradeRequirementCount() { return upgradeRequirementCount; }

    public void setUpgradeRequirementCount(Integer upgradeRequirementCount) { this.upgradeRequirementCount = upgradeRequirementCount; }

    public String getUpgradedItem() { return upgradedItem; }

    public void setUpgradedItem(String upgradedItem) { this.upgradedItem = upgradedItem; }
}
