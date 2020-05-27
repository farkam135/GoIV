
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gender {

    @SerializedName("malePercent")
    @Expose
    private Double malePercent;
    @SerializedName("femalePercent")
    @Expose
    private Double femalePercent;
    @SerializedName("genderlessPercent")
    @Expose
    private Double genderlessPercent;

    public Double getMalePercent() {
        return malePercent;
    }

    public void setMalePercent(Double malePercent) {
        this.malePercent = malePercent;
    }

    public Double getFemalePercent() {
        return femalePercent;
    }

    public void setFemalePercent(Double femalePercent) {
        this.femalePercent = femalePercent;
    }

    public Double getGenderlessPercent() {
        return genderlessPercent;
    }

    public void setGenderlessPercent(Double genderlessPercent) {
        this.genderlessPercent = genderlessPercent;
    }

}
