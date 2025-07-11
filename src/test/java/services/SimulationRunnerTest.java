package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import enums.AnimalType;
import enums.Biome;
import models.Animal;
import models.Ecosystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

class SimulationRunnerTest {

    private final ProbabilitiesService mockedProbabilitiesService = mock(ProbabilitiesService.class);
    private final AnimalFactory mockedAnimalFactory = mock(AnimalFactory.class);
    private SimulationRunner simulationRunner;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;

    @BeforeEach
    void setUp() {
        simulationRunner = new SimulationRunner(mockedProbabilitiesService, mockedAnimalFactory);
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testCreateEcosystem_whenValidBiomeProvided_thenEcosystemCreatedWithThatBiome() {
        //given
        String input = "3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        //when
        Ecosystem result = simulationRunner.createEcosystem(scanner);

        //then
        assertEquals(DESERT, result.getBiome());
        assertTrue(outputCapture.toString().contains("Choose biome"));
    }

    @Test
    void testProcessBreeding_whenAgeDivisibleByIteration_thenBreedTriggered() {
        //given
        Ecosystem ecosystem = mock(Ecosystem.class);
        Animal mockAnimal = mock(Animal.class);
        when(mockAnimal.getCurrentAge()).thenReturn(2);

        when(ecosystem.getEcosystemGroupedAnimals())
                .thenReturn(Map.of(
                        CARNIVORE, Map.of("Lions", List.of(mockAnimal)),
                        HERBIVORE, Map.of("Gazelles", List.of(mockAnimal))
                ));

        //when
        simulationRunner.processBreeding(ecosystem, 2);

        //then
        verify(mockAnimal, times(2)).breed(mockAnimal);
    }

    @Test
    void testAgeAllAnimals_whenCalled_thenAllAnimalsAged() {
        //given
        Ecosystem ecosystem = mock(Ecosystem.class);
        Animal mockAnimal = mock(Animal.class);

        when(ecosystem.getEcosystemGroupedAnimals())
                .thenReturn(Map.of(
                        CARNIVORE, Map.of("Lions", List.of(mockAnimal)),
                        HERBIVORE, Map.of("Gazelles", List.of(mockAnimal))
                ));

        //when
        simulationRunner.ageAllAnimals(ecosystem);

        //then
        verify(mockAnimal, times(2)).growUp(anyInt());
    }

    @Test
    void testPickEcosystem_whenValidIndex_thenReturnsCorrectEcosystem() {
        //given
        List<Ecosystem> ecosystems = List.of(
                new Ecosystem(SAVANNA, new EnumMap<>(AnimalType.class), mockedProbabilitiesService)
        );

        //when
        Ecosystem result = simulationRunner.pickEcosystem(ecosystems, 0);

        //then
        assertEquals(SAVANNA, result.getBiome());
    }

    @Test
    void testGetBiome_whenValidIndex_thenReturnsCorrectBiome() {
        //given
        int biomeIndex = 0;

        //when
        Biome result = simulationRunner.getBiome(biomeIndex);

        //then
        assertEquals(SAVANNA, result);
    }
}
