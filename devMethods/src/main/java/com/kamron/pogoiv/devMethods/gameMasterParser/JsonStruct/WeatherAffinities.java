package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherAffinities {
    @Expose
    private String weatherCondition;
    @SerializedName("pokemonTypes")
    @Expose
    private List<String> pokemonType = null;

    public String getWeatherCondition() { return weatherCondition; }

    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public List<String> getPokemonType() { return pokemonType; }

    public void setPokemonType(List<String> pokemonType) { this.pokemonType = pokemonType; }
}
