package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonHomeEnergyCosts {
    @Expose
    private String pokemonClass;
    @Expose
    private Integer base;
    @Expose
    private Integer shiny;
    @Expose
    private Integer cp1001To2000;
    @Expose
    private Integer cp2001ToInf;

    public String getPokemonClass() { return pokemonClass; }

    public void setPokemonClass(String pokemonClass) { this.pokemonClass = pokemonClass; }

    public Integer getBase() { return base; }

    public void setBase(Integer base) { this.base = base; }

    public Integer getShiny() { return shiny; }

    public void setShiny(Integer shiny) { this.shiny = shiny; }

    public Integer getCp1001To2000() { return cp1001To2000; }

    public void setCp1001To2000(Integer cp1001To2000) { this.cp1001To2000 = cp1001To2000; }

    public Integer getCp2001ToInf() { return cp2001ToInf; }

    public void setCp2001ToInf(Integer cp2001ToInf) { this.cp2001ToInf = cp2001ToInf; }
}
