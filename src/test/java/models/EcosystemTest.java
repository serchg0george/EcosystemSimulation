package models;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.HerbivoreKind.ZEBRA;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import enums.AnimalKind;
import enums.Biome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class EcosystemTest {
    private final int zebraMaxAge = 50;
    private final int zebraWeight = 300;
    private final int zebraReproductiveRate = 10;
    private final int zebraEscapePoints = 80;
    private final String ZEBRA_GROUP_NAME = "zebra test";
    boolean isAlive = true;
    boolean isZebraInGroup = true;
    Set<Biome> biomes = Set.of(SAVANNA);
    private int currentAge = 10;
    private List<Group> updatedGroups;
    private Map<AnimalKind, List<Group>> groupedAnimals;
    private List<Animal> groupMembers;
    private Ecosystem ecosystem;
    private final Animal zebra = new Herbivore(biomes, currentAge, isAlive, zebraMaxAge, zebraWeight, zebraReproductiveRate, LAND, HERBIVORE, ZEBRA, GROUP, isZebraInGroup, zebraEscapePoints, ZEBRA_GROUP_NAME);;
    private Group zebraGroup;

    @BeforeEach
    void setUp() {
        groupMembers = new ArrayList<>();
        zebraGroup = new Group(ZEBRA_GROUP_NAME, groupMembers);
        updatedGroups = new ArrayList<>();
        updatedGroups.add(zebraGroup);
        groupedAnimals = new HashMap<>();
        groupedAnimals.put(zebra.getAnimalKind(), updatedGroups);
        String SAVANNA_ECOSYSTEM = "Savanna";
        ecosystem = new Ecosystem(SAVANNA_ECOSYSTEM, SAVANNA, updatedGroups, groupedAnimals);
    }

    @Test
    void testAddAnimalToEcosystem_whenCalled_thenShouldAddNewAnimalToEcosystem() {
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
}
