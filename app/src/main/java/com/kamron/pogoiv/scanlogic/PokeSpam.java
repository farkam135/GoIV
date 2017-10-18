package com.kamron.pogoiv.scanlogic;

import lombok.Getter;

/**
 * Created by NightMadness on 9/20/2016.
 */

@Getter
public class PokeSpam {
    public static final int HOW_MANY_POKEMON_WE_HAVE_PER_ROW = 3;
    private static final int DEFAULT_BONUS = 1;

    private int totalEvolvable;
    private int evolveRows;
    private int evolveExtra;
    private int amountXP;
    private int amountXPWithLuckyEgg;

    public PokeSpam(int candyPlayerHas, int candyEvolutionCost) {
        this(candyPlayerHas, candyEvolutionCost, DEFAULT_BONUS);
    }

    /**
     * pokeSpam this object allows you to calculate how many pokemon we can evolve using current candy..
     *
     * @param candyPlayerHas     How much candy the player has for this pokemon
     * @param candyEvolutionCost How much candy it cost to evolve the pokemon
     * @param bonus              How much bonus for evolving, for example 1 is for regular evolve, 2 is for transferring
     */
    private PokeSpam(int candyPlayerHas, int candyEvolutionCost, int bonus) {
        calculatePokeSpam(candyPlayerHas, candyEvolutionCost, bonus);
    }

    /**
     * calculatePokeSpam calculates how many pokemon we can evolve using current candy.
     *
     * @param candyPlayerHas     How much candy the player has for this pokemon
     * @param candyEvolutionCost How much candy it cost to evolve the pokemon
     * @param bonus              Optional: How much bonus for evolving, for example 1 is for regular evolve, 2 is for
     *                           transferring
     */
    private void calculatePokeSpam(int candyPlayerHas, int candyEvolutionCost, int bonus) {
        //Candy Amount divided by Evolve Cost without the left over,
        //maybe in the future we will have better bonuses
        //bonus = 1 for regular
        //bonus = 2 for transfer

        //math.floor is not needed as int already does it, but its makes it explicit that we do it for readability
        totalEvolvable = (int) Math.floor((candyPlayerHas - bonus) / (candyEvolutionCost - bonus));
        evolveRows = (int) Math.floor(totalEvolvable / HOW_MANY_POKEMON_WE_HAVE_PER_ROW);
        evolveExtra = (int) Math.floor(totalEvolvable % HOW_MANY_POKEMON_WE_HAVE_PER_ROW);
        amountXP = 500 * totalEvolvable;
        amountXPWithLuckyEgg = amountXP * 2;
    }
}
