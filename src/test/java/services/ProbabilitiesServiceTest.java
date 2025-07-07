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
        assertTrue(chance >= 0);
    }

    @Test
    void testGetChanceForAttack_whenCalled_thenShouldBeLowerThan100() {
        //given, when
        int chance = service.getChanceForAttack();

        //then
        assertTrue(chance <= 100);
    }
}
