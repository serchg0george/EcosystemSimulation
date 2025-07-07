package models;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.CarnivoreKind.CHEETAH;
import static enums.Habitat.LAND;
import static enums.HerbivoreKind.ZEBRA;
import static enums.LivingType.ALONE;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import enums.AnimalKind;
import enums.Biome;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.ProbabilitiesService;

import java.util.*;

class EcosystemTest {
    private final int zebraMaxAge = 50;
    private final int zebraWeight = 300;
    private final int zebraReproductiveRate = 10;
    private final int zebraEscapePoints = 80;
    private final int cheetahMaxAge = 30;
    private final int cheetahWeight = 60;
    private final int cheetahReproductiveRate = 5;
    private final int cheetahAttackPoints = 110;
    private final String ZEBRA_GROUP_NAME = "zebra test";
    private final String LONERS_GROUP = "Loners";
    private final ProbabilitiesService probabilitiesService = mock(ProbabilitiesService.class);
    private final Set<Biome> biomes = Set.of(SAVANNA);
    boolean zebraIsAlive = true;
    boolean cheetahIsAlive = true;
    boolean isZebraInGroup = true;
    boolean isCheetahInGroup = false;
    private int zebraCurrentAge = 10;
    private int cheetahCurrentAge = 10;
    private List<Group> updatedGroups;
    private Map<AnimalKind, List<Group>> groupedAnimals;
    private List<Animal> groupMembers;
    private Ecosystem ecosystem;
    private final Animal zebra = new Herbivore(biomes, zebraCurrentAge, zebraIsAlive, zebraMaxAge, zebraWeight, zebraReproductiveRate, LAND, HERBIVORE, ZEBRA, GROUP, isZebraInGroup, zebraEscapePoints, ZEBRA_GROUP_NAME);
    private final Animal cheetah = new Carnivore(biomes, cheetahCurrentAge, cheetahIsAlive, cheetahMaxAge, cheetahWeight, cheetahReproductiveRate, LAND, CARNIVORE, ALONE, CHEETAH, isCheetahInGroup, cheetahAttackPoints, LONERS_GROUP, 15);

    @BeforeEach
    void setUp() {
        groupMembers = new ArrayList<>();
        Group zebraGroup = new Group(ZEBRA_GROUP_NAME, groupMembers);
        updatedGroups = new ArrayList<>();
        updatedGroups.add(zebraGroup);
        updatedGroups.add(new Group("Loners", new ArrayList<>()));
        groupedAnimals = new HashMap<>();
        groupedAnimals.put(zebra.getAnimalKind(), updatedGroups);
        groupedAnimals.put(cheetah.getAnimalKind(), updatedGroups);
        ecosystem = new Ecosystem(SAVANNA.toString(), SAVANNA, updatedGroups, groupedAnimals, probabilitiesService);
    }

    @AfterEach
    void tearDown() {
        updatedGroups.clear();
    }

    @Test
    void testAddLonerCarnivoreToEcosystem_whenCalled_thenShouldAddNewLonerCarnivoreToEcosystem() {
        //given
        List<Group> groups = groupedAnimals.get(cheetah.getAnimalKind());
        int initialGroupSize = groups.stream()
                .filter(group -> group.getGroupName().equals(LONERS_GROUP))
                .findFirst()
                .map(group -> group.getGroupedAnimals().size())
                .orElse(0);

        //when
        ecosystem.addAnimalToEcosystem(cheetah);

        //then
        int finalGroupSize = groups.stream()
                .filter(group -> group.getGroupName().equals(LONERS_GROUP))
                .findFirst()
                .map(group -> group.getGroupedAnimals().size())
                .orElse(0);
        assertNotEquals(initialGroupSize, finalGroupSize);
    }

    @Test
    void testAddHerbivoreToEcosystem_whenCalled_thenShouldAddNewHerbivoreToEcosystem() {
        //given
        List<Group> groups = groupedAnimals.get(zebra.getAnimalKind());
        int initialGroupSize = groups.stream()
                .findFirst()
                .map(group -> group.getGroupedAnimals().size())
                .orElse(0);

        // when
        ecosystem.addAnimalToEcosystem(zebra);

        //then
        int finalGroupSize = groups.stream()
                .findFirst()
                .map(group -> group.getGroupedAnimals().size())
                .orElse(0);
        assertNotEquals(initialGroupSize, finalGroupSize);
    }

    @Test
    void testAttack_whenCalledAndSucceed_thenShouldRemoveHerbivoreFromList() {
        //given
        ecosystem.addAnimalToEcosystem(cheetah);
        ecosystem.addAnimalToEcosystem(zebra);

        Optional<Group> initialZebrasGroup = groupedAnimals.get(zebra.getAnimalKind())
                .stream()
                .filter(group -> group.getGroupName().equals(ZEBRA_GROUP_NAME))
                .findFirst();

        if (initialZebrasGroup.isEmpty()) {
            return;
        }
        int initialZebrasSize = initialZebrasGroup.stream().findAny().get().getGroupedAnimals().size();
        when(probabilitiesService.getChanceForAttack()).thenReturn(0);

        //when
        ecosystem.attack(cheetah, zebra);

        //then
        int currentZebrasSize = groupedAnimals.get(zebra.getAnimalKind())
                .stream()
                .filter(group -> group.getGroupName().equals(ZEBRA_GROUP_NAME))
                .findFirst()
                .get()
                .getGroupedAnimals()
                .size();

        assertNotEquals(initialZebrasSize, currentZebrasSize);
    }
}
