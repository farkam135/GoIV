package com.kamron.pogoiv.scanlogic;

import com.google.common.base.Optional;
import com.kamron.pogoiv.utils.LevelRange;

/**
 * A ScanResult represents the result of an OCR scan.
 * Created by pgiarrusso on 3/9/2016.
 */
//TODO: we might want to make this Parcelable instead of sending the fields one by one?
//But writing the instance by hand would call for unit test.
public class ScanResult {
    private final LevelRange estimatedPokemonLevelRange;
    private final String pokemonName;
    private final String pokemonType;
    private final Pokemon.Gender pokemonGender;
    private final String candyName;
    private final Optional<Integer> pokemonHP;
    private final Optional<Integer> pokemonCP;
    private final Optional<Integer> pokemonCandyAmount;
    private final Optional<Integer> upgradeCandyCost;
    private final Optional<Integer> powerUpStardustCost;
    private final Optional<Integer> powerUpCandyCost;
    private final String uniqueID;

    public ScanResult(LevelRange estimatedPokemonLevel, String pokemonName, String pokemonType, String candyName,
                      Pokemon.Gender pokemonGender,
                      Optional<Integer> pokemonHP, Optional<Integer> pokemonCP,
                      Optional<Integer> pokemonCandyAmount, Optional<Integer> upgradeCandyCost,
                      Optional<Integer> powerUpStardustCost, Optional<Integer> powerUpCandyCost, String uniqueID) {
        this.estimatedPokemonLevelRange = estimatedPokemonLevel;
        this.pokemonName = pokemonName;
        this.pokemonType = pokemonType;
        this.pokemonGender = pokemonGender;
        this.candyName = candyName;
        this.pokemonHP = pokemonHP;
        this.pokemonCP = pokemonCP;
        this.pokemonCandyAmount = pokemonCandyAmount;
        this.upgradeCandyCost = upgradeCandyCost;
        this.powerUpStardustCost = powerUpStardustCost;
        this.powerUpCandyCost = powerUpCandyCost;
        this.uniqueID = uniqueID;
    }

    public LevelRange getEstimatedPokemonLevel() {
        return estimatedPokemonLevelRange;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getPokemonType() {
        return pokemonType;
    }

    public Pokemon.Gender getPokemonGender() {
        return pokemonGender;
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

    public Optional<Integer> getPokemonCandyAmount() {
        return pokemonCandyAmount;
    }

    public Optional<Integer> getPokemonPowerUpStardustCost() {
        return powerUpStardustCost;
    }

    public Optional<Integer> getPokemonPowerUpCandyCost() {
        return powerUpCandyCost;
    }

    public String getPokemonUniqueID() {
        return uniqueID;
    }

    /**
     * Test whether this ScanResult represents a failed scan.
     *
     * @return a boolean representing whether this scan failed.
     */
    public boolean isFailed() {
        //If both scans failed, then probably scrolled down.
        return !pokemonHP.isPresent() && !pokemonCP.isPresent();
    }

    public Optional<Integer> getUpgradeCandyCost() {
        return upgradeCandyCost;
    }
}
