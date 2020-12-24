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

    public Pokemon getForm(@NonNull Pokemon otherForm) {
        // If either of them have only one form, but the other doesn't, it cannot match the forms with the String
        // version, because the "normal" form uses different names when there are multiple and when there aren't.
        // In that case return the first one, which can be done in any case if this base has multiple forms (because
        // then we know that the other form is the single one). But if the other form has multiple forms it also needs
        // to verify that the other form is actually the first one.

        final int pokeListSize = PokeInfoCalculator.getInstance().getPokedex().size();

        if (number == 51) { // #52 Meowth
            // check #863 Perrserker with its index number in pokemons.xml
            if (otherForm.number == pokeListSize - PokeInfoCalculator.PERRSERKER_INDEX_OFFSET) {
                // return Galarian forms
                return forms.get(2);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == 52) { // #53 Persian
            return getForm(otherForm.formName);
        }
        if (number == 82) { // #83 Farfetch'd
            // check #865 Sirfetch'd with its index number in pokemons.xml
            if (otherForm.number == pokeListSize - PokeInfoCalculator.SIRFETCHD_INDEX_OFFSET) {
                // return Galarian forms
                return forms.get(1);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == 262 || number == 263) { // #263 ZIGZAGOON or #264 LINOONE
            // check #862 OBSTAGOON with its index number in pokemons.xml
            if (otherForm.number == pokeListSize - PokeInfoCalculator.OBSTAGOON_INDEX_OFFSET) {
                // return Galarian forms
                return forms.get(1);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == 553) { // #554 Darumaka
            // check #555 Darmanitan
            if (otherForm.number == 554 && otherForm.base.forms.get(0) == otherForm) {
                // return Normal Form
                return forms.get(0);
            }
            // check #555 Darmanitan Galarian
            if (otherForm.number == 554 && otherForm.base.forms.get(2) == otherForm) {
                // return Galarian Form
                return forms.get(1);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == 554) { // #555 Darmanitan
            // check #554 Darumaka Normal
            if (otherForm.number == 553 && otherForm.base.forms.get(0) == otherForm) {
                // return Standard Form
                return forms.get(0);
            }
            // check #554 Darumaka Galarian
            if (otherForm.number == 553 && otherForm.base.forms.get(1) == otherForm) {
                // return Galarian Standard Form
                return forms.get(2);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == pokeListSize
                - PokeInfoCalculator.OBSTAGOON_INDEX_OFFSET) { // #862 OBSTAGOON index number in pokemons.xml
            // check #263 ZIGZAGOON Galarian
            if (otherForm.number == 262 && otherForm.base.forms.get(1) == otherForm) {
                return forms.get(0);
            }
            // check #264 LINOONE Galarian
            if (otherForm.number == 263 && otherForm.base.forms.get(1) == otherForm) {
                return forms.get(0);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == pokeListSize
                - PokeInfoCalculator.PERRSERKER_INDEX_OFFSET) { // #863 Perrserker index number in pokemons.xml
            // check #52 Meowth Galarian
            if (otherForm.number == 51 && otherForm.base.forms.get(2) == otherForm) {
                return forms.get(0);
            } else {
                return getForm(otherForm.formName);
            }
        }
        if (number == pokeListSize
                - PokeInfoCalculator.SIRFETCHD_INDEX_OFFSET) { // #865 Sirfetch'd index number in pokemons.xml
            // check #83 Farfetch'd Galarian
            if (otherForm.number == 82 && otherForm.base.forms.get(1) == otherForm) {
                return forms.get(0);
            } else {
                return getForm(otherForm.formName);
            }
        }

        if ((otherForm.base.forms.size() == 1 && forms.size() > 1)
                || (forms.size() == 1 && otherForm.base.forms.size() > 1 && otherForm.base.forms.get(0) == otherForm)) {
            return forms.get(0);
        } else {
            return getForm(otherForm.formName);
        }

    }
}
