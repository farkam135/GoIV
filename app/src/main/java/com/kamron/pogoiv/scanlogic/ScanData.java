package com.kamron.pogoiv.scanlogic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Optional;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A ScanData represents the result of an OCR scan.
 * Created by pgiarrusso on 3/9/2016.
 */
//TODO: we might want to make this Parcelable instead of sending the fields one by one?
//But writing the instance by hand would call for unit test.
public class ScanData {
    private LevelRange estimatedPokemonLevelRange;
    private String pokemonName;
    private String normalizedPokemonName;
    private final String pokemonType;
    private final String normalizedPokemonType;
    private final Pokemon.Gender pokemonGender;
    private final List<String> candyNames;
    private final List<String> normalizedCandyNames;
    private Optional<Integer> pokemonHP;
    private Optional<Integer> pokemonCP;
    private Optional<Integer> pokemonCandyAmount;
    private final Optional<Integer> evolutionCandyCost;
    private final Optional<Integer> powerUpStardustCost;
    private final Optional<Integer> powerUpCandyCost;
    private final String moveFast;
    private final String moveCharge;
    private final boolean isLucky;
    private final String uniqueID;
    private Pokemon pokemon = null;

    public ScanData(LevelRange estimatedPokemonLevel, String pokemonName, String pokemonType, List<String> candyNames,
                    Pokemon.Gender pokemonGender, Optional<Integer> pokemonHP, Optional<Integer> pokemonCP,
                    Optional<Integer> pokemonCandyAmount, Optional<Integer> evolutionCandyCost,
                    Optional<Integer> powerUpStardustCost, Optional<Integer> powerUpCandyCost,
                    String moveFast, String moveCharge, boolean isLucky, String uniqueID) {
        this.estimatedPokemonLevelRange = estimatedPokemonLevel;
        this.pokemonName = pokemonName;
        this.normalizedPokemonName = StringUtils.normalize(pokemonName);
        this.pokemonType = pokemonType;
        this.normalizedPokemonType = StringUtils.normalize(pokemonType);
        this.pokemonGender = pokemonGender;
        if (candyNames == null) {
            this.candyNames = new ArrayList();
        } else {
            this.candyNames = candyNames;
        }
        this.normalizedCandyNames = new ArrayList();
        for (String candyName : this.candyNames) {
            this.normalizedCandyNames.add(StringUtils.normalize(candyName));
        }
        this.pokemonHP = pokemonHP;
        this.pokemonCP = pokemonCP;
        this.pokemonCandyAmount = pokemonCandyAmount;
        this.evolutionCandyCost = evolutionCandyCost;
        this.powerUpStardustCost = powerUpStardustCost;
        this.powerUpCandyCost = powerUpCandyCost;
        this.moveFast = moveFast;
        this.moveCharge = moveCharge;
        this.isLucky = isLucky;
        this.uniqueID = uniqueID;
    }

    @Override public String toString() {
        // temporary format for checking OCR result and debugging IV calculations.
        return String.format("ScanData\n"
                        + "name:%s\n"
                        + "type:%s\n"
                        + "gender:%s\n"
                        + "cp:%d\n"
                        + "hp:%d\n"
                        + "lucky:%B\n\n"

                        + "candy_name:%s or %s\n"
                        + "candy_amount:%d\n"
                        + "evolution_candy:%d\n"
                        + "powerup_candy:%d\n"
                        + "powerup_stardust:%d\n\n"

                        + "move_fast:%s\n"
                        + "move_charge:%s\n",
                pokemonName,
                pokemonType,
                pokemonGender,
                pokemonCP.or(-1),
                pokemonHP.or(-1),
                isLucky,

                candyNames.get(0),
                candyNames.get(1),
                pokemonCandyAmount.or(-1),
                evolutionCandyCost.or(-1),
                powerUpCandyCost.or(-1),
                powerUpStardustCost.or(-1),

                moveFast,
                moveCharge);
    }

    public LevelRange getEstimatedPokemonLevel() {
        return estimatedPokemonLevelRange;
    }

    public void setEstimatedPokemonLevelRange(@NonNull LevelRange levelRange) {
        this.estimatedPokemonLevelRange = levelRange;
    }

    public String getPokemonName() { return pokemonName; }

    public String getNormalizedPokemonName() {
        return normalizedPokemonName;
    }

    public void setPokemonName(@NonNull String pokemonName) {
        this.pokemonName = pokemonName;
        this.normalizedPokemonName = StringUtils.normalize(pokemonName);
    }

    public String getPokemonType() {
        return pokemonType;
    }

    public String getNormalizedPokemonType() {
        return normalizedPokemonType;
    }

    public Pokemon.Gender getPokemonGender() {
        return pokemonGender;
    }

    public List<String> getCandyNames() { return candyNames; }

    public List<String> getNormalizedCandyNames() { return normalizedCandyNames; }

    public Optional<Integer> getPokemonHP() {
        return pokemonHP;
    }

    public void setPokemonHP(int hp) {
        pokemonHP = Optional.of(hp);
    }

    public Optional<Integer> getPokemonCP() {
        return pokemonCP;
    }

    public void setPokemonCP(int cp) {
        pokemonCP = Optional.of(cp);
    }

    public Optional<Integer> getPokemonCandyAmount() {
        return pokemonCandyAmount;
    }

    public void setPokemonCandyAmount(int candyAmount) {
        pokemonCandyAmount = Optional.of(candyAmount);
    }

    public Optional<Integer> getPokemonPowerUpStardustCost() {
        return powerUpStardustCost;
    }

    public Optional<Integer> getPokemonPowerUpCandyCost() {
        return powerUpCandyCost;
    }

    public @Nullable String getFastMove() {
        return moveFast;
    }

    public @Nullable String getChargeMove() {
        return moveCharge;
    }

    public String getPokemonUniqueID() {
        return uniqueID;
    }

    public boolean getIsLucky() { return isLucky; }

    /**
     * Test whether this ScanData represents a failed scan.
     *
     * @return a boolean representing whether this scan failed.
     */
    public boolean isFailed() {
        //If both scans failed, then probably scrolled down.
        return !pokemonHP.isPresent() && !pokemonCP.isPresent();
    }

    public Optional<Integer> getEvolutionCandyCost() {
        return evolutionCandyCost;
    }

    /**
     * Pokemon value interpreted from the data or user input. Is null right after a scan, needs to be updated by
     * external factor.
     * @param p
     */
    public void setPokemon(Pokemon p) {
        pokemon = p;
    }

    public Pokemon getPokemon(){
        return pokemon;
    }
}
