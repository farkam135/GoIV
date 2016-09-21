package com.kamron.pogoiv.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by NightMadness on 9/20/2016.
 */
public class PokeSpamTest {

    //PidgySpam candy 536
    //candyCost 12
    //Rows 14 + 2 more

    @Test
    public void testGetDblHowMuchWeCanEvolve() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals((int)pokeSpamCalculator.getDblHowMuchWeCanEvolve(),44);
    }

    @Test
    public void testGetIntEvolveRows() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals((int)pokeSpamCalculator.getIntEvolveRows(),14);
    }

    @Test
    public void testGetIntEvolveExtra() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(536, 12);
        assertEquals((int)pokeSpamCalculator.getIntEvolveExtra(),2);
    }

    @Test
    public void testNotEnoghCandy() throws Exception {
        PokeSpam pokeSpamCalculator = new PokeSpam(2, 12);
        assertEquals((int)pokeSpamCalculator.getDblHowMuchWeCanEvolve(), 0);
        assertEquals((int)pokeSpamCalculator.getIntEvolveRows(), 0);
        assertEquals((int)pokeSpamCalculator.getIntEvolveExtra(), 0);
    }

}