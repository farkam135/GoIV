package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class ThirdMove {
    @Expose
    private Integer stardustToUnlock;
    @Expose
    private Integer candyToUnlock;

    public Integer getStardustToUnlock() { return stardustToUnlock; }

    public void setStardustToUnlock(Integer stardustToUnlock) { this.stardustToUnlock = stardustToUnlock; }

    public Integer getCandyToUnlock() { return candyToUnlock; }

    public void setCandyToUnlock(Integer candyToUnlock) { this.candyToUnlock = candyToUnlock; }
}
