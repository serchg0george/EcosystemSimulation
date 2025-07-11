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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import services.ProbabilitiesService;

import java.util.*;

class EcosystemTest {
    private static final String ZEBRA_GROUP_NAME = "zebra test";
    private static final String HYENA_GROUP_NAME = "hyena test";
    private static final String GAZELLE_GROUP_NAME = "gazelle test";
    private static final String LONERS_GROUP = "Loners";
    private final ProbabilitiesService mockedProbabilitiesService = mock(ProbabilitiesService.class);
    private final Set<Biome> biomes = Set.of(SAVANNA);
    private final Herbivore zebra = new Herbivore(biomes, 10, true, 50, 300, 10, LAND, HERBIVORE, "ZEBRA", GROUP, true, 80, ZEBRA_GROUP_NAME);
    private final Herbivore gazelle = new Herbivore(biomes, 10, true, 25, 25, 5, LAND, HERBIVORE, "GAZELLE", GROUP, true, 80, GAZELLE_GROUP_NAME);
    private final Carnivore cheetah = new Carnivore(biomes, 10, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "CHEETAH", false, 110, LONERS_GROUP, 15);
    private final Carnivore hyenaOne = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private final Carnivore hyenaTwo = new Carnivore(biomes, 10, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "HYENA", true, 80, HYENA_GROUP_NAME, 14);
    private List<Herbivore> zebras;
    private List<Herbivore> gazelles;
    private List<Carnivore> hyenas;
    private List<Carnivore> cheetahs;
    private Map<String, List<Animal>> groupedHerbivores;
    private Map<String, List<Animal>> groupedCarnivores;
    private Ecosystem ecosystem;

    @BeforeEach
    void setUp() {
        zebras = new ArrayList<>();
        gazelles = new ArrayList<>();
        hyenas = new ArrayList<>();
        cheetahs = new ArrayList<>();
        groupedHerbivores = new HashMap<>();
        groupedCarnivores = new HashMap<>();
        Map<AnimalType, Map<String, List<Animal>>> ecosystemAnimals = new EnumMap<>(AnimalType.class);
        zebras.add(zebra);
        gazelles.add(gazelle);
        hyenas.add(hyenaOne);
        hyenas.add(hyenaTwo);
        cheetahs.add(cheetah);
        groupedHerbivores.put(LONERS_GROUP, new ArrayList<>());
        groupedHerbivores.put(ZEBRA_GROUP_NAME, new ArrayList<>());
        groupedHerbivores.put(GAZELLE_GROUP_NAME, new ArrayList<>());
        groupedCarnivores.put(HYENA_GROUP_NAME, new ArrayList<>());
        groupedCarnivores.put(LONERS_GROUP, new ArrayList<>());
        ecosystemAnimals.put(CARNIVORE, groupedCarnivores);
        ecosystemAnimals.put(HERBIVORE, groupedHerbivores);
        ecosystem = new Ecosystem(SAVANNA, ecosystemAnimals, mockedProbabilitiesService);
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
