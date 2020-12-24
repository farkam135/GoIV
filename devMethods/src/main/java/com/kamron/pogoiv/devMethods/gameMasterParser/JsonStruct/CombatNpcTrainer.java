package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CombatNpcTrainer {
    static public class AvailablePokemon {
        @Expose
        private String pokemonType;
        @Expose
        private PokemonDisplay pokemonDisplay;

        public String getPokemonType() { return pokemonType; }

        public void setPokemonType(String pokemonType) { this.pokemonType = pokemonType; }

        public PokemonDisplay getPokemonDisplay() { return pokemonDisplay; }

        public void setPokemonDisplay(PokemonDisplay pokemonDisplay) { this.pokemonDisplay = pokemonDisplay; }
    }

    @Expose
    private String trainerName;
    @Expose
    private String combatLeagueTemplateId;
    @Expose
    private String combatPersonalityId;
    @Expose
    private Avatar avatar;
    @Expose
    private List<AvailablePokemon> availablePokemon = null;
    @Expose
    private String trainerTitle;
    @Expose
    private String trainerQuote;
    @Expose
    private String iconUrl;
    @Expose
    private String backdropImageBundle;

    public String getTrainerName() { return trainerName; }

    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getCombatLeagueTemplateId() { return combatLeagueTemplateId; }

    public void setCombatLeagueTemplateId(String combatLeagueTemplateId) { this.combatLeagueTemplateId = combatLeagueTemplateId; }

    public String getCombatPersonalityId() { return combatPersonalityId; }

    public void setCombatPersonalityId(String combatPersonalityId) { this.combatPersonalityId = combatPersonalityId; }

    public Avatar getAvatar() { return avatar; }

    public void setAvatar(Avatar avatar) { this.avatar = avatar; }

    public List<AvailablePokemon> getAvailablePokemon() { return availablePokemon; }

    public void setAvailablePokemon(List<AvailablePokemon> availablePokemon) { this.availablePokemon = availablePokemon; }

    public String getTrainerTitle() { return trainerTitle; }

    public void setTrainerTitle(String trainerTitle) { this.trainerTitle = trainerTitle; }

    public String getTrainerQuote() { return trainerQuote; }

    public void setTrainerQuote(String trainerQuote) { this.trainerQuote = trainerQuote; }

    public String getIconUrl() { return iconUrl; }

    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getBackdropImageBundle() { return backdropImageBundle; }

    public void setBackdropImageBundle(String backdropImageBundle) { this.backdropImageBundle = backdropImageBundle; }
}
