
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Form {

    @SerializedName("form")
    @Expose
    private String form;
    @SerializedName("assetBundleValue")
    @Expose
    private Integer assetBundleValue;

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Integer getAssetBundleValue() {
        return assetBundleValue;
    }

    public void setAssetBundleValue(Integer assetBundleValue) {
        this.assetBundleValue = assetBundleValue;
    }

}
