package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class InventoryUpgrade {
    @Expose
    private Integer additionalStorage;
    @Expose
    private String upgradeType;

    public Integer getAdditionalStorage() { return additionalStorage; }

    public void setAdditionalStorage(Integer additionalStorage) { this.additionalStorage = additionalStorage; }

    public String getUpgradeType() { return upgradeType; }

    public void setUpgradeType(String upgradeType) { this.upgradeType = upgradeType; }
}
