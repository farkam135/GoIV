package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OnboardingV2Settings {
    @SerializedName("pokedexId")
    @Expose
    private List<String> pokedexIds = null;
    @Expose
    private Integer eggKmUntilHatch;

    public List<String> getPokedexIds() { return pokedexIds; }

    public void setPokedexIds(List<String> pokedexIds) { this.pokedexIds = pokedexIds; }

    public Integer getEggKmUntilHatch() { return eggKmUntilHatch; }

    public void setEggKmUntilHatch(Integer eggKmUntilHatch) { this.eggKmUntilHatch = eggKmUntilHatch; }
}
