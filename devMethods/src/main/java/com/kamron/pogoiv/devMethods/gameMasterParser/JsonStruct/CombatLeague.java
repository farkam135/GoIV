package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CombatLeague {
    @Expose
    private String title;
    @Expose
    private Boolean enabled = false;
    @SerializedName("unlockCondition")
    @Expose
    private List<UnlockCondition> unlockConditions = null;
    @SerializedName("pokemonCondition")
    @Expose
    private List<PokemonCondition> pokemonConditions = null;
    @Expose
    private String iconUrl;
    @Expose
    private Integer pokemonCount;
    @Expose
    private List<String> bannedPokemon = null;
    @Expose
    private String badgeType;
    @Expose
    private String leagueType;
    @Expose
    private Boolean allowTempEvos = false;
    @Expose
    private String battlePartyCombatLeagueTemplateId;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public List<UnlockCondition> getUnlockConditions() { return unlockConditions; }

    public void setUnlockConditions(
            List<UnlockCondition> unlockConditions) { this.unlockConditions = unlockConditions; }

    public List<PokemonCondition> getPokemonConditions() { return pokemonConditions; }

    public void setPokemonConditions(
            List<PokemonCondition> pokemonConditions) { this.pokemonConditions = pokemonConditions; }

    public String getIconUrl() { return iconUrl; }

    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Integer getPokemonCount() { return pokemonCount; }

    public void setPokemonCount(Integer pokemonCount) { this.pokemonCount = pokemonCount; }

    public List<String> getBannedPokemon() { return bannedPokemon; }

    public void setBannedPokemon(List<String> bannedPokemon) { this.bannedPokemon = bannedPokemon; }

    public String getBadgeType() { return badgeType; }

    public void setBadgeType(String badgeType) { this.badgeType = badgeType; }

    public String getLeagueType() { return leagueType; }

    public void setLeagueType(String leagueType) { this.leagueType = leagueType; }

    public Boolean getAllowTempEvos() { return allowTempEvos; }

    public void setAllowTempEvos(Boolean allowTempEvos) { this.allowTempEvos = allowTempEvos; }

    public String getBattlePartyCombatLeagueTemplateId() { return battlePartyCombatLeagueTemplateId; }

    public void setBattlePartyCombatLeagueTemplateId(String battlePartyCombatLeagueTemplateId) {
        this.battlePartyCombatLeagueTemplateId = battlePartyCombatLeagueTemplateId;
    }
}
