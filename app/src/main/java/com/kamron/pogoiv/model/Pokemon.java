package com.kamron.pogoiv.model;

import com.kamron.pogoiv.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {
    public final List<Integer> evolutions; //evolutions sorted collection index, populated after sort
    public final String name;
    public final int number; //index number in resources, pokedex number - 1
    public final int baseAttack;
    public final int baseDefense;
    public final int baseStamina;
    public final int devolNumber; //index number in resources of devolution, pokedex number - 1

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina, int devolNumber) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devolNumber = devolNumber;
        this.evolutions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        int difference = Data.levenshteinDistance((CharSequence) o, name);
        return difference < 2;
    }

    public int getSimilarity(CharSequence rhs) {
        if (rhs != null) {
            return Data.levenshteinDistance(name, rhs);
        }
        return 100;
    }


}
