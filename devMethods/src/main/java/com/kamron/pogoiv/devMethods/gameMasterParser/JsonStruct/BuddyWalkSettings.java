package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyWalkSettings {
    @Expose
    private Double kmRequiredPerAffectionPoint;

    public Double getKmRequiredPerAffectionPoint() { return kmRequiredPerAffectionPoint; }

    public void setKmRequiredPerAffectionPoint(Double kmRequiredPerAffectionPoint) { this.kmRequiredPerAffectionPoint = kmRequiredPerAffectionPoint; }
}
