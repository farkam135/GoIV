package com.kamron.pogoiv.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by NightMadness on 9/20/2016.
 */
public class PokeSpamTest {


    @Test
    public void testGetHowMuchWeCanEvolve() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 11);
        assertEquals((int) pokeSpamCalculator.getTotalEvolvable(), 53);
    }

    @Test
    public void testEvolveRows() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals((int) pokeSpamCalculator.getEvolveRows(), 16);
    }

    @Test
    public void testGetEvolveExtra() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals((int) pokeSpamCalculator.getEvolveExtra(), 0);
    }

    @Test
    public void testNotEnoughCandy() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(2, 12);
        assertEquals((int) pokeSpamCalculator.getTotalEvolvable(), 0);
        assertEquals((int) pokeSpamCalculator.getEvolveRows(), 0);
        assertEquals((int) pokeSpamCalculator.getEvolveExtra(), 0);
    }

    @Test
    public void testBlaisorbladeTestCases() throws Exception {
        assertEquals((new PokeSpam(11, 12).getTotalEvolvable()), (Integer) 0);
        assertEquals((new PokeSpam(12, 12).getTotalEvolvable()), (Integer) 1);
        assertEquals((new PokeSpam(22, 12).getTotalEvolvable()), (Integer) 1);
        assertEquals((new PokeSpam(23, 12).getTotalEvolvable()), (Integer) 2);
    }

    @Test
    public void testVenonat() throws Exception {
        assertEquals((new PokeSpam(150, 50).getTotalEvolvable()), (Integer) 3);
    }

    @Test
    public void testExtraPokemonBasedOnExtraCandy() throws Exception {
        assertEquals((new PokeSpam(144, 12).getTotalEvolvable()), (Integer) 13);
    }

}