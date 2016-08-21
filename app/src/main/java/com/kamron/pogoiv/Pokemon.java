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
    public ArrayList<Pokemon> evolutions; //evolutions sorted collection

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
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

    /**
     * Checks if a pokemon is the same pokemon type as in a pokemons next Evolution
     * Example -  charmander.isInNextEvolution(charmeleon) returns true
     *              charmeleon.isInNextEvolution(charisard) returns fale (it has to be the NEXT evolution)
     * @param otherPokemon the pokemon which is potentially an evolution of this
     * @return true if evolution
     */
    public boolean isInNextEvolution(Pokemon otherPokemon){
        int otherPokemonNumber = otherPokemon.number;

        for (Pokemon evolution:evolutions){
            if (otherPokemonNumber == evolution.number) return true;
        }
        return false;
    }

}
