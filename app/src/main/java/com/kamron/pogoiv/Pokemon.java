package com.kamron.pogoiv;

import java.util.ArrayList;

/**
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {
    public String name;
    public int number; //indexnumber in ressources, pokedexnumber - 1
    public int baseAttack;
    public int baseDefense;
    public int baseStamina;
    public int devolNumber; //indexnumber in ressources of devolution, pokedexnumber - 1
    public ArrayList<Integer> evolutions; //evolutions sorted collection index, populated after sort

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina, int devolNumber) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devolNumber = devolNumber;
        this.evolutions = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        int difference = levenshteinDistance((CharSequence) o, name);
        return difference < 2;
    }

    public int getSimilarity(CharSequence rhs) {
        if (rhs != null) {
            return levenshteinDistance(name, rhs);
        }
        return 100;
    }

    // should be pretty fast https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
    private int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

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
