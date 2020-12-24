package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class UnlockCondition {
    @Expose
    private String type;
    @Expose
    private Integer minPokemonCount;
    @Expose
    private WithPokemonCpLimit withPokemonCpLimit;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Integer getMinPokemonCount() { return minPokemonCount; }

    public void setMinPokemonCount(Integer minPokemonCount) { this.minPokemonCount = minPokemonCount; }

    public WithPokemonCpLimit getWithPokemonCpLimit() { return withPokemonCpLimit; }

    public void setWithPokemonCpLimit(WithPokemonCpLimit withPokemonCpLimit) { this.withPokemonCpLimit = withPokemonCpLimit; }
}
