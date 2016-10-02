package com.kamron.pogoiv.logic;

import android.graphics.Point;

/**
 * Created by Pascal on 17.08.2016.
 */
public class Data {

    private static final double[] CpM = {
            0.0939999967813492, 0.135137432089339, 0.166397869586945, 0.192650913155325, 0.215732470154762,
            0.236572651424822, 0.255720049142838, 0.273530372106572, 0.290249884128571, 0.306057381389863,
            0.321087598800659, 0.335445031996451, 0.349212676286697, 0.362457736609939, 0.375235587358475,
            0.387592407713878, 0.399567276239395, 0.4111935532161, 0.422500014305115, 0.432926420512509,
            0.443107545375824, 0.453059948165049, 0.46279838681221, 0.472336085311278, 0.481684952974319,
            0.490855807179549, 0.499858438968658, 0.5087017489616, 0.517393946647644, 0.525942516110322,
            0.534354329109192, 0.542635753803599, 0.550792694091797, 0.558830584490385, 0.566754519939423,
            0.57456912814537, 0.582278907299042, 0.589887907888945, 0.597400009632111, 0.604823648665171,
            0.61215728521347, 0.619404107958234, 0.626567125320435, 0.633649178748576, 0.6406529545784,
            0.647580971386554, 0.654435634613037, 0.661219265805859, 0.667934000492096, 0.674581885647492,
            0.681164920330048, 0.687684901255373, 0.694143652915955, 0.700542901033063, 0.706884205341339,
            0.713169074873823, 0.719399094581604, 0.725575586915154, 0.731700003147125, 0.734741038550429,
            0.737769484519958, 0.740785579737136, 0.743789434432983, 0.746781197247765, 0.749761044979095,
            0.752729099732281, 0.75568550825119, 0.758630370209851, 0.761563837528229, 0.76448604959218,
            0.767397165298462, 0.770297293677362, 0.773186504840851, 0.776064947064992, 0.778932750225067,
            0.781790050767666, 0.784636974334717, 0.787473608513275, 0.790300011634827};

    public static int[] arcX;
    public static int[] arcY;

    /**
     * setupArcPoints
     * Sets up the x,y coordinates of the arc using the trainer level, stores it in Data.arcX/arcY
     */
    public static void setupArcPoints(Point arcInit, int arcRadius, int trainerLevel) {
        /*
         * Pokemon levels go from 1 to trainerLevel + 1.5, in increments of 0.5.
         * Here we use levelIdx for levels that are doubled and shifted by - 2; after this adjustment,
         * the level can be used to index CpM, arcX and arcY.
         */
        int maxPokeLevelIdx = trainerLevelToMaxPokeLevelIdx(trainerLevel);
        arcX = new int[maxPokeLevelIdx + 1]; //We access entries [0..maxPokeLevelIdx], hence + 1.
        arcY = new int[maxPokeLevelIdx + 1];

        double baseCpM = CpM[0];
        //TODO: debug this formula when we get to the end of CpM (that is, levels 39/40).
        double maxPokeCpMDelta = CpM[Math.min(maxPokeLevelIdx + 1, CpM.length - 1)] - baseCpM;

        //pokeLevelIdx <= maxPokeLevelIdx ensures we never overflow CpM/arc/arcY.
        for (int pokeLevelIdx = 0; pokeLevelIdx <= maxPokeLevelIdx; pokeLevelIdx++) {
            double pokeCurrCpMDelta = CpM[pokeLevelIdx] - baseCpM;
            double arcRatio = pokeCurrCpMDelta / maxPokeCpMDelta;
            double angleInRadians = (arcRatio + 1) * Math.PI;

            arcX[pokeLevelIdx] = (int) (arcInit.x + (arcRadius * Math.cos(angleInRadians)));
            arcY[pokeLevelIdx] = (int) (arcInit.y + (arcRadius * Math.sin(angleInRadians)));
        }
    }

    /**
     * Convert a pokemon/trainer level to a <em>level index</em> (<code>levelIdx</code> in code).
     * The mapping is invertible, but level indexes can be used to index an array (like Data.CpM), or seekbars.
     * <p/>
     * Pokemon levels go from 1 to trainerLevelToMaxPokeLevel(trainerLevel), in increments of 0.5.
     * Level indexes go from 0 to trainerLevelToMaxPokeLevelIdx(trainerLevel) in increments of 1.
     * This method adjusts a level to a <em>level index</em> (<code>levelIdx</code>), by doubling it
     * and subtracting 2.
     */
    public static int levelToLevelIdx(double level) {
        return (int) ((level - 1) * 2);
    }

    /**
     * Convert a <em>level index</em> back to a level. Inverse of levelToLevelIdx, see explanations
     * there for rationale.
     */
    public static double levelIdxToLevel(int levelIdx) {
        return levelIdx * 0.5 + 1;
    }

    /**
     * Return CpM (CP Multiplier) for a given pokemon level. Levels are described as documented for
     * Data.levelToLevelIdx.
     *
     * @param level The desired level.
     * @return Associated CpM.
     */
    public static double getLevelCpM(double level) {
        return CpM[levelToLevelIdx(level)];
    }

    /**
     * Maximum pokemon level for a trainer, from the trainer level. That's usually trainerLevel + 1.5, but
     * the maximum is 40 (http://pokemongo.gamepress.gg/power-up-costs).
     */
    public static double trainerLevelToMaxPokeLevel(int trainerLevel) {
        return Math.min(trainerLevel + 1.5, 40);
    }

    /*
     * Pokemon levels go from 1 to trainerLevel + 1.5, in increments of 0.5.
     * Here we use levelIdx for levels that are doubled and shifted by - 2; after this adjustment,
     * the level can be used to index CpM, arcX and arcY.
     */
    public static int trainerLevelToMaxPokeLevelIdx(int trainerLevel) {
        // This is Math.min(2 * trainerLevel + 1, 78).
        return levelToLevelIdx(trainerLevelToMaxPokeLevel(trainerLevel));
    }

    // should be pretty fast https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
    public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        if (rhs == null) {
            return Integer.MAX_VALUE;
        }

        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}
