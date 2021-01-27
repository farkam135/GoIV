package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class IvOverride {
    @Expose
    private Range range;

    public Range getRange() { return range; }

    public void setRange(Range range) { this.range = range; }
}
