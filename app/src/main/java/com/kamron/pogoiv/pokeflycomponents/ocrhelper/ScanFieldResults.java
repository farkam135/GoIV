package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import android.support.annotation.ColorInt;

public class ScanFieldResults {

    public ScanArea pokemonNameArea;
    public ScanArea pokemonTypeArea;
    public ScanArea pokemonGenderArea;
    public ScanArea candyNameArea;
    public ScanArea pokemonHpArea;
    public ScanArea pokemonCpArea;
    public ScanArea pokemonCandyAmountArea;
    public ScanArea pokemonEvolutionCostArea;
    public ScanArea pokemonPowerUpStardustCostArea;
    public ScanArea pokemonPowerUpCandyCostArea;
    public ScanPoint arcCenter;
    public Integer arcRadius;
    public ScanPoint infoScreenCardWhitePixelPoint;
    public @ColorInt Integer infoScreenCardWhitePixelColor;
    public ScanPoint infoScreenFabGreenPixelPoint;
    public @ColorInt Integer infoScreenFabGreenPixelColor;

    public boolean isCompleteCalibration() {
        return pokemonNameArea != null
                && pokemonTypeArea != null
                && pokemonGenderArea != null
                && candyNameArea != null
                && pokemonHpArea != null
                && pokemonCpArea != null
                && pokemonCandyAmountArea != null
                && pokemonEvolutionCostArea != null
                && pokemonPowerUpStardustCostArea != null
                && pokemonPowerUpCandyCostArea != null
                && arcCenter != null
                && arcRadius != null
                && infoScreenCardWhitePixelPoint != null
                && infoScreenCardWhitePixelColor != null
                && infoScreenFabGreenPixelPoint != null
                && infoScreenFabGreenPixelColor != null;
    }

}
