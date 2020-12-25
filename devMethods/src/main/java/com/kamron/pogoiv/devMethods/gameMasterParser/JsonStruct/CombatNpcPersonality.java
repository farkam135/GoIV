package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CombatNpcPersonality {
    @Expose
    private String personalityName;
    @Expose
    private Double superEffectiveChance;
    @Expose
    private Double specialChance;
    @Expose
    private Double offensiveMinimumScore;
    @Expose
    private Double offensiveMaximumScore;
    @Expose
    private Double defensiveMinimumScore;
    @Expose
    private Double defensiveMaximumScore;

    public String getPersonalityName() { return personalityName; }

    public void setPersonalityName(String personalityName) { this.personalityName = personalityName; }

    public Double getSuperEffectiveChance() { return superEffectiveChance; }

    public void setSuperEffectiveChance(Double superEffectiveChance) {
        this.superEffectiveChance = superEffectiveChance;
    }

    public Double getSpecialChance() { return specialChance; }

    public void setSpecialChance(Double specialChance) { this.specialChance = specialChance; }

    public Double getOffensiveMinimumScore() { return offensiveMinimumScore; }

    public void setOffensiveMinimumScore(Double offensiveMinimumScore) {
        this.offensiveMinimumScore = offensiveMinimumScore;
    }

    public Double getOffensiveMaximumScore() { return offensiveMaximumScore; }

    public void setOffensiveMaximumScore(Double offensiveMaximumScore) {
        this.offensiveMaximumScore = offensiveMaximumScore;
    }

    public Double getDefensiveMinimumScore() { return defensiveMinimumScore; }

    public void setDefensiveMinimumScore(Double defensiveMinimumScore) {
        this.defensiveMinimumScore = defensiveMinimumScore;
    }

    public Double getDefensiveMaximumScore() { return defensiveMaximumScore; }

    public void setDefensiveMaximumScore(Double defensiveMaximumScore) {
        this.defensiveMaximumScore = defensiveMaximumScore;
    }
}
