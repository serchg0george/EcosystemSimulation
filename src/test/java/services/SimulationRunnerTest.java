package services;

import static enums.Biome.DESERT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import models.Ecosystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

class SimulationRunnerTest {

    private final ProbabilitiesService probabilitiesService = mock(ProbabilitiesService.class);
    private final AnimalFactory animalFactory = mock(AnimalFactory.class);
    private final SimulationRunner simulationRunner = new SimulationRunner(probabilitiesService, animalFactory);

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;

    @BeforeEach
    void setUp() {
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
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        //when
        Ecosystem result = simulationRunner.createEcosystem();

        //then
        assertEquals(DESERT, result.getBiome());
        assertTrue(outputCapture.toString().contains("Choose biome"));
    }
}
