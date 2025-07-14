package services;

/**
 * The main entry point for the animal simulation program.
 *
 * <p>This method initializes the core services and starts the simulation by:
 * <ol>
 *   <li>Creating a {@link ProbabilitiesService} instance to handle probability calculations</li>
 *   <li>Creating an {@link FeedingService} instance to handle feeding process</li>
 *   <li>Creating an {@link AnimalFactory} instance to generate animal entities</li>
 *   <li>Initializing a {@link SimulationRunner} with these services</li>
 *   <li>Starting the simulation process</li>
 * </ol>
 *
 * <p>The program expects no command-line arguments as all configuration should be
 * handled internally by the services and simulation components.
 *
 * @see ProbabilitiesService
 * @see FeedingService
 * @see AnimalFactory
 * @see SimulationRunner
 */
public class Main {
    public static void main(String[] args) {
        final ProbabilitiesService probabilitiesService = new ProbabilitiesService();
        final FeedingService feedingService = new FeedingService();
        final AnimalFactory animalFactory = new AnimalFactory();
        SimulationRunner runner = new SimulationRunner(probabilitiesService, animalFactory, feedingService);
        runner.startSimulation();
    }
}
