package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonTagSettings {
    @Expose
    private Integer minPlayerLevelForPokemonTagging;
    @SerializedName("colorBinding")
    @Expose
    private List<ColorBinding> colorBindings = null;
    @Expose
    private Integer maxNumTagsAllowed;

    public Integer getMinPlayerLevelForPokemonTagging() { return minPlayerLevelForPokemonTagging; }

    public void setMinPlayerLevelForPokemonTagging(Integer minPlayerLevelForPokemonTagging) {
        this.minPlayerLevelForPokemonTagging = minPlayerLevelForPokemonTagging;
    }

    public List<ColorBinding> getColorBindings() { return colorBindings; }

    public void setColorBindings(List<ColorBinding> colorBindings) { this.colorBindings = colorBindings; }

    public Integer getMaxNumTagsAllowed() { return maxNumTagsAllowed; }

    public void setMaxNumTagsAllowed(Integer maxNumTagsAllowed) { this.maxNumTagsAllowed = maxNumTagsAllowed; }
}
