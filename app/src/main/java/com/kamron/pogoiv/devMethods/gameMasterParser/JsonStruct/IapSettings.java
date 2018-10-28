
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IapSettings {

    @SerializedName("dailyDefenderBonusPerPokemon")
    @Expose
    private List<Integer> dailyDefenderBonusPerPokemon = null;
    @SerializedName("dailyDefenderBonusMaxDefenders")
    @Expose
    private Integer dailyDefenderBonusMaxDefenders;
    @SerializedName("dailyDefenderBonusCurrency")
    @Expose
    private List<String> dailyDefenderBonusCurrency = null;
    @SerializedName("minTimeBetweenClaimsMs")
    @Expose
    private String minTimeBetweenClaimsMs;

    public List<Integer> getDailyDefenderBonusPerPokemon() {
        return dailyDefenderBonusPerPokemon;
    }

    public void setDailyDefenderBonusPerPokemon(List<Integer> dailyDefenderBonusPerPokemon) {
        this.dailyDefenderBonusPerPokemon = dailyDefenderBonusPerPokemon;
    }

    public Integer getDailyDefenderBonusMaxDefenders() {
        return dailyDefenderBonusMaxDefenders;
    }

    public void setDailyDefenderBonusMaxDefenders(Integer dailyDefenderBonusMaxDefenders) {
        this.dailyDefenderBonusMaxDefenders = dailyDefenderBonusMaxDefenders;
    }

    public List<String> getDailyDefenderBonusCurrency() {
        return dailyDefenderBonusCurrency;
    }

    public void setDailyDefenderBonusCurrency(List<String> dailyDefenderBonusCurrency) {
        this.dailyDefenderBonusCurrency = dailyDefenderBonusCurrency;
    }

    public String getMinTimeBetweenClaimsMs() {
        return minTimeBetweenClaimsMs;
    }

    public void setMinTimeBetweenClaimsMs(String minTimeBetweenClaimsMs) {
        this.minTimeBetweenClaimsMs = minTimeBetweenClaimsMs;
    }

}
