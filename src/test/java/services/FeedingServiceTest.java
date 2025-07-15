package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.*;
import enums.Biome;
import models.Animal;
import models.Carnivore;
import models.Herbivore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class FeedingServiceTest {
    private static final String HYENA_GROUP_NAME = "hyena test";
    private static final String LONERS_GROUP = "Loners";
    private final Set<Biome> biomes = Set.of(SAVANNA);
    private final Herbivore gazelle = new Herbivore(biomes, 10, true, 25, 25, 5, LAND, HERBIVORE, "GAZELLE", GROUP, true, 80, "gazelle test");
    private final Carnivore cheetah = new Carnivore(biomes, 10, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "CHEETAH", false, 110, LONERS_GROUP, 15);
    private final Carnivore hyenaOne = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private final Carnivore hyenaTwo = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private FeedingService feedingService;

    @BeforeEach
    void setUp() {
        feedingService = new FeedingService();
        cheetah.setCurrentHunger(0);
        hyenaOne.setCurrentHunger(0);
        hyenaTwo.setCurrentHunger(0);
    }

    @Test
    void testProcessSuccessfulHunt_whenPredatorInGroup_thenReducesHungerForAllMembers() {
        //given
        hyenaOne.setCurrentHunger(40.0);
        hyenaTwo.setCurrentHunger(40.0);
        List<Animal> hyenaGroup = List.of(hyenaOne, hyenaTwo);

        //when
        feedingService.processSuccessfulHunt(hyenaOne, gazelle, hyenaGroup);

        //then
        double finalHungerAttacker = hyenaOne.getCurrentHunger();
        double finalHungerSupporter = hyenaTwo.getCurrentHunger();

        assertNotEquals(40.0, finalHungerAttacker, "Attacker's hunger should change");
        assertNotEquals(40.0, finalHungerSupporter, "Supporter's hunger should change");
        assertEquals(6.7, finalHungerAttacker, "Attacker should get a double share of food, reducing hunger significantly");
        assertEquals(23.3, finalHungerSupporter, "Supporter should get a single share of food");
    }

    @Test
    void testCalculateHungerDecreaseAmount_shouldReturnCorrectValue() {
        //given //when
        double result = feedingService.calculateHungerDecreaseAmount(cheetah, gazelle);

        //then
        assertEquals(41.7, result, 0.1);
    }

    @Test
    void testIsUpdatedHungerGreaterThanInitial_whenDecreaseMoreThanHunger_shouldReturnTrueAndSetZero() {
        //given
        cheetah.setCurrentHunger(20.0);

        //when
        boolean result = feedingService.isUpdatedHungerGreaterThanInitial(30.0, 20.0, cheetah);

        //then
        assertEquals(0.0, cheetah.getCurrentHunger(), 0.01);
        assertTrue(result);
    }

    @Test
    void testIsUpdatedHungerGreaterThanInitial_whenDecreaseLessThanOrEqual_shouldReturnFalseAndKeepValue() {
        //given
        cheetah.setCurrentHunger(50.0);

        //when
        boolean result = feedingService.isUpdatedHungerGreaterThanInitial(20.0, 50.0, cheetah);

        //then
        assertEquals(50.0, cheetah.getCurrentHunger(), 0.01);
        assertFalse(result);
    }

    @Test
    void testRoundToOneDecimal_shouldRoundCorrectly() {
        //given //when
        double result = feedingService.roundToOneDecimal(12.345);

        //then
        assertEquals(12.3, result);
    }

    @Test
    void testFeedAttackerWithinGroup_shouldReduceHungerByDoubleAmount() {
        //given
        hyenaOne.setCurrentHunger(50.0);
        double baseShare = 10.0;

        //when
        feedingService.feedAttackerWithinGroup(baseShare, hyenaOne);

        //then
        assertEquals(30.0, hyenaOne.getCurrentHunger(), 0.1);
    }

    @Test
    void testFeedSupportersWithinGroup_shouldReduceHungerBySingleAmount() {
        //given
        hyenaTwo.setCurrentHunger(50.0);
        double baseShare = 10.0;

        //when
        feedingService.feedSupportersWithinGroup(baseShare, hyenaTwo);

        //then
        assertEquals(40.0, hyenaTwo.getCurrentHunger(), 0.1);
    }
}
