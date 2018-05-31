package com.kamron.pogoiv.scanlogic;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Johan on 2018-02-19.
 * <p>
 * A class which represents the data of a moveset, which is used by the MoveSetFraction to create the moveset list.
 */

public class MovesetData {

    private String quick;
    private String charge;
    private boolean quickIsLegacy;
    private boolean chargeIsLegacy;
    private Double atkScore;
    private Double defScore;
    private String quickMoveType;
    private String chargeMoveType;


    public MovesetData(String quick, String charge, boolean quickIsLegacy, boolean chargeIsLegacy, Double atkScore,
                       Double defScore,
                       String chargeMoveType, String quickMoveType) {
        this.quick = quick;
        this.charge = charge;
        this.quickIsLegacy = quickIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
        this.chargeMoveType = chargeMoveType;
        this.quickMoveType = quickMoveType;


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

    public boolean isQuickIsLegacy() {
        return quickIsLegacy;
    }

    public boolean isChargeIsLegacy() {
        return chargeIsLegacy;
    }

    public Double getAtkScore() {
        return atkScore;
    }

    public Double getDefScore() {
        return defScore;
    }

    public static class AtkComparator implements Comparator<MovesetData> {

        @Override public int compare(MovesetData movesetData, MovesetData other) {
            return Double.compare(other.getAtkScore(), movesetData.getAtkScore());
        }
    }

    public static class DefComparator implements Comparator<MovesetData> {

        @Override public int compare(MovesetData movesetData, MovesetData other) {
            return Double.compare(other.getDefScore(), movesetData.getDefScore());
        }
    }

    public static class MoveComparator implements Comparator<MovesetData> {

        @Override public int compare(MovesetData movesetData, MovesetData other) {
            int retval = other.getQuick().compareTo(movesetData.getQuick());
            if (retval == 0) {
                retval = other.getCharge().compareTo(movesetData.getCharge());
            }
            return retval;
        }
    }
    public static class Key implements Comparable<Key>{
        private final String quick;
        private final String charge;

        public Key(String quick, String charge) {
            this.quick = quick;
            this.charge = charge;
        }

        public String getQuick() {
            return quick;
        }

        public String getCharge() {
            return charge;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            if (quick != null ? !quick.equals(key.quick) : key.quick != null) {
                return false;
            }
            return charge != null ? charge.equals(key.charge) : key.charge == null;
        }

        @Override public int hashCode() {
            int result = quick != null ? quick.hashCode() : 0;
            result = 31 * result + (charge != null ? charge.hashCode() : 0);
            return result;
        }

        @Override public int compareTo(@NonNull Key other) {
            int retval = this.getQuick().compareTo(other.getQuick());
            if (retval == 0) {
                retval = this.getCharge().compareTo(other.getCharge());
            }
            return retval;
        }
    }


}
