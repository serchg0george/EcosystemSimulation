package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.DESERT;
import static enums.Biome.SAVANNA;
import static org.junit.jupiter.api.Assertions.*;
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
    void testProcessBreeding_whenAgeNotDivisibleByRate_thenNoBreed() {
        //given
        Ecosystem ecosystem = mock(Ecosystem.class);
        Animal mockAnimal = mock(Animal.class);
        when(mockAnimal.getCurrentAge()).thenReturn(5);
        when(mockAnimal.getReproductiveRate()).thenReturn(10);

        when(ecosystem.getEcosystemGroupedAnimals())
                .thenReturn(Map.of(
                        CARNIVORE, Map.of("Lions", List.of(mockAnimal)),
                        HERBIVORE, Map.of("Gazelles", List.of(mockAnimal))
                ));

        //when
        simulationRunner.processBreeding(ecosystem);

        //then
        verify(mockAnimal, never()).breed(any());
    }

    @Test
    void testCreateMultipleAnimals_whenInvalidThenValid_thenFactoryInvokedOnce() {
        //given
        Ecosystem eco = mock(Ecosystem.class);
        String input = """
                foo
                2
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(in);

        //when
        simulationRunner.createMultipleAnimals(sc, "Lion", "Pride", eco);

        //then
        verify(mockedAnimalFactory, times(1)).createAnimals(eco, "Lion", "Pride", 2);
    }

    @Test
    void testSelectRandomAnimal_whenSingleAnimal_thenReturn() {
        //given
        Collection<List<Animal>> animalGroups = new ArrayList<>();
        Animal expectedAnimal = mock(Animal.class);
        when(expectedAnimal.isAlive()).thenReturn(true);
        animalGroups.add(List.of(expectedAnimal));

        //when
        Animal result = simulationRunner.selectRandomAnimal(animalGroups);

        //then
        assertEquals(expectedAnimal, result);
    }

    @Test
    void testSelectOrCreateEcosystem_whenCreateNew_thenReturnsNewEcosystem() {
        //given
        List<Ecosystem> ecosystems = new ArrayList<>();
        Scanner input = new Scanner(new ByteArrayInputStream("2\n0\n".getBytes()));

        //when
        Ecosystem result = simulationRunner.selectOrCreateEcosystem(ecosystems, input);

        //then
        assertEquals(SAVANNA, result.getBiome());
        assertEquals(1, ecosystems.size());
    }

    @Test
    void testGetBiome_whenIndexOutOfBounds_thenThrowsException() {
        //given
        int invalidIndex = Biome.values().length;

        //when/then
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> simulationRunner.getBiome(invalidIndex));
    }

    @Test
    void testPickEcosystem_whenNegativeIndex_thenThrowsException() {
        //given
        List<Ecosystem> ecosystems = List.of(
                new Ecosystem(SAVANNA, new EnumMap<>(AnimalType.class), mockedProbabilitiesService)
        );

        //when/then
        assertThrows(IndexOutOfBoundsException.class,
                () -> simulationRunner.pickEcosystem(ecosystems, -1));
    }

    @Test
    void testRunSimulationLoop_whenExtinctImmediately_thenExitsLoop() {
        //given
        Ecosystem ecosystem = mock(Ecosystem.class);
        when(ecosystem.hasExtinctAnimalType()).thenReturn(true);

        //when
        simulationRunner.runSimulationLoop(ecosystem, 1);

        //then
        verify(ecosystem, never()).increaseHungerOfCarnivore(any());
    }

    @Test
    void testPromptForAnimalCreation_whenUserStopsImmediately_thenNoAnimalsCreated() {
        //given
        Scanner input = new Scanner(new ByteArrayInputStream("n\n".getBytes()));
        Ecosystem ecosystem = mock(Ecosystem.class);

        //when
        simulationRunner.promptForAnimalCreation(input, ecosystem);

        //then
        verify(ecosystem, never()).addAnimalToEcosystem(any());
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
        when(mockAnimal.getCurrentAge()).thenReturn(10);
        when(mockAnimal.getReproductiveRate()).thenReturn(10);

        when(ecosystem.getEcosystemGroupedAnimals())
                .thenReturn(Map.of(
                        CARNIVORE, Map.of("Lions", List.of(mockAnimal)),
                        HERBIVORE, Map.of("Gazelles", List.of(mockAnimal))
                ));

        //when
        simulationRunner.processBreeding(ecosystem);

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