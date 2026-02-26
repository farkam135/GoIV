package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Form {
    @Expose
    private String form;
    @Expose
    private String assetBundleSuffix;
    @Expose
    private Integer assetBundleValue;

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getAssetBundleSuffix() { return assetBundleSuffix; }

    public void setAssetBundleSuffix(String assetBundleSuffix) { this.assetBundleSuffix = assetBundleSuffix; }

    public Integer getAssetBundleValue() {
        return assetBundleValue;
    }

    public void setAssetBundleValue(Integer assetBundleValue) {
        this.assetBundleValue = assetBundleValue;
    }

}