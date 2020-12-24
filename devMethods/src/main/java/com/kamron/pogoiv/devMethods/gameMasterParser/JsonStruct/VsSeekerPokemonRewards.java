package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class VsSeekerPokemonRewards {
    static public class AvailablePokemon {
        @Expose
        private GuaranteedLimitedPokemonReward guaranteedLimitedPokemonReward;
        @Expose
        private Integer unlockedAtRank;
        @Expose
        private IvOverride attackIvOverride;
        @Expose
        private IvOverride defenseIvOverride;
        @Expose
        private IvOverride staminaIvOverride;
        @Expose
        private PokemonId pokemon;

        public GuaranteedLimitedPokemonReward getGuaranteedLimitedPokemonReward() { return guaranteedLimitedPokemonReward; }

        public void setGuaranteedLimitedPokemonReward(GuaranteedLimitedPokemonReward guaranteedLimitedPokemonReward) { this.guaranteedLimitedPokemonReward = guaranteedLimitedPokemonReward; }

        public Integer getUnlockedAtRank() { return unlockedAtRank; }

        public void setUnlockedAtRank(Integer unlockedAtRank) { this.unlockedAtRank = unlockedAtRank; }

        public IvOverride getAttackIvOverride() { return attackIvOverride; }

        public void setAttackIvOverride(IvOverride attackIvOverride) { this.attackIvOverride = attackIvOverride; }

        public IvOverride getDefenseIvOverride() { return defenseIvOverride; }

        public void setDefenseIvOverride(IvOverride defenseIvOverride) { this.defenseIvOverride = defenseIvOverride; }

        public IvOverride getStaminaIvOverride() { return staminaIvOverride; }

        public void setStaminaIvOverride(IvOverride staminaIvOverride) { this.staminaIvOverride = staminaIvOverride; }

        public PokemonId getPokemon() { return pokemon; }

        public void setPokemon(PokemonId pokemon) { this.pokemon = pokemon; }
    }

    @Expose
    private List<AvailablePokemon> availablePokemon = null;
    @Expose
    private String rewardTrack;

    public List<AvailablePokemon> getAvailablePokemon() { return availablePokemon; }

    public void setAvailablePokemon(List<AvailablePokemon> availablePokemon) { this.availablePokemon = availablePokemon; }

    public String getRewardTrack() { return rewardTrack; }

    public void setRewardTrack(String rewardTrack) { this.rewardTrack = rewardTrack; }
}
