package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Item {
    @Expose
    private String uniqueId;
    @Expose
    private String itemType;
    @Expose
    private String category;
    @Expose
    private Integer dropTrainerLevel;
    @Expose
    private Food food;
    @Expose
    private GlobalEventTicket globalEventTicket;
    @Expose
    private IncidentTicket incidentTicket;
    @Expose
    private Potion potion;
    @Expose
    private Incense incense;
    @Expose
    private EggIncubator eggIncubator;
    @Expose
    private InventoryUpgrade inventoryUpgrade;
    @Expose
    private XpBoost xpBoost;
    @Expose
    private Revive revive;
    @Expose
    private StardustBoost stardustBoost;

    public String getUniqueId() { return uniqueId; }

    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getItemType() { return itemType; }

    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public Integer getDropTrainerLevel() { return dropTrainerLevel; }

    public void setDropTrainerLevel(Integer dropTrainerLevel) { this.dropTrainerLevel = dropTrainerLevel; }

    public Food getFood() { return food; }

    public void setFood(Food food) { this.food = food; }

    public GlobalEventTicket getGlobalEventTicket() { return globalEventTicket; }

    public void setGlobalEventTicket(GlobalEventTicket globalEventTicket) {
        this.globalEventTicket = globalEventTicket;
    }

    public IncidentTicket getIncidentTicket() { return incidentTicket; }

    public void setIncidentTicket(IncidentTicket incidentTicket) { this.incidentTicket = incidentTicket; }

    public Potion getPotion() { return potion; }

    public void setPotion(Potion potion) { this.potion = potion; }

    public Incense getIncense() { return incense; }

    public void setIncense(Incense incense) { this.incense = incense; }

    public EggIncubator getEggIncubator() { return eggIncubator; }

    public void setEggIncubator(EggIncubator eggIncubator) { this.eggIncubator = eggIncubator; }

    public InventoryUpgrade getInventoryUpgrade() { return inventoryUpgrade; }

    public void setInventoryUpgrade(InventoryUpgrade inventoryUpgrade) { this.inventoryUpgrade = inventoryUpgrade; }

    public XpBoost getXpBoost() { return xpBoost; }

    public void setXpBoost(XpBoost xpBoost) { this.xpBoost = xpBoost; }

    public Revive getRevive() { return revive; }

    public void setRevive(Revive revive) { this.revive = revive; }

    public StardustBoost getStardustBoost() { return stardustBoost; }

    public void setStardustBoost(StardustBoost stardustBoost) { this.stardustBoost = stardustBoost; }
}
