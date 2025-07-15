package services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ProbabilitiesServiceTest {
    private final ProbabilitiesService service = new ProbabilitiesService();

    @Test
    void testGetChanceForAttack_whenCalled_thenShouldBeGreaterThanZero() {
        //given, when
        int chance = service.getChanceForAttack();

        //then
        assertTrue(chance >= 0, "Chance is greater than zero");
    }

    @Test
    void testGetChanceForAttack_whenCalled_thenShouldBeLowerThan100() {
        //given, when
        int chance = service.getChanceForAttack();

        //then
        assertTrue(chance <= 100, "Chance is lower than 100");
    }
}
