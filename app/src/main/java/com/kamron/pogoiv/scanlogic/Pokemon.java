package com.kamron.pogoiv.scanlogic;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Pokemon class for each form, it has a reference to the base corresponding to the number in the Pokedex.
 *
 * Created by Kamron on 7/30/2016.
 */

public class Pokemon {

    public enum Gender {
        F("♀", "F"),
        M("♂", "M"),
        N("", "N");

        private String symbol;
        private String letter;

        Gender(@NonNull String symbol, @NonNull String letter) {
            this.symbol = symbol;
            this.letter = letter;
        }

        @Override public String toString() {
            return letter;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getLetter() {
            return letter;
        }
    }

    public enum Type {
        NORMAL,
        FIRE,
        WATER,
        GRASS,
        ELECTRIC,
        ICE,
        FIGHTING,
        POISON,
        GROUND,
        FLYING,
        PSYCHIC,
        BUG,
        ROCK,
        GHOST,
        DRAGON,
        DARK,
        STEEL,
        FAIRY,
    }

    /**
     * Pokemon name for OCR, this is what you saw in PokemonGo app.
     */
    public final String name;

    /**
     * Pokemon name for display, this is what you wanna see in GoIV's result UI.
     */
    private final String displayName;

    public final PokemonBase base;
    public final String formName;
    // Copy of the value in the base class
    public final int number; //index number in resources, pokedex number - 1
    public final int baseAttack;
    public final int baseDefense;
    public final int baseStamina;
    // Copy of the value in the base class
    public final int devoNumber;
    // Copy of the value in the base class
    public final int candyNameNumber;
    // Copy of the value in the base class
    public final int candyEvolutionCost;

    public Pokemon(PokemonBase base, @NonNull String formName, int baseAttack, int baseDefense, int baseStamina) {
        this.base = base;
        this.formName = formName;
        if (!formName.isEmpty()) {
            formName = " - " + formName;
        }
        this.name = base.name + formName;
        this.displayName = base.displayName + formName;
        this.number = base.number;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseStamina = baseStamina;
        this.devoNumber = base.devoNumber;
        this.candyNameNumber = base.candyNameNumber;
        this.candyEvolutionCost = base.candyEvolutionCost;
    }

    @Override
    public String toString() {
        return displayName;
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
        for (Pokemon evolution : otherPokemon.getEvolutions()) {
            if (number == evolution.number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evolutions of this Pokemon, sorted in alphabetical order.
     * Try to avoid assumptions that only hold for Gen. I Pokemon: evolutions can have smaller
     * Pokedex number, not be consecutive, etc.
     */
    public List<Pokemon> getEvolutions() {
        ArrayList<Pokemon> formEvolutions = new ArrayList<>();
        for (PokemonBase evolvedBase : base.evolutions) {
            Pokemon evolvedForm = evolvedBase.getForm(this);
            if (evolvedForm != null) {
                formEvolutions.add(evolvedForm);
            }
        }
        return formEvolutions;
    }
}
