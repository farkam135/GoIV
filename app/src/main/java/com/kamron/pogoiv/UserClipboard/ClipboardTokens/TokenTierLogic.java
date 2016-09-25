package com.kamron.pogoiv.UserClipboard.ClipboardTokens;

/**
 * Created by Johan on 2016-09-24.
 * A class which translates a CP value to a tier string
 */

public class TokenTierLogic {

    /**
     * Get a string representation of a pokemon rating, for example "A" or "B+".
     *
     * @param combatPower the general combatPower to translate to a tier string.
     * @return A string S,A,B,C,D which might have a plus or minus after.
     */
    public String getRating(double combatPower) {
        int ap = 3100; //
        int a = 2650;
        int am = 2550;
        int bp = 2200;
        int b = 2100;
        int bm = 2000;
        int cp = 1900;
        int c = 1800;
        int cm = 1700;
        int dp = 1600;
        int d = 1500;
        int dm = 1400;
        int ep = 1300;
        int e = 1200;
        int em = 1100;
        int fp = 1000;
        int f = 800;
        int fm = 0;

        if (combatPower > ap) {
            return "A+";
        }
        if (combatPower > a) {
            return "A";
        }
        if (combatPower > am) {
            return "A-";
        }
        if (combatPower > bp) {
            return "B+";
        }
        if (combatPower > b) {
            return "B";
        }
        if (combatPower > bm) {
            return "B-";
        }
        if (combatPower > cp) {
            return "C+";
        }
        if (combatPower > c) {
            return "C";
        }
        if (combatPower > cm) {
            return "C-";
        }
        if (combatPower > dp) {
            return "D+";
        }
        if (combatPower > d) {
            return "D";
        }
        if (combatPower > dm) {
            return "D-";
        }
        if (combatPower > ep) {
            return "E+";
        }
        if (combatPower > e) {
            return "E";
        }
        if (combatPower > em) {
            return "E-";
        }
        if (combatPower > fp) {
            return "F+";
        }
        if (combatPower > f) {
            return "F";
        }
        if (combatPower > fm) {
            return "F-";
        }

        return "??";
    }
}
