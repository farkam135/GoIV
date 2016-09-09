package com.kamron.pogoiv.logic;

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
    private int pokemonHP;
    private int pokemonCP;
    private int upgradeCandyCost;

    public ScanResult(double estimatedPokemonLevel, String pokemonName, String candyName, int pokemonHP,
                      int pokemonCP, int upgradeCandyCost) {
        this.estimatedPokemonLevel = estimatedPokemonLevel;
        this.pokemonName = pokemonName;
        this.candyName = candyName;
        this.pokemonHP = pokemonHP;
        this.pokemonCP = pokemonCP;
        this.upgradeCandyCost = upgradeCandyCost;
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

    public int getPokemonHP() {
        return pokemonHP;
    }

    public int getPokemonCP() {
        return pokemonCP;
    }

    public int getUpgradeCandyCost(){ return upgradeCandyCost; }

    public boolean isFailed() {
        //XXX replace by proper logic.
        //the default values for a failed scan, if all three fail, then probably scrolled down.
        return candyName.equals("") && pokemonHP == 10 && pokemonCP == 10;
    }
}
