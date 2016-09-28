package com.kamron.pogoiv.logic;

import com.google.common.base.Optional;

import lombok.Getter;

/**
 * Created by NightMadness on 9/20/2016.
 */

@Getter
public class PokeSpam {
    final int howManyPokemonWeHavePerRow = 3;
    final int defaultBonus = 1;

    private Integer totalEvolvable;
    private Integer evolveRows;
    private Integer evolveExtra;
    private Integer amountXP;
    private Integer amountXPWithLuckyEgg;

    public PokeSpam(int candyPlayerHas, int candyEvolutionCost) {
        calculatePokeSpam(candyPlayerHas, candyEvolutionCost, defaultBonus);
    }

    /**
     * pokeSpam this object allows you to calculate how many pokemon we can evolve using current candy..
     * @param candyPlayerHas How much candy the player has for this pokemon
     * @param candyEvolutionCost How much candy it cost to evolve the pokemon
     * @param bonus How much bonus for evolving, for example 1 is for regular evolve, 2 is for transferring
     */
    public PokeSpam(int candyPlayerHas, int candyEvolutionCost, Optional<Integer> bonus) {
        calculatePokeSpam(candyPlayerHas, candyEvolutionCost, bonus.or(defaultBonus));
    }

    /**
     * calculatePokeSpam calculates how many pokemon we can evolve using current candy.
     * @param candyPlayerHas How much candy the player has for this pokemon
     * @param candyEvolutionCost How much candy it cost to evolve the pokemon
     * @param bonus Optional: How much bonus for evolving, for example 1 is for regular evolve, 2 is for transferring
     */
    private void calculatePokeSpam(int candyPlayerHas, int candyEvolutionCost, int bonus) {
        //Candy Amount divided by Evolve Cost without the left over,
        //maybe in the future we will have better bonuses
        //bonus = 1 for regular
        //bonus = 2 for transfer

        //math.floor is not needed as int already does it, but its makes it explicit that we do it for readability
        totalEvolvable = (int) Math.floor((candyPlayerHas - bonus) / (candyEvolutionCost - bonus));
        evolveRows = (int) Math.floor(totalEvolvable / howManyPokemonWeHavePerRow);
        evolveExtra = (int) Math.floor(totalEvolvable % howManyPokemonWeHavePerRow);
        amountXP = 500 * totalEvolvable;
        amountXPWithLuckyEgg = (amountXP * 2);
    }

}