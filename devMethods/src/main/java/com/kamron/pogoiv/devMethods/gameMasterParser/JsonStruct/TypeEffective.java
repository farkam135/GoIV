package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class TypeEffective {
    @Expose
    private List<Double> attackScalar = null;
    @Expose
    private String attackType;

    public List<Double> getAttackScalar() { return attackScalar; }

    public void setAttackScalar(List<Double> attackScalar) { this.attackScalar = attackScalar; }

    public String getAttackType() { return attackType; }

    public void setAttackType(String attackType) { this.attackType = attackType; }
}
