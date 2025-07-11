package services;

import static enums.Biome.SAVANNA;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import enums.AnimalType;
import models.Animal;
import models.Ecosystem;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class AnimalFactoryTest {
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals = new EnumMap<>(AnimalType.class);
    private final ProbabilitiesService probabilitiesService = mock(ProbabilitiesService.class);
    private final Ecosystem ecosystem = new Ecosystem(SAVANNA, ecosystemGroupedAnimals, probabilitiesService);
    private final AnimalFactory animalFactory = new AnimalFactory();

    @Test
    void testCreateAnimals_whenCalled_thenAddCreatedAnimalsToEcosystem() {
        //given
        int initGroupsSize = ecosystemGroupedAnimals.size();

        //when
        animalFactory.createAnimals(ecosystem, "zebra", "zebras", 10);

        //then
        assertNotEquals(initGroupsSize, ecosystemGroupedAnimals.size());
    }
}