package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Goal {
    @SerializedName("condition")
    @Expose
    private List<Condition> conditions = null;
    @Expose
    private Integer target;

    public List<Condition> getConditions() { return conditions; }

    public void setConditions(List<Condition> conditions) { this.conditions = conditions; }

    public Integer getTarget() { return target; }

    public void setTarget(Integer target) { this.target = target; }
}
