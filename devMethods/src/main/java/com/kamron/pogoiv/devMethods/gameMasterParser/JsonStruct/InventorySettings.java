package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class InventorySettings {
    @Expose
    private Integer maxPokemon;
    @Expose
    private Integer maxBagItems;
    @Expose
    private Integer basePokemon;
    @Expose
    private Integer baseBagItems;
    @Expose
    private Integer baseEggs;
    @Expose
    private Integer maxTeamChanges;
    @Expose
    private String teamChangeItemResetPeriodInDays;
    @Expose
    private String maxItemBoostDurationMs;
    @Expose
    private Boolean enableEggsNotInventory = false;
    @Expose
    private Integer specialEggOverflowSpots;

    public Integer getMaxPokemon() { return maxPokemon; }

    public void setMaxPokemon(Integer maxPokemon) { this.maxPokemon = maxPokemon; }

    public Integer getMaxBagItems() { return maxBagItems; }

    public void setMaxBagItems(Integer maxBagItems) { this.maxBagItems = maxBagItems; }

    public Integer getBasePokemon() { return basePokemon; }

    public void setBasePokemon(Integer basePokemon) { this.basePokemon = basePokemon; }

    public Integer getBaseBagItems() { return baseBagItems; }

    public void setBaseBagItems(Integer baseBagItems) { this.baseBagItems = baseBagItems; }

    public Integer getBaseEggs() { return baseEggs; }

    public void setBaseEggs(Integer baseEggs) { this.baseEggs = baseEggs; }

    public Integer getMaxTeamChanges() { return maxTeamChanges; }

    public void setMaxTeamChanges(Integer maxTeamChanges) { this.maxTeamChanges = maxTeamChanges; }

    public String getTeamChangeItemResetPeriodInDays() { return teamChangeItemResetPeriodInDays; }

    public void setTeamChangeItemResetPeriodInDays(String teamChangeItemResetPeriodInDays) { this.teamChangeItemResetPeriodInDays = teamChangeItemResetPeriodInDays; }

    public String getMaxItemBoostDurationMs() { return maxItemBoostDurationMs; }

    public void setMaxItemBoostDurationMs(String maxItemBoostDurationMs) { this.maxItemBoostDurationMs = maxItemBoostDurationMs; }

    public Boolean getEnableEggsNotInventory() { return enableEggsNotInventory = false; }

    public void setEnableEggsNotInventory(Boolean enableEggsNotInventory) { this.enableEggsNotInventory = enableEggsNotInventory = false; }

    public Integer getSpecialEggOverflowSpots() { return specialEggOverflowSpots; }

    public void setSpecialEggOverflowSpots(Integer specialEggOverflowSpots) { this.specialEggOverflowSpots = specialEggOverflowSpots; }
}
