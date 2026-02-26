package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonHomeSettings {
    @Expose
    private Integer playerMinLevel;
    @Expose
    private Integer transporterMaxEnergy;
    @Expose
    private String energySkuId;
    @Expose
    private Integer transporterEnergyGainPerHour;

    public Integer getPlayerMinLevel() { return playerMinLevel; }

    public void setPlayerMinLevel(Integer playerMinLevel) { this.playerMinLevel = playerMinLevel; }

    public Integer getTransporterMaxEnergy() { return transporterMaxEnergy; }

    public void setTransporterMaxEnergy(Integer transporterMaxEnergy) {
        this.transporterMaxEnergy = transporterMaxEnergy;
    }

    public String getEnergySkuId() { return energySkuId; }

    public void setEnergySkuId(String energySkuId) { this.energySkuId = energySkuId; }

    public Integer getTransporterEnergyGainPerHour() { return transporterEnergyGainPerHour; }

    public void setTransporterEnergyGainPerHour(Integer transporterEnergyGainPerHour) {
        this.transporterEnergyGainPerHour = transporterEnergyGainPerHour;
    }
}
