package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WithPokemonType {
    @SerializedName("pokemonType")
    @Expose
    private List<String> pokemonTypes = null;

    public List<String> getPokemonTypes() { return pokemonTypes; }

    public void setPokemonTypes(List<String> pokemonTypes) { this.pokemonTypes = pokemonTypes; }
}
