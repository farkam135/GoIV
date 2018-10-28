
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormSettings {

    @SerializedName("pokemon")
    @Expose
    private String pokemon;
    @SerializedName("forms")
    @Expose
    private List<Form> forms = null;

    public String getPokemon() {
        return pokemon;
    }

    public void setPokemon(String pokemon) {
        this.pokemon = pokemon;
    }

    public List<Form> getForms() {
        return forms;
    }

    public void setForms(List<Form> forms) {
        this.forms = forms;
    }

}
