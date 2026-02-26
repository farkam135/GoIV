package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonHomeFormReversion {
    @Expose
    private String pokemonId;
    @SerializedName("formMapping")
    @Expose
    private List<FormMapping> formMappings = null;

    public String getPokemonId() { return pokemonId; }

    public void setPokemonId(String pokemonId) { this.pokemonId = pokemonId; }

    public List<FormMapping> getFormMappings() { return formMappings; }

    public void setFormMappings(List<FormMapping> formMappings) { this.formMappings = formMappings; }
}
