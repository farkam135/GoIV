package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class LimitedPurchaseSkuSettings {
    @Expose
    private Integer purchaseLimit;
    @Expose
    private String chronoUnit;
    @Expose
    private String lootTableId;
    @Expose
    private Integer resetInterval;
    @Expose
    private Integer version;

    public Integer getPurchaseLimit() { return purchaseLimit; }

    public void setPurchaseLimit(Integer purchaseLimit) { this.purchaseLimit = purchaseLimit; }

    public String getChronoUnit() { return chronoUnit; }

    public void setChronoUnit(String chronoUnit) { this.chronoUnit = chronoUnit; }

    public String getLootTableId() { return lootTableId; }

    public void setLootTableId(String lootTableId) { this.lootTableId = lootTableId; }

    public Integer getResetInterval() { return resetInterval; }

    public void setResetInterval(Integer resetInterval) { this.resetInterval = resetInterval; }

    public Integer getVersion() { return version; }

    public void setVersion(Integer version) { this.version = version; }
}
