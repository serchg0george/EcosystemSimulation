package models;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import enums.AnimalType;
import enums.Biome;
import exceptions.AnimalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import services.FeedingService;
import services.ProbabilitiesService;

import java.util.*;

class EcosystemTest {
    private static final String ZEBRA_GROUP_NAME = "zebra test";
    private static final String HYENA_GROUP_NAME = "hyena test";
    private static final String GAZELLE_GROUP_NAME = "gazelle test";
    private static final String LONERS_GROUP = "Loners";
    private final ProbabilitiesService mockedProbabilitiesService = mock(ProbabilitiesService.class);
    private final FeedingService feedingService = new FeedingService();
    private final Set<Biome> biomes = Set.of(SAVANNA);
    private final Herbivore zebra = new Herbivore(biomes, 10, true, 50, 300, 10, LAND, HERBIVORE, "ZEBRA", GROUP, true, 80, ZEBRA_GROUP_NAME);
    private final Herbivore gazelle = new Herbivore(biomes, 10, true, 25, 25, 5, LAND, HERBIVORE, "GAZELLE", GROUP, true, 80, GAZELLE_GROUP_NAME);
    private final Carnivore cheetah = new Carnivore(biomes, 10, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "CHEETAH", false, 110, LONERS_GROUP, 15);
    private final Carnivore hyenaOne = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private final Carnivore hyenaTwo = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private Map<String, List<Animal>> groupedHerbivores;
    private Map<String, List<Animal>> groupedCarnivores;
    private Ecosystem ecosystem;

    @BeforeEach
    void setUp() {
        groupedHerbivores = new HashMap<>();
        groupedCarnivores = new HashMap<>();
        Map<AnimalType, Map<String, List<Animal>>> ecosystemAnimals = new EnumMap<>(AnimalType.class);
        groupedHerbivores.put(LONERS_GROUP, new ArrayList<>());
        groupedHerbivores.put(ZEBRA_GROUP_NAME, new ArrayList<>());
        groupedHerbivores.put(GAZELLE_GROUP_NAME, new ArrayList<>());
        groupedCarnivores.put(HYENA_GROUP_NAME, new ArrayList<>());
        groupedCarnivores.put(LONERS_GROUP, new ArrayList<>());
        ecosystemAnimals.put(CARNIVORE, groupedCarnivores);
        ecosystemAnimals.put(HERBIVORE, groupedHerbivores);
        ecosystem = new Ecosystem(SAVANNA, ecosystemAnimals, mockedProbabilitiesService, feedingService);
    }

    @Test
    void testAttack_whenPredatorNotFound_thenThrowAnimalNotFoundException() {
        //given
        ecosystem.addAnimalToEcosystem(zebra);
        long nonExistingPredatorId = 9999L;

        //when
        Executable attackAction = () -> ecosystem.attack(nonExistingPredatorId, zebra.getId());

        //then
        assertThrows(AnimalNotFoundException.class, attackAction, "Should throw when predator not found");
    }

    @Test
    void testAttack_whenVictimNotFound_thenThrowAnimalNotFoundException() {
        //given
        ecosystem.addAnimalToEcosystem(hyenaOne);
        long nonExistingVictimId = 9999L;

        //when
        Executable attackAction = () -> ecosystem.attack(hyenaOne.getId(), nonExistingVictimId);

        //then
        assertThrows(AnimalNotFoundException.class, attackAction, "Should throw when victim not found");
    }

    @Test
    void testAttack_whenAttackNotSucceed_thenHungerNotReduced() {
        //given
        ecosystem.addAnimalToEcosystem(gazelle);
        ecosystem.addAnimalToEcosystem(hyenaOne);
        double initialHunger = hyenaOne.getCurrentHunger();
        when(mockedProbabilitiesService.getChanceForAttack()).thenReturn(100);

        //when
        ecosystem.attack(hyenaOne.getId(), gazelle.getId());

        //then
        assertEquals(initialHunger, hyenaOne.getCurrentHunger(), "Hunger should not change after failed attack");
        assertTrue(groupedHerbivores.get(GAZELLE_GROUP_NAME).contains(gazelle), "Gazelle should remain in ecosystem");
    }

    @Test
    void testAddAnimalToEcosystem_whenNewGroup_thenCreatesNewGroup() {
        //given
        String newGroupName = "newGroup";
        Herbivore antelope = new Herbivore(biomes, 10, true, 30, 50, 8, LAND, HERBIVORE, "ANTELOPE", GROUP, true, 75, newGroupName);
        int initialGroupCount = groupedHerbivores.size();

        //when
        ecosystem.addAnimalToEcosystem(antelope);

        //then
        assertEquals(initialGroupCount + 1, groupedHerbivores.size(), "Should add new group");
        assertTrue(groupedHerbivores.containsKey(newGroupName), "New group should be created");
        assertEquals(1, groupedHerbivores.get(newGroupName).size(), "Animal should be in new group");
    }

    @Test
    void testIncreaseHungerOfCarnivore_whenStarved_thenRemovesAnimal() {
        //given
        ecosystem.addAnimalToEcosystem(hyenaOne);
        List<Animal> hyenaGroup = groupedCarnivores.get(HYENA_GROUP_NAME);
        int initialGroupSize = hyenaGroup.size();

        // Max out hunger to kill animal
        for (int i = 0; i < 20; i++) {
            hyenaOne.increaseHunger();
        }

        //when
        ecosystem.increaseHungerOfCarnivore(groupedCarnivores);

        //then
        assertEquals(initialGroupSize - 1, hyenaGroup.size(), "Starved animal should be removed");
    }

