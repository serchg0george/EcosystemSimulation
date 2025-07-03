package models.animals;

import static enums.AnimalKind.ZEBRA;
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
    private Animal zebra;

    @BeforeEach
    void setUp() {
        final int zebraMaxAge = 50;
        final int zebraWeight = 300;
        final int zebraReproductiveRate = 10;
        final int zebraEscapePoints = 80;
        int currentAge = 10;
        boolean isAlive = true;
        boolean isZebraInGroup = true;
        Set<Biome> biomes = new HashSet<>();
        biomes.add(SAVANNA);
        String ZEBRA_GROUP_NAME = "zebra test";
        zebra = new Herbivore(biomes, currentAge, isAlive, zebraMaxAge, zebraWeight, zebraReproductiveRate, LAND, HERBIVORE, ZEBRA, GROUP, isZebraInGroup, zebraEscapePoints, ZEBRA_GROUP_NAME);
    }

    @Test
    void testGrowUp_whenCalled_thenShouldReturnIncreasedAge() {
        //given, when, then
        assertNotEquals(zebra.getCurrentAge(), zebra.growUp(zebra.getCurrentAge()));
    }

    @Test
    void testBreed_whenCalled_thenShouldReturnNewSameHerbivoreKind() {
        //given
        Animal newBornZebra = zebra.breed(zebra);

        //when //then
        assertEquals(HERBIVORE, newBornZebra.getAnimalType());
        assertTrue(newBornZebra.getBiomes().contains(SAVANNA));
        assertFalse(newBornZebra.getCurrentAge() > 0);
    }
}