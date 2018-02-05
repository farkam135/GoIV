package com.kamron.pogoiv.scanlogic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {

    public enum Gender {
        F("♀", "f"),
        M("♂", "m"),
        N("", "n");

        private String symbol;
        private String character;

        Gender(@NonNull String symbol, @NonNull String character) {
            this.symbol = symbol;
            this.character = character;
        }

        @Override public String toString() {
            return symbol;
        }

        public String getCharacter() {
            return character;
        }
    }

    /**
     * Evolutions of this Pokemon, sorted in alphabetical order.
     * Try to avoid assumptions that only hold for Gen. I Pokemon: evolutions can have smaller
     * Pokedex number, not be consecutive, etc.
     */
    public final List<Pokemon> evolutions;

    /**
     * Pokemon name for OCR, this is what you saw in PokemonGo app.
     */
    public final String name;

    /**
     * Pokemon name for display, this is what you wanna see in GoIV's result UI.
     */
    private final String displayName;

    public final int number; //index number in resources, pokedex number - 1
    public final int pokedexNumber;
    public final int baseAttack;
    public final int baseDefense;
    public final int baseStamina;
    public final int devoNumber;
    public final int candyEvolutionCost;

    public Pokemon(String name, String displayName, int number, int baseAttack, int baseDefense, int baseStamina,
                   int devoNumber, int candyEvolutionCost) {
        this.name = name;
        this.displayName = displayName;
        this.number = number;
        this.pokedexNumber = number + 1;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devoNumber = devoNumber;
        this.evolutions = new ArrayList<>();
        this.candyEvolutionCost = candyEvolutionCost;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getDistanceCaseInsensitive(String rhs) {
        return Data.levenshteinDistance(name.toLowerCase(), rhs.toLowerCase());
    }

    public int getDistance(String rhs) {
        return Data.levenshteinDistance(name, rhs);
    }

    /**
     * Checks if this Pokemon is the direct evolution of otherPokemon.
     * Example:
     * - Charmeleon.isInNextEvolution(Charmander) returns true
     * - Charizard.isInNextEvolution(Charmander) returns false (it has to be the NEXT evolution)
     *
     * @param otherPokemon the pokemon which is potentially an evolution of this
     * @return true if evolution
     */
    public boolean isNextEvolutionOf(Pokemon otherPokemon) {
        for (Pokemon evolution : otherPokemon.evolutions) {
            if (number == evolution.number) {
                return true;
            }
        }
        return false;
    }

}
