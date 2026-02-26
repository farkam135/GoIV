package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class GuiSearchSettings {
    @Expose
    private Boolean guiSearchEnabled = false;
    @Expose
    private Integer maxNumberRecentSearches;
    @Expose
    private Integer maxNumberFavoriteSearches;
    @Expose
    private Integer maxQueryLength;

    public Boolean getGuiSearchEnabled() { return guiSearchEnabled; }

    public void setGuiSearchEnabled(Boolean guiSearchEnabled) { this.guiSearchEnabled = guiSearchEnabled; }

    public Integer getMaxNumberRecentSearches() { return maxNumberRecentSearches; }

    public void setMaxNumberRecentSearches(Integer maxNumberRecentSearches) {
        this.maxNumberRecentSearches = maxNumberRecentSearches;
    }

    public Integer getMaxNumberFavoriteSearches() { return maxNumberFavoriteSearches; }

    public void setMaxNumberFavoriteSearches(Integer maxNumberFavoriteSearches) {
        this.maxNumberFavoriteSearches = maxNumberFavoriteSearches;
    }

    public Integer getMaxQueryLength() { return maxQueryLength; }

    public void setMaxQueryLength(Integer maxQueryLength) { this.maxQueryLength = maxQueryLength; }
}
