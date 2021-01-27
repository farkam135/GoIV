package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class TemporaryEvolutionSettings {
    @Expose
    private String pokemon;
    @Expose
    private List<TemporaryEvolution> temporaryEvolutions = null;

    public String getPokemon() { return pokemon; }

    public void setPokemon(String pokemon) { this.pokemon = pokemon; }

    public List<TemporaryEvolution> getTemporaryEvolutions() { return temporaryEvolutions; }

    public void setTemporaryEvolutions(List<TemporaryEvolution> temporaryEvolutions) {
        this.temporaryEvolutions = temporaryEvolutions;
    }
}
