package com.kamron.pogoiv.scanlogic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by NightMadness on 9/20/2016.
 */
public class PokeSpamTest {


    @Test
    public void testGetHowMuchWeCanEvolve() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 11);
        assertEquals(pokeSpamCalculator.getTotalEvolvable(), 53);
    }

    @Test
    public void testEvolveRows() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals(pokeSpamCalculator.getEvolveRows(), 16);
    }

    @Test
    public void testGetEvolveExtra() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals(pokeSpamCalculator.getEvolveExtra(), 0);
    }

    @Test
    public void testNotEnoughCandy() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(2, 12);
        assertEquals(pokeSpamCalculator.getTotalEvolvable(), 0);
        assertEquals(pokeSpamCalculator.getEvolveRows(), 0);
        assertEquals(pokeSpamCalculator.getEvolveExtra(), 0);
    }

    // From https://github.com/farkam135/GoIV/pull/457#issuecomment-248880815.
    @Test
    public void testBlaisorbladeTestCases() throws Exception {
        assertEquals(new PokeSpam(11, 12).getTotalEvolvable(), 0);
        assertEquals(new PokeSpam(12, 12).getTotalEvolvable(), 1);
        assertEquals(new PokeSpam(22, 12).getTotalEvolvable(), 1);
        assertEquals(new PokeSpam(23, 12).getTotalEvolvable(), 2);
    }

    @Test
    public void testVenonat() throws Exception {
        assertEquals(new PokeSpam(150, 50).getTotalEvolvable(), 3);
    }

    @Test
    public void testExtraPokemonBasedOnExtraCandy() throws Exception {
        assertEquals(new PokeSpam(144, 12).getTotalEvolvable(), 13);
    }

}