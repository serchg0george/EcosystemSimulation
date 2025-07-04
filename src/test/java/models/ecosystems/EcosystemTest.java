package models.ecosystems;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.HerbivoreKind.ZEBRA;
import static enums.LivingType.GROUP;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import enums.AnimalKind;
import enums.Biome;
import models.animals.Animal;
import models.animals.Group;
import models.animals.Herbivore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class EcosystemTest {

    private List<Animal> lonerAnimals;
    private List<Group> updatedGroups;
    private List<Animal> zebraAnimals;
    private Map<AnimalKind, List<Group>> groupedAnimals;
    private Map<String, List<Animal>> groupMembers;
    private Ecosystem ecosystem;
    private Animal zebra;
    private Group zebraGroup;

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
        groupMembers = new HashMap<>();
        zebraAnimals = new ArrayList<>();
        zebraAnimals.add(zebra);
        zebraAnimals.add(zebra);
        groupMembers.put(ZEBRA_GROUP_NAME, zebraAnimals);
        zebraGroup = new Group(groupMembers);
        updatedGroups = new ArrayList<>();
        updatedGroups.add(zebraGroup);
        groupedAnimals = new HashMap<>();
        groupedAnimals.put(zebra.getAnimalKind(), updatedGroups);
        lonerAnimals = new ArrayList<>();
        String SAVANNA_ECOSYSTEM = "Savanna";
        ecosystem = new Ecosystem(SAVANNA_ECOSYSTEM, SAVANNA, lonerAnimals, updatedGroups, groupedAnimals);
    }

    @Test
    void testAddAnimalToEcosystem_whenCalled_thenShouldAddNewAnimalToEcosystem() {
        //given
        List<Group> groups = groupedAnimals.get(zebra.getAnimalKind());
        int initialGroupSize = groups.stream()
                .findFirst()
                .map(group -> group.getGroupedAnimals().get(zebra.getGroupName()).size())
                .orElse(0);

        // when
        ecosystem.addAnimalToEcosystem(zebra);

        //then
        assertNotEquals(initialGroupSize, groupedAnimals.size());
    }
}
