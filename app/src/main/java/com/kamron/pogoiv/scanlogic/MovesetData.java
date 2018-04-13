package com.kamron.pogoiv.scanlogic;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Johan on 2018-02-19.
 * <p>
 * A class which represents the data of a moveset, which is used by the MoveSetFraction to create the moveset list.
 */

public class MovesetData {

    private String quick;
    private String charge;
    private String quickKey;
    private String chargeKey;
    private boolean quickIsLegacy;
    private boolean chargeIsLegacy;
    private double atkScore;
    private double defScore;
    private String quickMoveType;
    private String chargeMoveType;


    /**
     * Create a new Moveset.
     * @param quickKey Unique key identifying the quick move
     * @param chargeKey Unique key identifying the quick move
     * @param quick Localized quick move name
     * @param charge Localized charge move name
     * @param quickMoveType Quick move type
     * @param chargeMoveType Charge move type
     * @param quickIsLegacy Whether the quick move is legacy
     * @param chargeIsLegacy Whether the charge move is legacy
     * @param atkScore A score for the attack power of this moveset
     * @param defScore A score for the defense power of this moveset
     */
    public MovesetData(String quickKey, String chargeKey,
                       String quick, String charge,
                       String quickMoveType, String chargeMoveType,
                       boolean quickIsLegacy, boolean chargeIsLegacy,
                       double atkScore, double defScore) {
        this.quickKey = quickKey;
        this.chargeKey = chargeKey;
        this.quick = quick;
        this.charge = charge;
        this.quickMoveType = quickMoveType;
        this.chargeMoveType = chargeMoveType;
        this.quickIsLegacy = quickIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
    }

    public String getQuickMoveType() {
        return quickMoveType;
    }

    public String getChargeMoveType() {
        return chargeMoveType;
    }

    public String getQuick() {
        return quick;
    }

    public String getCharge() {
        return charge;
    }

    public String getQuickKey() {
        return quickKey;
    }

    public String getChargeKey() {
        return chargeKey;
    }

    public boolean isQuickIsLegacy() {
        return quickIsLegacy;
    }

    public boolean isChargeIsLegacy() {
        return chargeIsLegacy;
    }

    public double getAtkScore() {
        return atkScore;
    }

    public double getDefScore() {
        return defScore;
    }

    public static class AtkComparator implements Comparator<MovesetData> {
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {
            return Double.compare(other.getAtkScore(), movesetData.getAtkScore());
        }
    }

    public static class DefComparator implements Comparator<MovesetData> {
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {
            return Double.compare(other.getDefScore(), movesetData.getDefScore());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovesetData that = (MovesetData) o;
        return Objects.equals(quick, that.quick) && Objects.equals(charge, that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quick, charge);
    }

}
