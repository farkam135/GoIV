package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonId {
    @Expose
    private String pokemonId;
    @Expose
    private PokemonDisplay pokemonDisplay;

    public String getPokemonId() { return pokemonId; }

    public void setPokemonId(String pokemonId) { this.pokemonId = pokemonId; }

    public PokemonDisplay getPokemonDisplay() { return pokemonDisplay; }

    public void setPokemonDisplay(PokemonDisplay pokemonDisplay) { this.pokemonDisplay = pokemonDisplay; }
}
