
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("baseStamina")
    @Expose
    private Integer baseStamina;
    @SerializedName("baseAttack")
    @Expose
    private Integer baseAttack;
    @SerializedName("baseDefense")
    @Expose
    private Integer baseDefense;

    public Integer getBaseStamina() {
        return baseStamina;
    }

    public void setBaseStamina(Integer baseStamina) {
        this.baseStamina = baseStamina;
    }

    public Integer getBaseAttack() {
        return baseAttack;
    }

    public void setBaseAttack(Integer baseAttack) {
        this.baseAttack = baseAttack;
    }

    public Integer getBaseDefense() {
        return baseDefense;
    }

    public void setBaseDefense(Integer baseDefense) {
        this.baseDefense = baseDefense;
    }

}
