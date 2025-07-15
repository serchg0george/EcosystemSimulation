package models;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.*;
import enums.Biome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class HerbivoreTest {
    private static final String ZEBRA_GROUP_NAME = "zebra test";
    private static final int ZEBRA_MAX_AGE = 50;
    private static final int ZEBRA_WEIGHT = 300;
    private static final int ZEBRA_REPRODUCTIVE_RATE = 10;
    private static final int ZEBRA_ESCAPE_POINTS = 80;
    private Herbivore zebra;

    @BeforeEach
    void setUp() {
        int currentAge = 10;
        boolean isAlive = true;
        boolean isZebraInGroup = true;
        Set<Biome> biomes = new HashSet<>();
        biomes.add(SAVANNA);
        zebra = new Herbivore(biomes, currentAge, isAlive, ZEBRA_MAX_AGE, ZEBRA_WEIGHT, ZEBRA_REPRODUCTIVE_RATE, LAND, HERBIVORE, "ZEBRA", GROUP, isZebraInGroup, ZEBRA_ESCAPE_POINTS, ZEBRA_GROUP_NAME);
    }

    @Test
    void testGrowUp_whenCalled_thenShouldReturnIncreasedAge() {
        //given
        int initialAge = zebra.getCurrentAge();

        //when
        zebra.growUp(zebra.getCurrentAge());

        //then
        assertNotEquals(initialAge, zebra.getCurrentAge(), "Age was increased");
    }

    @Test
    void testBreed_whenCalled_thenShouldReturnNewSameHerbivoreKind() {
        //given
        Animal newBornZebra = zebra.breed(zebra);

        //when //then
        assertEquals(HERBIVORE, newBornZebra.getAnimalType(), "Created animal belongs to the herbivore, passed");
        assertTrue(newBornZebra.getBiomes().contains(SAVANNA), "Created animal contains biome Savanna");
        assertFalse(newBornZebra.getCurrentAge() > 0, "Age of created animal is 0");
    }
}
