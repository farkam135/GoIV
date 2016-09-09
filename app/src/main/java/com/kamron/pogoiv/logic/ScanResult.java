package com.kamron.pogoiv.logic;

import com.google.common.base.Optional;

/**
 * A ScanResult represents the result of an OCR scan.
 * Created by pgiarrusso on 3/9/2016.
 */
//TODO: we might want to make this Parcelable instead of sending the fields one by one?
//But writing the instance by hand would call for unit test.
public class ScanResult {
    private double estimatedPokemonLevel;
    private String pokemonName;
    private String candyName;
    private Optional<Integer> pokemonHP;
    private Optional<Integer> pokemonCP;

    public ScanResult(double estimatedPokemonLevel, String pokemonName, String candyName, Optional<Integer> pokemonHP,
                      Optional<Integer> pokemonCP) {
        this.estimatedPokemonLevel = estimatedPokemonLevel;
        this.pokemonName = pokemonName;
        this.candyName = candyName;
        this.pokemonHP = pokemonHP;
        this.pokemonCP = pokemonCP;
    }

    public double getEstimatedPokemonLevel() {
        return estimatedPokemonLevel;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getCandyName() {
        return candyName;
    }

    public Optional<Integer> getPokemonHP() {
        return pokemonHP;
    }

    public Optional<Integer> getPokemonCP() {
        return pokemonCP;
    }

    public boolean isFailed() {
        //XXX replace by proper logic.
        //the default values for a failed scan, if all three fail, then probably scrolled down.
        return candyName.equals("") && !pokemonHP.isPresent() && !pokemonCP.isPresent();
    }
}
