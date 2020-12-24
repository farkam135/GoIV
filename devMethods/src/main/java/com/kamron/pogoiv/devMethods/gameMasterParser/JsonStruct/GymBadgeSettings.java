package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GymBadgeSettings {
    @SerializedName("target")
    @Expose
    private List<Integer> targets = null;
    @Expose
    private Double battleWinningScorePerDefenderCp;
    @Expose
    private Double gymDefendingScorePerMinute;
    @Expose
    private Integer berryFeedingScore;
    @Expose
    private Integer pokemonDeployScore;
    @Expose
    private Integer raidBattleWinningScore;
    @Expose
    private Integer loseAllBattlesScore;

    public List<Integer> getTargets() {
        return targets;
    }

    public void setTargets(List<Integer> targets) {
        this.targets = targets;
    }

    public Double getBattleWinningScorePerDefenderCp() { return battleWinningScorePerDefenderCp; }

    public void setBattleWinningScorePerDefenderCp(Double battleWinningScorePerDefenderCp) { this.battleWinningScorePerDefenderCp = battleWinningScorePerDefenderCp; }

    public Double getGymDefendingScorePerMinute() {
        return gymDefendingScorePerMinute;
    }

    public void setGymDefendingScorePerMinute(Double gymDefendingScorePerMinute) { this.gymDefendingScorePerMinute = gymDefendingScorePerMinute; }

    public Integer getBerryFeedingScore() {
        return berryFeedingScore;
    }

    public void setBerryFeedingScore(Integer berryFeedingScore) {
        this.berryFeedingScore = berryFeedingScore;
    }

    public Integer getPokemonDeployScore() {
        return pokemonDeployScore;
    }

    public void setPokemonDeployScore(Integer pokemonDeployScore) {
        this.pokemonDeployScore = pokemonDeployScore;
    }

    public Integer getRaidBattleWinningScore() {
        return raidBattleWinningScore;
    }

    public void setRaidBattleWinningScore(Integer raidBattleWinningScore) { this.raidBattleWinningScore = raidBattleWinningScore; }

    public Integer getLoseAllBattlesScore() {
        return loseAllBattlesScore;
    }

    public void setLoseAllBattlesScore(Integer loseAllBattlesScore) {
        this.loseAllBattlesScore = loseAllBattlesScore;
    }
}
