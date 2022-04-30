package com.kamron.pogoiv.scanlogic;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Johan on 2018-02-19.
 *
 * A class which represents the data of a moveset, which is used by the MoveSetFraction to create the moveset list.
 */

public class MovesetData {
    private String fast;
    private String charge;
    private String fastKey;
    private String chargeKey;
    private boolean fastIsLegacy;
    private boolean chargeIsLegacy;
    private Double atkScore;
    private Double defScore;
    private String fastMoveType;
    private String chargeMoveType;

    /**
     * Create a new Moveset.
     *
     * @param fast   Localized fast move name
     * @param charge Localized charge move name
     */
    public MovesetData(String fast, String charge) {
        this.fast = fast;
        this.charge = charge;
    }

    public MovesetData(String quick,
                       String charge,
                       boolean quickIsLegacy,
                       boolean chargeIsLegacy,
                       Double atkScore,
                       Double defScore,
                       String chargeMoveType,
                       String quickMoveType) {

        this.fast = quick;
        this.charge = charge;
        this.fastMoveType = quickMoveType;
        this.chargeMoveType = chargeMoveType;
        this.fastIsLegacy = quickIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
    }

    /**
     * Create a new Moveset.
     *
     * @param fastKey        Unique key identifying the fast move
     * @param chargeKey      Unique key identifying the charge move
     * @param fast           Localized fast move name
     * @param charge         Localized charge move name
     * @param fastMoveType   Fast move type
     * @param chargeMoveType Charge move type
     * @param fastIsLegacy   Whether the fast move is legacy
     * @param chargeIsLegacy Whether the charge move is legacy
     * @param atkScore       A score for the attack power of this moveset
     * @param defScore       A score for the defense power of this moveset
     */
    public MovesetData(String fastKey, String chargeKey,
                       String fast, String charge,
                       String fastMoveType, String chargeMoveType,
                       boolean fastIsLegacy, boolean chargeIsLegacy,
                       Double atkScore, Double defScore) {
        this.fastKey = fastKey;
        this.chargeKey = chargeKey;
        this.fast = fast;
        this.charge = charge;
        this.fastMoveType = fastMoveType;
        this.chargeMoveType = chargeMoveType;
        this.fastIsLegacy = fastIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
    }

    public String getFastMoveType() {
        return fastMoveType;
    }

    public String getChargeMoveType() {
        return chargeMoveType;
    }

    public String getFast() {
        return fast;
    }

    public String getCharge() {
        return charge;
    }

    public String getFastKey() {
        return fastKey;
    }

    public String getChargeKey() {
        return chargeKey;
    }

    public boolean isFastIsLegacy() {
        return fastIsLegacy;
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
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {

            //The worst moves dont get a score, so they should always be at the end of the list.
            if (movesetData.getAtkScore() == null) {
                return 1;
            }
            if (other.getAtkScore() == null) {
                return -1;
            }
            return Double.compare(other.getAtkScore(), movesetData.getAtkScore());
        }
    }

    public static class DefComparator implements Comparator<MovesetData> {
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {
            //The worst moves dont get a score, so they should always be at the end of the list.
            if (movesetData.getDefScore() == null) {
                return 1;
            }
            if (other.getDefScore() == null) {
                return -1;
            }
            return Double.compare(other.getDefScore(), movesetData.getDefScore());
        }
    }

    public static class Key implements Comparable<Key> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovesetData that = (MovesetData) o;
        return Objects.equals(fast, that.fast) && Objects.equals(charge, that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fast, charge);
    }

}
