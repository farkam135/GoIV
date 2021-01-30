package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Potion {
    @Expose
    private Integer staAmount;
    @Expose
    private Double staPercent;

    public Integer getStaAmount() { return staAmount; }

    public void setStaAmount(Integer staAmount) { this.staAmount = staAmount; }

    public Double getStaPercent() { return staPercent; }

    public void setStaPercent(Double staPercent) { this.staPercent = staPercent; }
}