    @Test
    void testAttack_whenPredatorIsHerbivore_thenThrowClassCastException() {
        //given
        ecosystem.addAnimalToEcosystem(zebra);
        ecosystem.addAnimalToEcosystem(gazelle);

        //when
        Executable attackAction = () -> ecosystem.attack(zebra.getId(), gazelle.getId());

        //then
        assertThrows(ClassCastException.class, attackAction, "Should throw when predator is herbivore");
    }

    @Test
    void testAttack_whenGroupExtinctAfterAttack_thenRemovesGroup() {
        //given
        ecosystem.addAnimalToEcosystem(cheetah);
        ecosystem.addAnimalToEcosystem(zebra);
        when(mockedProbabilitiesService.getChanceForAttack()).thenReturn(0);
        int initialHerbivoreGroups = groupedHerbivores.size();

        //when
        ecosystem.attack(cheetah.getId(), zebra.getId());

        //then
        assertFalse(groupedHerbivores.containsKey(zebra.getGroupName()), "Loner group should be removed");
        assertEquals(initialHerbivoreGroups - 1, groupedCarnivores.size(), "Carnivore groups should decrease");
    }

    @Test
    void testAddAnimalToEcosystem_whenFirstAnimalOfType_thenInitializesType() {
        //given
        Map<AnimalType, Map<String, List<Animal>>> emptyAnimals = new EnumMap<>(AnimalType.class);
        Ecosystem newEcosystem = new Ecosystem(SAVANNA, emptyAnimals, mockedProbabilitiesService, feedingService);

        //when
        newEcosystem.addAnimalToEcosystem(zebra);

        //then
        assertTrue(newEcosystem.getEcosystemGroupedAnimals().containsKey(HERBIVORE), "Herbivore type should be initialized");
        assertEquals(1, newEcosystem.getEcosystemGroupedAnimals().get(HERBIVORE).size(), "Should have one herbivore group");
    }

    @Test
    void testAttackReduceHunger_whenAttackSuccessful_theShouldReduceHunger() {
        //given
        ecosystem.addAnimalToEcosystem(gazelle);
        ecosystem.addAnimalToEcosystem(hyenaOne);
        ecosystem.addAnimalToEcosystem(hyenaTwo);
        Carnivore hyenaOneCarnivore = hyenaOne;
        Carnivore hyenaTwoCarnivore = hyenaTwo;
        for (int i = 0; i < 3; i++) {
            hyenaOneCarnivore.increaseHunger();
            hyenaTwoCarnivore.increaseHunger();
        }
        double initialHungerRateHyenaOne = hyenaOneCarnivore.getCurrentHunger();
        double initialHungerRateHyenaTwo = hyenaTwoCarnivore.getCurrentHunger();
        when(mockedProbabilitiesService.getChanceForAttack()).thenReturn(0);

        //when
        ecosystem.attack(hyenaOne.getId(), gazelle.getId());

        //then
        double currentHungerHyenaOne = hyenaOneCarnivore.getCurrentHunger();
        double currentHungerHyenaTwo = hyenaTwoCarnivore.getCurrentHunger();
        assertNotEquals(initialHungerRateHyenaOne, currentHungerHyenaOne, "Initial hunger of hyena one not equals to current hunger");
        assertNotEquals(initialHungerRateHyenaTwo, currentHungerHyenaTwo, "Initial hunger of hyena one not equals to current hunger");
        assertEquals(8.7, currentHungerHyenaOne, "Current hunger of hyena one is equal to 8.7");
        assertEquals(25.3, currentHungerHyenaTwo, "Current hunger of hyena one is equal to 25.3");
    }

    @Test
    void testAttackIllegalTarget_whenTargetCarnivore_thenShouldThrowClassCastException() {
        //given
        ecosystem.addAnimalToEcosystem(cheetah);
        ecosystem.addAnimalToEcosystem(zebra);

        //when
        Executable attackAction = () -> ecosystem.attack(cheetah.getId(), cheetah.getId());

        //then
        assertThrows(ClassCastException.class, attackAction, "ClassCastException was thrown");
    }

    @Test
    void testAttack_whenCalledAndSucceed_thenShouldRemoveHerbivoreFromList() {
        //given
        ecosystem.addAnimalToEcosystem(cheetah);
        ecosystem.addAnimalToEcosystem(zebra);

        int initialHerbivoreGroupsSize = groupedHerbivores.size();

        when(mockedProbabilitiesService.getChanceForAttack()).thenReturn(0);

        //when
        ecosystem.attack(cheetah.getId(), zebra.getId());

        //then
        int currentHerbivoreGroupsSize = groupedHerbivores.size();
        assertNotEquals(initialHerbivoreGroupsSize, currentHerbivoreGroupsSize, "Attack was successful, zebra was killed and removed from ecosystem");
    }

    @Test
    void testAddLonerCarnivoreToEcosystem_whenCalled_thenShouldAddNewLonerCarnivoreToEcosystem() {
        //given
        List<Animal> groups = groupedCarnivores.get(cheetah.getGroupName());
        int initialGroupSize = groups.size();

        //when
        ecosystem.addAnimalToEcosystem(cheetah);

        //then
        int finalGroupSize = groups.size();
        assertNotEquals(initialGroupSize, finalGroupSize, "Carnivore was added");
    }

    @Test
    void testAddHerbivoreToEcosystem_whenCalled_thenShouldAddNewHerbivoreToEcosystem() {
        //given
        List<Animal> groups = groupedHerbivores.get(zebra.getGroupName());
        int initialGroupSize = groups.size();

        // when
        ecosystem.addAnimalToEcosystem(zebra);

        //then
        int finalGroupSize = groups.size();
        assertNotEquals(initialGroupSize, finalGroupSize, "Herbivore was added");
    }
}
