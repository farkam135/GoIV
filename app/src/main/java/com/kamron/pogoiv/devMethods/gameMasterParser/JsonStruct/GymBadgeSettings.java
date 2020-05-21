
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GymBadgeSettings {

    @SerializedName("target")
    @Expose
    private List<Integer> target = null;
    @SerializedName("battleWinningScorePerDefenderCp")
    @Expose
    private Double battleWinningScorePerDefenderCp;
    @SerializedName("gymDefendingScorePerMinute")
    @Expose
    private Double gymDefendingScorePerMinute;
    @SerializedName("berryFeedingScore")
    @Expose
    private Integer berryFeedingScore;
    @SerializedName("pokemonDeployScore")
    @Expose
    private Integer pokemonDeployScore;
    @SerializedName("raidBattleWinningScore")
    @Expose
    private Integer raidBattleWinningScore;
    @SerializedName("loseAllBattlesScore")
    @Expose
    private Integer loseAllBattlesScore;

    public List<Integer> getTarget() {
        return target;
    }

    public void setTarget(List<Integer> target) {
        this.target = target;
    }

    public Double getBattleWinningScorePerDefenderCp() {
        return battleWinningScorePerDefenderCp;
    }

    public void setBattleWinningScorePerDefenderCp(Double battleWinningScorePerDefenderCp) {
        this.battleWinningScorePerDefenderCp = battleWinningScorePerDefenderCp;
    }

    public Double getGymDefendingScorePerMinute() {
        return gymDefendingScorePerMinute;
    }

    public void setGymDefendingScorePerMinute(Double gymDefendingScorePerMinute) {
        this.gymDefendingScorePerMinute = gymDefendingScorePerMinute;
    }

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

    public void setRaidBattleWinningScore(Integer raidBattleWinningScore) {
        this.raidBattleWinningScore = raidBattleWinningScore;
    }

    public Integer getLoseAllBattlesScore() {
        return loseAllBattlesScore;
    }

    public void setLoseAllBattlesScore(Integer loseAllBattlesScore) {
        this.loseAllBattlesScore = loseAllBattlesScore;
    }

}
