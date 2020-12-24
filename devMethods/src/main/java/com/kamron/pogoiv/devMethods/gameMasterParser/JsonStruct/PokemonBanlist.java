package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class PokemonBanlist {
    static public class Pokemon {
        @Expose
        private String id;
        @Expose
        private String form;

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getForm() { return form; }

        public void setForm(String form) { this.form = form; }
    }

    @Expose
    private List<Pokemon> pokemon = null;

    public List<Pokemon> getPokemon() { return pokemon; }

    public void setPokemon(List<Pokemon> pokemon) { this.pokemon = pokemon; }
}
