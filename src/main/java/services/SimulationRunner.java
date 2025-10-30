package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.*;
import enums.AnimalType;
import enums.Biome;
import exceptions.EcosystemNotFoundException;
import models.Animal;
import models.Ecosystem;

import java.util.*;

/**
 * Orchestrates the ecosystem simulation lifecycle including initialization, animal management,
 * and iteration execution. Handles user interactions for ecosystem/animal creation and drives
 * the core simulation loop until extinction occurs.
 */
public class SimulationRunner {
    private final ProbabilitiesService probabilitiesService;
    private final AnimalCreatorService animalCreatorService;
    private final FeedingService feedingService;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals = new EnumMap<>(AnimalType.class);
    private final Random random = new Random();

    public SimulationRunner(ProbabilitiesService probabilitiesService, AnimalCreatorService animalCreatorService, FeedingService feedingService) {
        this.probabilitiesService = probabilitiesService;
        this.animalCreatorService = animalCreatorService;
        this.feedingService = feedingService;
        Map<String, List<Animal>> lonerCarnivores = new HashMap<>();
        Map<String, List<Animal>> lonerHerbivores = new HashMap<>();
        ecosystemGroupedAnimals.put(CARNIVORE, lonerCarnivores);
        ecosystemGroupedAnimals.put(HERBIVORE, lonerHerbivores);
    }

    /**
     * Starts the ecosystem simulation. Initializes predefined ecosystems, handles user selection/creation,
     * processes animal population setup, and executes the simulation loop until extinction occurs.
     */
    public void startSimulation() {
        final List<Ecosystem> ecosystems = getEcosystems();
        try (Scanner input = new Scanner(System.in)) {
            Ecosystem chosenEcosystem = selectOrCreateEcosystem(ecosystems, input);
            int iterationNumber = 1;
            if (isEcosystemChosen(chosenEcosystem)) {
                promptForAnimalCreation(input, chosenEcosystem);
                runSimulationLoop(chosenEcosystem, iterationNumber);
            }
        }
    }

    /**
     * Initializes and returns a predefined list of ecosystems (Savanna, Tundra, Desert)
     * each associated with the shared animal map and required services.
     *
     * @return list of predefined ecosystems
     */
    private List<Ecosystem> getEcosystems() {
        final Ecosystem savanna = new Ecosystem(SAVANNA, ecosystemGroupedAnimals, probabilitiesService, feedingService);
        final Ecosystem tundra = new Ecosystem(TUNDRA, ecosystemGroupedAnimals, probabilitiesService, feedingService);
        final Ecosystem desert = new Ecosystem(DESERT, ecosystemGroupedAnimals, probabilitiesService, feedingService);
        final List<Ecosystem> ecosystems = new ArrayList<>();
        ecosystems.add(savanna);
        ecosystems.add(tundra);
        ecosystems.add(desert);
        return ecosystems;
    }

    /**
     * Prompts the user to repeatedly create animal groups until they indicate completion.
     *
     * @param input           Scanner for reading user input
     * @param chosenEcosystem Ecosystem where animals will be added
     */
    protected void promptForAnimalCreation(Scanner input, Ecosystem chosenEcosystem) {
        boolean isContinueCreation = true;
        while (isContinueCreation) {
            System.out.println("Will you want to add another animal/-s? Y/N");
            if (input.nextLine().equalsIgnoreCase("n")) {
                System.out.println("""
                        --------------------------------------------
                        STARTING SIMULATION
                        --------------------------------------------
                        """);
                isContinueCreation = false;
            } else {
                addAnimalsToEcosystem(chosenEcosystem, input);
            }
        }
    }

    /**
     * Executes the main simulation loop. Each iteration ages animals, processes breeding,
     * and runs lifecycle events until an animal type goes extinct.
     *
     * @param chosenEcosystem Ecosystem being simulated
     * @param iterationNumber Starting iteration count (typically 1)
     */
    protected void runSimulationLoop(Ecosystem chosenEcosystem, int iterationNumber) {
        while (!chosenEcosystem.hasExtinctAnimalType()) {
            System.out.printf("""
                    %n
                    --------------------------------------------
                    Iteration number %d
                    --------------------------------------------
                    %n""", iterationNumber);
            ageAllAnimals(chosenEcosystem);
            processBreeding(chosenEcosystem);
            executeLifecyclePhase(chosenEcosystem);
            iterationNumber++;
        }
    }

    /**
     * Checks if an animal type has gone extinct (no living individuals remain).
     * Terminates the simulation if extinction is detected.
     *
     * @param animalLists Collection of animal groups for the type being checked
     * @param animalType  Animal type (CARNIVORE/HERBIVORE) to evaluate
     */
    protected void checkExtinction(Collection<List<Animal>> animalLists, AnimalType animalType) {
        List<Animal> aliveAnimals = getCurrentAliveAnimals(animalLists);
        if (aliveAnimals.isEmpty()) {
            System.out.println(animalType.toString() + " are extinct. Ending simulation.");
            System.exit(0);
        }
    }

