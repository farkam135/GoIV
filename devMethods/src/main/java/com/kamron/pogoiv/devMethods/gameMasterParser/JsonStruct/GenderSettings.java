package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class GenderSettings {
    @Expose
    private String pokemon;
    @Expose
    private Gender gender;

    public String getPokemon() { return pokemon; }

    public void setPokemon(String pokemon) { this.pokemon = pokemon; }

    public Gender getGender() { return gender; }

    public void setGender(Gender gender) { this.gender = gender; }
}
