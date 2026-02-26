package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Revive {
    @Expose
    private Double staPercent;

    public Double getStaPercent() { return staPercent; }

    public void setStaPercent(Double staPercent) { this.staPercent = staPercent; }
}
