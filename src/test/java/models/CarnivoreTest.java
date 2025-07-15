package models;

import static enums.AnimalType.CARNIVORE;
import static enums.Biome.SAVANNA;
import static enums.Biome.TUNDRA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class CarnivoreTest {

    private Carnivore tiger;

    @BeforeEach
    void setUp() {
        tiger = new Carnivore(Set.of(SAVANNA, TUNDRA), 5, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "tiger", false, 75, "Loners", 18);
    }

    @Test
    void testBreed_whenCalled_thenCreateNewCarnivoreWithSameKind() {
        //given
        Animal newBornTiger = tiger.breed(tiger);

        //when //then
        assertEquals(CARNIVORE, newBornTiger.getAnimalType(), "Created animal belongs to the carnivore, passed");
        assertEquals("tiger", newBornTiger.getAnimalKind(), "Created animal belongs to the same kind, passed");
        assertTrue(newBornTiger.getBiomes().contains(SAVANNA), "Created animal contains biome Savanna, passed");
        assertFalse(newBornTiger.getCurrentAge() > 0, "Age of created animal is 0, passed");
    }

    @Test
    void testHasDiedFromHunger_whenHungerOverHundred_thenReturnTrue() {
        //given //when
        for (int i = 0; i < 20; i++) {
            tiger.increaseHunger();
        }

        //then
        assertTrue(tiger.hasDiedFromHunger(), "Tiger has died from hunger, pass");
    }

    @Test
    void testIncreaseHunger_whenCalled_thenHungerShouldBeIncreased() {
        //given
        double initHunger = tiger.getCurrentHunger();

        //when
        tiger.increaseHunger();

        //then
        assertNotEquals(initHunger, tiger.getCurrentHunger());
        assertEquals(18.0, tiger.getCurrentHunger(), "Increased hunger rate, passed");
    }
}