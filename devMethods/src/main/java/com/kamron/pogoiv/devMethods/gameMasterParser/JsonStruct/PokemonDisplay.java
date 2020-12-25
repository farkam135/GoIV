package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonDisplay {
    @Expose
    private String form;

    public String getForm() { return form; }

    public void setForm(String form) { this.form = form; }
}
