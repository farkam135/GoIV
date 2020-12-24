package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PartyRecommendationSettings {
    @Expose
    private String mode;
    @Expose
    private Double variance;
    @Expose
    private Double thirdMoveWeight;
    @Expose
    private Double megaEvoCombatRatingScale;

    public String getMode() { return mode; }

    public void setMode(String mode) { this.mode = mode; }

    public Double getVariance() { return variance; }

    public void setVariance(Double variance) { this.variance = variance; }

    public Double getThirdMoveWeight() { return thirdMoveWeight; }

    public void setThirdMoveWeight(Double thirdMoveWeight) { this.thirdMoveWeight = thirdMoveWeight; }

    public Double getMegaEvoCombatRatingScale() { return megaEvoCombatRatingScale; }

    public void setMegaEvoCombatRatingScale(Double megaEvoCombatRatingScale) { this.megaEvoCombatRatingScale = megaEvoCombatRatingScale; }
}
