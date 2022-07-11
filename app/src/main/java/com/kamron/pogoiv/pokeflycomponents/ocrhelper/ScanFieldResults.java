package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import androidx.annotation.ColorInt;

public class ScanFieldResults {

    public ScanArea pokemonNameArea;
    public ScanArea pokemonTypeArea;
    public ScanArea pokemonGenderArea;
    public ScanArea candyNameArea;
    public ScanArea pokemonHpArea;
    public ScanArea pokemonCpArea;
    public ScanArea pokemonCandyAmountArea = null;
    public ScanArea pokemonEvolutionCostArea = null;
    public ScanArea pokemonPowerUpStardustCostArea = null;
    public ScanArea pokemonPowerUpCandyCostArea = null;
    public ScanPoint arcCenter;
    public Integer arcRadius;
    public ScanPoint infoScreenCardWhitePixelPoint;
    public @ColorInt Integer infoScreenCardWhitePixelColor;
    public ScanPoint infoScreenFabGreenPixelPoint;
    public @ColorInt Integer infoScreenFabGreenPixelColor;
    public Boolean candyNameWrapped = false;

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

    /**
     * Adjusts the relevant scan areas upwards if the calibration pokemon's candy text wrapped to a second line
     */
    public void finalAdjustments() {
        if (!candyNameWrapped) {
            return;
        }
        // Same as in OcrHelper
        int adj = (int) (candyNameArea.height * 0.8);

        if (pokemonPowerUpCandyCostArea != null) {
            pokemonPowerUpCandyCostArea.yPoint -= adj;
        }
        if (pokemonPowerUpStardustCostArea != null) {
            pokemonPowerUpStardustCostArea.yPoint -= adj;
        }
        if (pokemonEvolutionCostArea != null) {
            pokemonEvolutionCostArea.yPoint -= adj;
        }
    }

}
