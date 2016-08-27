package com.kamron.pogoiv;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-08-27.
 *
 * A class for methods which is only used during development
 */
public class DevMethods {


    /**
     * Method used to generate the <item> </item> list for candy cost of evolution
     * Since pokemons follow the following pattern (with a few exceptions):
     * Pokemon with 3 evolutions will cost
     * 25,100,-
     * 2 evolutions
     * 50, -
     * Exceptions: Eevee, weedle, caterpie, pidgey, magicarp and rattatta
     *
     * To run this method, you need to make the pokemon field evolutionCandyCost not final and uncomment the method
     */
    private void printOutEvolutionCandyCosts(PokeInfoCalculator pokeCalculator){
        //to create the evolutions
        for (Pokemon poke : pokeCalculator.pokedex){
            ArrayList<Pokemon> evoLine = pokeCalculator.getEvolutionLine(poke);
            int numberInEvoLine =1;
            for (int i = 0; i<evoLine.size(); i++){
                System.out.println("poke:" + poke.name + " evoLine size: " + evoLine.size());
                if (poke.name.equals(evoLine.get(i).name)){
                    numberInEvoLine = i;
                }
            }
            /*
            if (evoLine.size() ==3){
                if (numberInEvoLine == 0){
                    poke.candyEvolutionCost = 25;
                }
                if (numberInEvoLine == 1){
                    poke.candyEvolutionCost = 100;
                }
                if (numberInEvoLine == 2){
                    poke.candyEvolutionCost = -1;
                }
            }
            if (evoLine.size() == 2){
                if (numberInEvoLine == 0){
                    poke.candyEvolutionCost = 50;
                }
                if (numberInEvoLine == 1){
                    poke.candyEvolutionCost = -1;
                }
            }
            if (evoLine.size()==1){
                poke.candyEvolutionCost = -1;
            }
            */

        }

        for (Pokemon poke : pokeCalculator.pokedex) {
            //<item>-1</item> <!--Bulbasaur-->
            System.out.println("nahojjjen" + "<item>" + poke.candyEvolutionCost + "</item> <!--" + poke.name + "-->");
        }
    }
}
