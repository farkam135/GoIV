
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EvolutionBranch {

    @SerializedName("evolution")
    @Expose
    private String evolution;
    @SerializedName("candyCost")
    @Expose
    private Integer candyCost;

    public String getEvolution() {
        return evolution;
    }

    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }

    public Integer getCandyCost() {
        return candyCost;
    }

    public void setCandyCost(Integer candyCost) {
        this.candyCost = candyCost;
    }

}
