package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class EggTransparencySettings {
    @Expose
    private boolean enableEggDistribution;

    public boolean isEnableEggDistribution() {
        return enableEggDistribution;
    }

    public void setEnableEggDistribution(boolean enableEggDistribution) {
        this.enableEggDistribution = enableEggDistribution;
    }
}
