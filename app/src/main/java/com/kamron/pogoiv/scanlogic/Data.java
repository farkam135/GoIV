package com.kamron.pogoiv.scanlogic;

import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;

/**
 * Created by Pascal on 17.08.2016.
 */
public class Data {

    public static final int MINIMUM_TRAINER_LEVEL = 1;
    public static final int MAXIMUM_TRAINER_LEVEL = 40;
    public static final int MINIMUM_POKEMON_LEVEL = 1;
    public static final int MAXIMUM_POKEMON_LEVEL = 40;
    public static final int MAXIMUM_WILD_POKEMON_LEVEL = 35;

    private static final float[] CpM = {
            0.0939999967813492f, 0.135137432089339f, 0.166397869586945f, 0.192650913155325f, 0.215732470154762f,
            0.236572651424822f, 0.255720049142838f, 0.273530372106572f, 0.290249884128571f, 0.306057381389863f,
            0.321087598800659f, 0.335445031996451f, 0.349212676286697f, 0.362457736609939f, 0.375235587358475f,
            0.387592407713878f, 0.399567276239395f, 0.4111935532161f, 0.422500014305115f, 0.432926420512509f,
            0.443107545375824f, 0.453059948165049f, 0.46279838681221f, 0.472336085311278f, 0.481684952974319f,
            0.490855807179549f, 0.499858438968658f, 0.5087017489616f, 0.517393946647644f, 0.525942516110322f,
            0.534354329109192f, 0.542635753803599f, 0.550792694091797f, 0.558830584490385f, 0.566754519939423f,
            0.57456912814537f, 0.582278907299042f, 0.589887907888945f, 0.597400009632111f, 0.604823648665171f,
            0.61215728521347f, 0.619404107958234f, 0.626567125320435f, 0.633649178748576f, 0.6406529545784f,
            0.647580971386554f, 0.654435634613037f, 0.661219265805859f, 0.667934000492096f, 0.674581885647492f,
            0.681164920330048f, 0.687684901255373f, 0.694143652915955f, 0.700542901033063f, 0.706884205341339f,
            0.713169074873823f, 0.719399094581604f, 0.725575586915154f, 0.731700003147125f, 0.734741038550429f,
            0.737769484519958f, 0.740785579737136f, 0.743789434432983f, 0.746781197247765f, 0.749761044979095f,
            0.752729099732281f, 0.75568550825119f, 0.758630370209851f, 0.761563837528229f, 0.76448604959218f,
            0.767397165298462f, 0.770297293677362f, 0.773186504840851f, 0.776064947064992f, 0.778932750225067f,
            0.781790050767666f, 0.784636974334717f, 0.787473608513275f, 0.790300011634827f};

    public static final int[] POWER_UP_CANDY_COSTS = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 8, 8,
            8, 8, 10, 10, 10, 10, 12, 12, 12, 12, 15, 15, 15, 15 };
    public static int arcInitX;
    public static int arcInitY;
    public static int[] arcX;
    public static int[] arcY;

    /**
     * setupArcPoints
     * Sets up the x,y coordinates of the arc using the trainer level, stores it in Data.arcX/arcY
     */
    public static void setupArcPoints(ScanPoint arcInit, int arcRadius, int trainerLevel) {
        arcInitX = arcInit.xCoord;
        arcInitY = arcInit.yCoord;
        /*
         * Pokemon levels go from 1 to trainerLevel + 2, in increments of 0.5.
         * Here we use levelIdx for levels that are doubled and shifted by - 2; after this adjustment,
         * the level can be used to index CpM, arcX and arcY.
         */
        int maxPokeLevelIndex = (trainerLevelToMaxPokeLevelIndex(trainerLevel));
        arcX = new int[maxPokeLevelIndex + 1]; //We access entries [0..maxPokeLevelIndex], hence + 1.
        arcY = new int[maxPokeLevelIndex + 1];

        double baseCpM = CpM[0];


        //amount of possible levels: level*2 + 3
        double maxPokeCpMDelta = CpM[Math.min(maxPokeLevelIndex, CpM.length - 1)] - baseCpM;

        //pokeLevelIdx <= maxPokeLevelIndex ensures we never overflow CpM/arc/arcY
        for (int pokeLevelIdx = 0; pokeLevelIdx <= maxPokeLevelIndex; pokeLevelIdx++) {
            double pokeCurrCpMDelta = CpM[pokeLevelIdx] - baseCpM;
            double arcRatio = pokeCurrCpMDelta / maxPokeCpMDelta;
            double angleInRadians = (arcRatio + 1) * Math.PI;

            arcX[pokeLevelIdx] = (int) Math.round(arcInit.xCoord + (arcRadius * Math.cos(angleInRadians)));
            arcY[pokeLevelIdx] = (int) Math.round(arcInit.yCoord + (arcRadius * Math.sin(angleInRadians)));
        }
    }

    /**
     * Convert a pokemon/trainer level to a <em>level index</em> (<code>levelIdx</code> in code).
     * The mapping is invertible, but level indexes can be used to index an array (like Data.CpM), or seekbars.
     * <p/>
     * Pokemon levels go from 1 to trainerLevelToMaxPokeLevel(trainerLevel), in increments of 0.5.
     * Level indexes go from 0 to trainerLevelToMaxPokeLevelIndex(trainerLevel) in increments of 1.
     * This method adjusts a level to a <em>level index</em> (<code>levelIdx</code>), by doubling it
     * and subtracting 2.
     */
    public static int maxPokeLevelToIndex(double level) {

        return (int) ((level - 1) * 2);
    }

    /**
     * Convert a <em>level index</em> back to a level. Inverse of maxPokeLevelToIndex, see explanations
     * there for rationale.
     */
    public static double levelIdxToLevel(int levelIdx) {
        return levelIdx * 0.5 + 1;
    }

    /**
     * Return CpM (CP Multiplier) for a given pokemon level. Levels are described as documented for
     * Data.maxPokeLevelToIndex.
     *
     * @param level The desired level.
     * @return Associated CpM.
     */
    public static double getLevelCpM(double level) {
        return CpM[maxPokeLevelToIndex(level)];
    }

    /**
     * Maximum pokemon level for a trainer, from the trainer level. This is 2 levels above trainer level.
     * It used to be 1.5, but was changed around december 2017.
     */
    public static double trainerLevelToMaxPokeLevel(int trainerLevel) {
        return Math.min(trainerLevel + 2, 40);
    }

    /*
     * Pokemon levels go from 1 to trainerLevel + 2, in increments of 0.5.
     * Here we use levelIdx for levels that are doubled and shifted by - 2; after this adjustment,
     * the level can be used to index CpM, arcX and arcY.
     */
    public static int trainerLevelToMaxPokeLevelIndex(int trainerLevel) {
        // This is Math.min(2 * trainerLevel + 1, 79).
        return maxPokeLevelToIndex(trainerLevelToMaxPokeLevel(trainerLevel));
    }

    public static boolean isValidPowerUpCandyCost(int powerUpCandyCost) {
        for (int currentCost : POWER_UP_CANDY_COSTS) {
            if (currentCost == powerUpCandyCost) {
                return true;
            } else if (currentCost > powerUpCandyCost) {
                break; // Costs are ascending ordered. There won't be a cost equal to the input in the array.
            }
        }
        return false;
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
