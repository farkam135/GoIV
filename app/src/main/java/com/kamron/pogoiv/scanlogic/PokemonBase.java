package com.kamron.pogoiv.scanlogic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Pokemon base class, it only holds the common data for a pokedex number. It holds also a list of all forms in that
 * pokedex number as Pokemon instances.
 */
public class PokemonBase {

    /**
     * Evolutions of this Pokemon, sorted in alphabetical order.
     * Try to avoid assumptions that only hold for Gen. I Pokemon: evolutions can have smaller
     * Pokedex number, not be consecutive, etc.
     */
    public final List<PokemonBase> evolutions;


    /**
     * Forms of this Pokemon. (Such as Alolan forms.)
     * This list dose not include the normal form.
     * The normal form pokemon is this pokemon itself.
     */
    public final List<Pokemon> forms;

    /**
     * Pokemon name for OCR, this is what you saw in PokemonGo app.
     */
    public final String name;

    /**
     * Pokemon name for display, this is what you wanna see in GoIV's result UI.
     */
    public final String displayName;

    public final int number; //index number in resources, pokedex number - 1
    public final int devoNumber;
    public final int candyEvolutionCost;

    public PokemonBase(String name, String displayName, int number, int devoNumber, int candyEvolutionCost) {
        this.name = name;
        this.displayName = displayName;
        this.number = number;
        this.devoNumber = devoNumber;
        this.evolutions = new ArrayList<>();
        this.forms = new ArrayList<>();
        this.candyEvolutionCost = candyEvolutionCost;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public Pokemon getForm(@NonNull String formName) {
        for (Pokemon form : forms) {
            if (form.formName.equals(formName)) {
                return form;
            }
        }
        return null;
    }
}
