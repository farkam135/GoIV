package com.kamron.pogoiv;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IVCombinationTest {

    @Test
    public void derivesPerfectPercentage() {
        IVCombination ivCombination = new IVCombination(40, 50, 60);
        assertThat(ivCombination.percentPerfect).isEqualTo(333);
    }
}
