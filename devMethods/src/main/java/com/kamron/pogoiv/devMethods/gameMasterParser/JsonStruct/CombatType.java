package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class CombatType {
    @Expose
    private String type;
    @Expose
    private Double niceLevelThreshold;
    @Expose
    private Double greatLevelThreshold;
    @Expose
    private Double excellentLevelThreshold;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Double getNiceLevelThreshold() { return niceLevelThreshold; }

    public void setNiceLevelThreshold(Double niceLevelThreshold) { this.niceLevelThreshold = niceLevelThreshold; }

    public Double getGreatLevelThreshold() { return greatLevelThreshold; }

    public void setGreatLevelThreshold(Double greatLevelThreshold) { this.greatLevelThreshold = greatLevelThreshold; }

    public Double getExcellentLevelThreshold() { return excellentLevelThreshold; }

    public void setExcellentLevelThreshold(Double excellentLevelThreshold) { this.excellentLevelThreshold = excellentLevelThreshold; }
}
