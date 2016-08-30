package com.kamron.pogoiv.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {
    /**
     * Evolutions of this Pokemon, sorted in alphabetical order.
     * Try to avoid assumptions that only hold for Gen. I Pokemon: evolutions can have smaller
     * Pokedex number, not be consecutive, etc.
     */
    public final List<Pokemon> evolutions;
    public final String name;
    public final int number; //index number in resources, pokedex number - 1
    public final int baseAttack;
    public final int baseDefense;
    public final int baseStamina;
    public final int devoNumber;
    public final int candyEvolutionCost;

    public Pokemon(String name, int number, int baseAttack, int baseDefense, int baseStamina, int devoNumber, int candyEvolutionCost) {
        this.name = name;
        this.number = number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devoNumber = devoNumber;
        this.evolutions = new ArrayList<>();
        this.candyEvolutionCost = candyEvolutionCost;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getDistanceCaseInsensitive(String rhs) {
        return Data.levenshteinDistance(name.toLowerCase(), rhs.toLowerCase());
    }

    public int getDistance(String rhs) {
        return Data.levenshteinDistance(name, rhs);
    }

    /**
     * Checks if a pokemon is the same pokemon type as in a pokemons next Evolution
     * Example:
     * - charmander.isInNextEvolution(charmeleon) returns true
     * - charmeleon.isInNextEvolution(charisard) returns false (it has to be the NEXT evolution)
     *
     * @param otherPokemon the pokemon which is potentially an evolution of this
     * @return true if evolution
     */
    public boolean isInNextEvolution(Pokemon otherPokemon) {
        int otherPokemonNumber = otherPokemon.number;

        for (Pokemon evolution : evolutions) {
            if (otherPokemonNumber == evolution.number) return true;
        }
        return false;
    }

}