    /**
     * Processes breeding for all animals. Animals breed when their age is divisible
     * by the current iteration number.
     *
     * @param ecosystem Ecosystem containing animals to breed
     */
    protected void processBreeding(Ecosystem ecosystem) {
        Collection<List<Animal>> carnivores = getAnimalGroupsByType(ecosystem, CARNIVORE);
        Collection<List<Animal>> herbivores = getAnimalGroupsByType(ecosystem, HERBIVORE);
        breedAllAnimals(carnivores);
        breedAllAnimals(herbivores);
    }

    /**
     * Ages all animals in the ecosystem by one iteration.
     *
     * @param ecosystem Ecosystem containing animals to age
     */
    protected void ageAllAnimals(Ecosystem ecosystem) {
        Collection<List<Animal>> carnivores = getAnimalGroupsByType(ecosystem, CARNIVORE);
        Collection<List<Animal>> herbivores = getAnimalGroupsByType(ecosystem, HERBIVORE);
        carnivores.forEach(animals -> animals.forEach(animal -> animal.growUp(animal.getCurrentAge())));
        herbivores.forEach(animals -> animals.forEach(animal -> animal.growUp(animal.getCurrentAge())));
    }

    /**
     * Prompts the user for the number of animals to create and attempts to create them
     * using a corresponding factory. Successfully created animals are added to the ecosystem.
     *
     * @param input           the Scanner used for reading user input
     * @param kind            the type of animal to create (e.g., "Zebra", "Lion")
     * @param group           the group name the animal will belong to
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    protected void createMultipleAnimals(Scanner input, String kind, String group, Ecosystem chosenEcosystem) {
        boolean isInvalidInteger = true;
        while (isInvalidInteger) {
            System.out.println("Needed amount (integer): ");
            isInvalidInteger = handleAnimalCreationInput(input, kind, group, chosenEcosystem);
        }
    }

    /**
     * Randomly selects a living animal from the provided collection of animal groups.
     *
     * @param animalGroups Collection of animal groups to select from
     * @return Randomly selected living Animal instance
     */
    protected Animal selectRandomAnimal(Collection<List<Animal>> animalGroups) {
        List<Animal> animals = getCurrentAliveAnimals(animalGroups);
        return animals.get(random.nextInt(animals.size()));
    }

    /**
     * Allows the user to choose an existing ecosystem from a list or create a new one.
     *
     * @param ecosystems the set of available ecosystems
     * @return the ecosystem chosen or created by the user
     */
    protected Ecosystem selectOrCreateEcosystem(List<Ecosystem> ecosystems, Scanner input) {
        Ecosystem pickedEcosystem;
        System.out.println("1 - I will chose ecosystem from the list below");
        System.out.println("2 - I want to create new ecosystem");
        if (input.nextLine().equals("1")) {
            for (int i = 0; i < ecosystems.size(); i++) {
                System.out.println(i + " " + ecosystems.get(i).getBiome());
            }
            pickedEcosystem = pickEcosystem(ecosystems, input.nextInt());
            input.nextLine();
        } else {
            Ecosystem createdEcosystem = createEcosystem(input);
            ecosystems.add(createdEcosystem);
            pickedEcosystem = pickEcosystem(ecosystems, ecosystems.size() - 1);
        }
        return pickedEcosystem;
    }

    /**
     * Creates a new ecosystem by prompting the user to select a biome.
     *
     * @return the newly created Ecosystem instance
     */
    protected Ecosystem createEcosystem(Scanner input) {
        Biome biome;
        System.out.println("Choose biome for new Ecosystem and enter the number of the chosen one below");
        for (int i = 0; i < values().length; i++) {
            System.out.println(i + " " + values()[i]);
        }
        biome = getBiome(input.nextInt());
        input.nextLine();
        return new Ecosystem(biome, ecosystemGroupedAnimals, probabilitiesService, feedingService);
    }

    /**
     * Finds an ecosystem from the given set by biome name.
     *
     * @param ecosystems      the set of available ecosystems
     * @param ecosystemNumber the number of the biome to search for
     * @return the matching Ecosystem instance
     * @throws EcosystemNotFoundException if no ecosystem with the given name is found
     */
    protected Ecosystem pickEcosystem(List<Ecosystem> ecosystems, int ecosystemNumber) {
        return ecosystems.get(ecosystemNumber);
    }

