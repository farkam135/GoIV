package com.kamron.pogoiv.logic;

/**
 * Created by ofirp_000 on 9/20/2016.
 */

public class PokeSpam {
    final Integer dblPokemonPerRow = 3;
    private int pokemonCandyPlayerHas;
    private int candyEvolutionCost;
    private Integer dblHowMuchWeCanEvolve;
    private Integer intEvolveRows;
    private Integer intEvolveExtra;

    public PokeSpam(int pokemonCandyPlayerHas, int candyEvolutionCost) {
        //Candy Amount divided by Evolve Cost without the left over
        dblHowMuchWeCanEvolve = pokemonCandyPlayerHas / candyEvolutionCost;
        intEvolveRows = (int) Math.floor(dblHowMuchWeCanEvolve / dblPokemonPerRow);
        intEvolveExtra = (int) Math.floor(dblHowMuchWeCanEvolve % dblPokemonPerRow);
    }

    public Integer getDblHowMuchWeCanEvolve() {
        return dblHowMuchWeCanEvolve;
    }

    public Integer getIntEvolveRows() {
        return intEvolveRows;
    }

    public Integer getIntEvolveExtra() {
        return intEvolveExtra;
    }

}