package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class BelugaPokemonWhitelist {
    @Expose
    private Integer maxAllowedPokemonPokedexNumber;
    @Expose
    private List<String> additionalPokemonAllowed = null;
    @Expose
    private List<String> costumesAllowed = null;

    public Integer getMaxAllowedPokemonPokedexNumber() { return maxAllowedPokemonPokedexNumber; }

    public void setMaxAllowedPokemonPokedexNumber(Integer maxAllowedPokemonPokedexNumber) { this.maxAllowedPokemonPokedexNumber = maxAllowedPokemonPokedexNumber; }

    public List<String> getAdditionalPokemonAllowed() { return additionalPokemonAllowed; }

    public void setAdditionalPokemonAllowed(List<String> additionalPokemonAllowed) { this.additionalPokemonAllowed =         additionalPokemonAllowed;
    }

    public List<String> getCostumesAllowed() { return costumesAllowed; }

    public void setCostumesAllowed(List<String> costumesAllowed) { this.costumesAllowed = costumesAllowed; }
}