    /**
     * Executes one lifecycle phase of the ecosystem simulation.
     * <p>
     * This phase includes:
     * <ul>
     *     <li>Increasing the hunger level of all carnivores</li>
     *     <li>Checking for extinction of carnivores and herbivores</li>
     *     <li>Performing attacks: each alive carnivore attacks a randomly selected alive herbivore</li>
     * </ul>
     * The method ensures that each carnivore, if alive, attempts an attack.
     * If no herbivores remain, attacks are skipped for the rest of the carnivores.
     *
     * @param ecosystem the ecosystem being processed for this simulation iteration
     */
    private void executeLifecyclePhase(Ecosystem ecosystem) {
        ecosystem.increaseHungerOfCarnivore(ecosystem.getEcosystemGroupedAnimals().get(CARNIVORE));
        Collection<List<Animal>> carnivoreLists = getAnimalGroupsByType(ecosystem, CARNIVORE);
        Collection<List<Animal>> herbivoreLists = getAnimalGroupsByType(ecosystem, HERBIVORE);

        checkExtinction(carnivoreLists, CARNIVORE);
        checkExtinction(herbivoreLists, HERBIVORE);

        for (List<Animal> carnivoreGroup : carnivoreLists) {
            for (Animal carnivore : carnivoreGroup) {
                List<Animal> currentAliveVictims = getCurrentAliveAnimals(herbivoreLists);
                if (currentAliveVictims.isEmpty()) return;
                Animal victim = currentAliveVictims.get(random.nextInt(currentAliveVictims.size()));
                ecosystem.attack(carnivore.getId(), victim.getId());
            }
        }
    }

    /**
     * Retrieves all animal groups of a specific type (e.g., herbivores or carnivores)
     * from the given ecosystem.
     *
     * @param ecosystem the ecosystem containing the animal group data
     * @param type      the {@link AnimalType} (e.g., CARNIVORE or HERBIVORE) whose groups are to be retrieved
     * @return a collection of animal group lists for the specified type
     */
    protected Collection<List<Animal>> getAnimalGroupsByType(Ecosystem ecosystem, AnimalType type) {
        return ecosystem.getEcosystemGroupedAnimals().get(type).values();
    }

    /**
     * Filters and collects all currently alive animals from the provided animal groups.
     *
     * @param animalsList collection of herbivore group lists
     * @return list of alive animals
     */
    private List<Animal> getCurrentAliveAnimals(Collection<List<Animal>> animalsList) {
        return animalsList.stream().flatMap(List::stream).filter(Animal::isAlive).toList();
    }

    /**
     * Breeds animals when their age matches their reproductive rate.
     * Each animal breeds with itself if its age > 0 and divisible by its reproductive rate.
     *
     * @param carnivores Collection of animal lists to process
     */
    private void breedAllAnimals(Collection<List<Animal>> carnivores) {
        carnivores.forEach(animals -> animals.forEach(animal -> {
            if (animal.getCurrentAge() > 0 && animal.getCurrentAge() % animal.getReproductiveRate() == 0) {
                animal.breed(animal);
            }
        }));
    }

    /**
     * Initiates the process of adding animals to a chosen ecosystem.
     * Displays a list of allowed animals and prompts the user to select one or more for creation.
     *
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void addAnimalsToEcosystem(Ecosystem chosenEcosystem, Scanner input) {
        animalCreatorService.printAllowedAnimals(chosenEcosystem.getBiome());
        handleAnimalCreation(input, chosenEcosystem);
    }

    /**
     * Handles the user input loop for creating animals within a chosen ecosystem.
     * Prompts the user to repeatedly create animals until they choose to stop.
     *
     * @param input           the Scanner used for reading user input
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void handleAnimalCreation(Scanner input, Ecosystem chosenEcosystem) {
        System.out.println("Print animal kind: ");
        String kind = input.nextLine();
        System.out.println("Print animal group: ");
        String group = input.nextLine();
        createMultipleAnimals(input, kind, group, chosenEcosystem);
    }

    /**
     * Returns the Biome enum constant matching the provided number of biome
     *
     * @param biomeNumber the number of the biome
     * @return matching provided number biome
     */
    protected Biome getBiome(int biomeNumber) {
        return values()[biomeNumber];
    }

    /**
     * Validates input and creates animals if successful.
     *
     * @return true if input was invalid (retry needed), false if successful.
     */
    private boolean handleAnimalCreationInput(Scanner input, String kind, String group, Ecosystem ecosystem) {
        if (input.hasNextInt()) {
            int amount = input.nextInt();
            input.nextLine();
            animalCreatorService.createAnimals(ecosystem, kind, group, amount);
            return false;
        } else {
            System.out.println("Invalid input! Please enter a valid integer.");
            input.nextLine();
            return true;
        }
    }

    /**
     * Checks whether a valid ecosystem has been chosen.
     *
     * @param ecosystem the ecosystem to check
     * @return {@code true} if the ecosystem is not null, otherwise {@code false}
     */
    private boolean isEcosystemChosen(Ecosystem ecosystem) {
        return ecosystem != null;
    }
}
