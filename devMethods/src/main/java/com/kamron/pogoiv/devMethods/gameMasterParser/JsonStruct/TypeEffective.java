
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeEffective {

    @SerializedName("attackScalar")
    @Expose
    private List<Double> attackScalar = null;
    @SerializedName("attackType")
    @Expose
    private String attackType;

    public List<Double> getAttackScalar() {
        return attackScalar;
    }

    public void setAttackScalar(List<Double> attackScalar) {
        this.attackScalar = attackScalar;
    }

    public String getAttackType() {
        return attackType;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

}
