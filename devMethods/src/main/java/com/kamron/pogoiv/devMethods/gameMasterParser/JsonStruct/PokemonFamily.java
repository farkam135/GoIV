package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonFamily {
    @Expose
    private String familyId;
    @Expose
    private Integer candyPerXlCandy;
    @Expose
    private String megaEvolvablePokemonId;

    public String getFamilyId() { return familyId; }

    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public Integer getCandyPerXlCandy() { return candyPerXlCandy; }

    public void setCandyPerXlCandy(Integer candyPerXlCandy) { this.candyPerXlCandy = candyPerXlCandy; }

    public String getMegaEvolvablePokemonId() { return megaEvolvablePokemonId; }

    public void setMegaEvolvablePokemonId(String megaEvolvablePokemonId) {
        this.megaEvolvablePokemonId = megaEvolvablePokemonId;
    }
}
