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
    public ArrayList<Pokemon> evolutions; //evolutions sorted collection index, populated after sort

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina, int devolNumber) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devolNumber = devolNumber;
        this.evolutions = new ArrayList<Pokemon>();
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
