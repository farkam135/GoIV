package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class TemporaryEvolutionSettings {
    @Expose
    private String pokemonId;
    @Expose
    private List<TemporaryEvolution> temporaryEvolutions = null;

    public String getPokemonId() { return pokemonId; }

    public void setPokemonId(String pokemonId) { this.pokemonId = pokemonId; }

    public List<TemporaryEvolution> getTemporaryEvolutions() { return temporaryEvolutions; }

    public void setTemporaryEvolutions(List<TemporaryEvolution> temporaryEvolutions) {
        this.temporaryEvolutions = temporaryEvolutions;
    }
}
