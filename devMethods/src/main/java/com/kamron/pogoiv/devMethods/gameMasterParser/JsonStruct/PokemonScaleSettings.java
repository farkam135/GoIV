package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonScaleSettings {
    @Expose
    private String pokemonScaleMode;
    @Expose
    private Double minHeight;
    @Expose
    private Double maxHeight;

    public String getPokemonScaleMode() { return pokemonScaleMode; }

    public void setPokemonScaleMode(String pokemonScaleMode) { this.pokemonScaleMode = pokemonScaleMode; }

    public Double getMinHeight() { return minHeight; }

    public void setMinHeight(Double minHeight) { this.minHeight = minHeight; }

    public Double getMaxHeight() { return maxHeight; }

    public void setMaxHeight(Double maxHeight) { this.maxHeight = maxHeight; }
}
