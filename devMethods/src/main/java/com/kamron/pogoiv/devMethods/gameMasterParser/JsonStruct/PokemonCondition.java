package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonCondition {
    @Expose
    private String type;
    @Expose
    private WithPokemonCpLimit withPokemonCpLimit; // minCp field doesn't get used here.
    @Expose
    private PokemonCaughtTimestamp pokemonCaughtTimestamp;
    @Expose
    private PokemonLevelRange pokemonLevelRange;
    @Expose
    private WithPokemonType withPokemonType;
    @Expose
    private PokemonBanlist pokemonBanlist;
    @Expose
    private PokemonWhitelist pokemonWhitelist;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public WithPokemonCpLimit getWithPokemonCpLimit() { return withPokemonCpLimit; }

    public void setWithPokemonCpLimit(
            WithPokemonCpLimit withPokemonCpLimit) { this.withPokemonCpLimit = withPokemonCpLimit; }

    public PokemonCaughtTimestamp getPokemonCaughtTimestamp() { return pokemonCaughtTimestamp; }

    public void setPokemonCaughtTimestamp(
            PokemonCaughtTimestamp pokemonCaughtTimestamp) { this.pokemonCaughtTimestamp = pokemonCaughtTimestamp; }

    public PokemonLevelRange getPokemonLevelRange() { return pokemonLevelRange; }

    public void setPokemonLevelRange(
            PokemonLevelRange pokemonLevelRange) { this.pokemonLevelRange = pokemonLevelRange; }

    public WithPokemonType getWithPokemonType() { return withPokemonType; }

    public void setWithPokemonType(WithPokemonType withPokemonType) { this.withPokemonType = withPokemonType; }

    public PokemonBanlist getPokemonBanlist() { return pokemonBanlist; }

    public void setPokemonBanlist(PokemonBanlist pokemonBanlist) { this.pokemonBanlist = pokemonBanlist; }
}
