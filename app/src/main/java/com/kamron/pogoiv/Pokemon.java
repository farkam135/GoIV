package com.kamron.pogoiv;

import com.kamron.pogoiv.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {
    public final List<Pokemon> evolutions; //evolutions sorted collection index, populated after sort
    public final String name;
    public final int number; //index number in resources, pokedex number - 1
    public final int baseAttack;
    public final int baseDefense;
    public final int baseStamina;

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
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

    public int getDistance(CharSequence rhs) {
        if (rhs != null) {
            return Data.levenshteinDistance(name, rhs);
        }
        return 100;
    }


}
