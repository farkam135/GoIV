package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Incense {
    @Expose
    private Integer incenseLifetimeSeconds;
    @Expose
    private Double spawnTableProbability;

    public Integer getIncenseLifetimeSeconds() { return incenseLifetimeSeconds; }

    public void setIncenseLifetimeSeconds(Integer incenseLifetimeSeconds) { this.incenseLifetimeSeconds = incenseLifetimeSeconds; }

    public Double getSpawnTableProbability() { return spawnTableProbability; }

    public void setSpawnTableProbability(Double spawnTableProbability) { this.spawnTableProbability = spawnTableProbability; }
}
