package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class PokemonWhitelist {
    public static class Pokemon {
        @Expose
        private String id;
        @Expose
        private String form;
        @Expose
        private List<String> forms;

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getForm() { return form; }

        public void setForm(String form) { this.form = form; }

        public List<String> getForms() {
            return forms;
        }

        public void setForms(List<String> forms) {
            this.forms = forms;
        }
    }

    @Expose
    private List<Pokemon> pokemon = null;

    public List<Pokemon> getPokemon() { return pokemon; }

    public void setPokemon(List<Pokemon> pokemon) { this.pokemon = pokemon; }
}
