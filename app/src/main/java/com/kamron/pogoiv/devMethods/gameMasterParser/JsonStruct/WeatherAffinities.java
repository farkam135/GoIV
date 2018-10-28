
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherAffinities {

    @SerializedName("weatherCondition")
    @Expose
    private String weatherCondition;
    @SerializedName("pokemonType")
    @Expose
    private List<String> pokemonType = null;

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public List<String> getPokemonType() {
        return pokemonType;
    }

    public void setPokemonType(List<String> pokemonType) {
        this.pokemonType = pokemonType;
    }

}
