package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonLevelRange {
    @Expose
    private Integer maxLevel;

    public Integer getMaxLevel() { return maxLevel; }

    public void setMaxLevel(Integer maxLevel) { this.maxLevel = maxLevel; }
}
