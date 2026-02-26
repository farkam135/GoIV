package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Gender {
    @Expose
    private Double malePercent;
    @Expose
    private Double femalePercent;
    @Expose
    private Double genderlessPercent;

    public Double getMalePercent() { return malePercent; }

    public void setMalePercent(Double malePercent) { this.malePercent = malePercent; }

    public Double getFemalePercent() { return femalePercent; }

    public void setFemalePercent(Double femalePercent) { this.femalePercent = femalePercent; }

    public Double getGenderlessPercent() { return genderlessPercent; }

    public void setGenderlessPercent(Double genderlessPercent) { this.genderlessPercent = genderlessPercent; }
}
