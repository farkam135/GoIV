package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class GuaranteedLimitedPokemonReward {
    @Expose
    private PokemonId pokemon;
    @Expose
    private String identifier;
    @Expose
    private Integer perCompetitiveSeasonMaxCount;
    @Expose
    private Integer lifetimeMaxCount;

    public PokemonId getPokemon() { return pokemon; }

    public void setPokemon(PokemonId pokemon) { this.pokemon = pokemon; }

    public String getIdentifier() { return identifier; }

    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public Integer getPerCompetitiveSeasonMaxCount() { return perCompetitiveSeasonMaxCount; }

    public void setPerCompetitiveSeasonMaxCount(Integer perCompetitiveSeasonMaxCount) { this.perCompetitiveSeasonMaxCount = perCompetitiveSeasonMaxCount; }

    public Integer getLifetimeMaxCount() { return lifetimeMaxCount; }

    public void setLifetimeMaxCount(Integer lifetimeMaxCount) { this.lifetimeMaxCount = lifetimeMaxCount; }
}
