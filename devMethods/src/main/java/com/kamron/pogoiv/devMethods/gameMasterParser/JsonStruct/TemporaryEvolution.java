package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class TemporaryEvolution {
    @Expose
    private String temporaryEvolutionId;
    @Expose
    private Integer assetBundleValue;

    public String getTemporaryEvolutionId() { return temporaryEvolutionId; }

    public void setTemporaryEvolutionId(String temporaryEvolutionId) { this.temporaryEvolutionId = temporaryEvolutionId; }

    public Integer getAssetBundleValue() { return assetBundleValue; }

    public void setAssetBundleValue(Integer assetBundleValue) { this.assetBundleValue = assetBundleValue; }
}
