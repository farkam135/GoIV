package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IapSettings {
    @SerializedName("dailyDefenderBonusPerPokemon")
    @Expose
    private List<Integer> dailyDefenderBonusesPerPokemon = null;
    @Expose
    private Integer dailyDefenderBonusMaxDefenders;
    @SerializedName("dailyDefenderBonusCurrency")
    @Expose
    private List<String> dailyDefenderBonusCurrencies = null;
    @Expose
    private String minTimeBetweenClaimsMs;

    public List<Integer> getDailyDefenderBonusesPerPokemon() {
        return dailyDefenderBonusesPerPokemon;
    }

    public void setDailyDefenderBonusesPerPokemon(List<Integer> dailyDefenderBonusesPerPokemon) {
        this.dailyDefenderBonusesPerPokemon = dailyDefenderBonusesPerPokemon;
    }

    public Integer getDailyDefenderBonusMaxDefenders() {
        return dailyDefenderBonusMaxDefenders;
    }

    public void setDailyDefenderBonusMaxDefenders(Integer dailyDefenderBonusMaxDefenders) {
        this.dailyDefenderBonusMaxDefenders = dailyDefenderBonusMaxDefenders;
    }

    public List<String> getDailyDefenderBonusCurrencies() {
        return dailyDefenderBonusCurrencies;
    }

    public void setDailyDefenderBonusCurrencies(List<String> dailyDefenderBonusCurrencies) {
        this.dailyDefenderBonusCurrencies = dailyDefenderBonusCurrencies;
    }

    public String getMinTimeBetweenClaimsMs() {
        return minTimeBetweenClaimsMs;
    }

    public void setMinTimeBetweenClaimsMs(String minTimeBetweenClaimsMs) {
        this.minTimeBetweenClaimsMs = minTimeBetweenClaimsMs;
    }
}
