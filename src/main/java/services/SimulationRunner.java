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
    private final AnimalFactory animalFactory;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals = new EnumMap<>(AnimalType.class);
    private final Random random = new Random();

    public SimulationRunner(ProbabilitiesService probabilitiesService, AnimalFactory animalFactory) {
        this.probabilitiesService = probabilitiesService;
        this.animalFactory = animalFactory;
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
        final Ecosystem savanna = new Ecosystem(SAVANNA, ecosystemGroupedAnimals, probabilitiesService);
        final Ecosystem tundra = new Ecosystem(TUNDRA, ecosystemGroupedAnimals, probabilitiesService);
        final Ecosystem desert = new Ecosystem(DESERT, ecosystemGroupedAnimals, probabilitiesService);
        final List<Ecosystem> ecosystems = new ArrayList<>();
        ecosystems.add(savanna);
        ecosystems.add(tundra);
        ecosystems.add(desert);
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
     * Creates a new ecosystem by prompting the user to select a biome.
     *
     * @return the newly created Ecosystem instance
     */
    protected Ecosystem createEcosystem() {
        try (Scanner input = new Scanner(System.in)) {
            Biome biome;
            System.out.println("Choose biome for new Ecosystem and enter the number of the chosen one below");
            for (int i = 0; i < values().length; i++) {
                System.out.println(i + " " + values()[i]);
            }
            biome = getBiome(input.nextInt());
            return new Ecosystem(biome, ecosystemGroupedAnimals, probabilitiesService);
        }
    }

    /**
     * Prompts the user to repeatedly create animal groups until they indicate completion.
     *
     * @param input           Scanner for reading user input
     * @param chosenEcosystem Ecosystem where animals will be added
     */
    private void promptForAnimalCreation(Scanner input, Ecosystem chosenEcosystem) {
        boolean isContinueCreation = true;
        while (isContinueCreation) {
            System.out.println("Will you want to create another animal/-s? Y/N");
            if (input.nextLine().equalsIgnoreCase("n")) {
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
    private void runSimulationLoop(Ecosystem chosenEcosystem, int iterationNumber) {
        while (!chosenEcosystem.hasExtinctAnimalType()) {
            System.out.println("number " + iterationNumber);
            ageAllAnimals(chosenEcosystem);
            processBreeding(chosenEcosystem, iterationNumber);
            executeLifecyclePhase(chosenEcosystem);
            iterationNumber++;
        }
    }

    /**
     * Executes one lifecycle phase: Increases carnivore hunger, checks for extinction events,
     * and triggers a random attack between carnivores and herbivores.
     *
     * @param ecosystem Ecosystem to process
     */
    private void executeLifecyclePhase(Ecosystem ecosystem) {
        ecosystem.increaseHungerOfCarnivore(ecosystem.getEcosystemGroupedAnimals().get(CARNIVORE));
        Collection<List<Animal>> carnivoreLists = getCarnivoreGroups(ecosystem);
        Collection<List<Animal>> herbivoreLists = getHerbivoreGroups(ecosystem);

        checkExtinction(carnivoreLists, CARNIVORE);
        checkExtinction(herbivoreLists, HERBIVORE);

        long attackerId = selectRandomAnimal(carnivoreLists).getId();
        long victimId = selectRandomAnimal(herbivoreLists).getId();
        ecosystem.attack(attackerId, victimId);
    }

    /**
     * Checks if an animal type has gone extinct (no living individuals remain).
     * Terminates the simulation if extinction is detected.
     *
     * @param animalLists Collection of animal groups for the type being checked
     * @param animalType  Animal type (CARNIVORE/HERBIVORE) to evaluate
     */
    private void checkExtinction(Collection<List<Animal>> animalLists, AnimalType animalType) {
        List<Animal> aliveAnimals = animalLists.stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(Animal::isAlive)
                .toList();

        if (aliveAnimals.isEmpty()) {
            System.out.println(animalType.toString() + " are extinct. Ending simulation.");
            System.exit(0);
        }
    }

    /**
     * Randomly selects a living animal from the provided collection of animal groups.
     *
     * @param animalGroups Collection of animal groups to select from
     * @return Randomly selected living Animal instance
     */
    private Animal selectRandomAnimal(Collection<List<Animal>> animalGroups) {
        List<Animal> animals = animalGroups.stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(Animal::isAlive)
                .toList();

        return animals.get(random.nextInt(animals.size()));
    }

    /**
     * Processes breeding for all animals. Animals breed when their age is divisible
     * by the current iteration number.
     *
     * @param ecosystem       Ecosystem containing animals to breed
     * @param iterationNumber Current simulation iteration (used for breeding condition)
     */
    private void processBreeding(Ecosystem ecosystem, int iterationNumber) {
        Collection<List<Animal>> carnivores = getCarnivoreGroups(ecosystem);
        Collection<List<Animal>> herbivores = getHerbivoreGroups(ecosystem);

        carnivores.forEach(animals ->
                animals.forEach(animal -> {
                    if (animal.getCurrentAge() % iterationNumber == 0) {
                        animal.breed(animal);
                    }
                }));

        herbivores.forEach(animals ->
                animals.forEach(animal -> {
                    if (animal.getCurrentAge() % iterationNumber == 0) {
                        animal.breed(animal);
                    }
                }));
    }

    /**
     * Ages all animals in the ecosystem by one iteration.
     *
     * @param ecosystem Ecosystem containing animals to age
     */
    private void ageAllAnimals(Ecosystem ecosystem) {
        Collection<List<Animal>> carnivores = getCarnivoreGroups(ecosystem);
        Collection<List<Animal>> herbivores = getHerbivoreGroups(ecosystem);

        carnivores.forEach(animals ->
                animals.forEach(animal ->
                        animal.growUp(animal.getCurrentAge())));

        herbivores.forEach(animals ->
                animals.forEach(animal ->
                        animal.growUp(animal.getCurrentAge())));
    }

    /**
     * Retrieves all herbivore groups from the ecosystem.
     *
     * @param ecosystem Ecosystem to query
     * @return Collection of herbivore group lists
     */
    private Collection<List<Animal>> getHerbivoreGroups(Ecosystem ecosystem) {
        return ecosystem.getEcosystemGroupedAnimals().get(HERBIVORE).values();
    }

    /**
     * Retrieves all carnivore groups from the ecosystem.
     *
     * @param ecosystem Ecosystem to query
     * @return Collection of carnivore group lists
     */
    private Collection<List<Animal>> getCarnivoreGroups(Ecosystem ecosystem) {
        return ecosystem.getEcosystemGroupedAnimals().get(CARNIVORE).values();
    }

    /**
     * Initiates the process of adding animals to a chosen ecosystem.
     * Displays a list of allowed animals and prompts the user to select one or more for creation.
     *
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void addAnimalsToEcosystem(Ecosystem chosenEcosystem, Scanner input) {
        final List<String> allowedAnimals = List.of("Zebra", "Hare", "Gazelle", "Buffalo",
                "Lion", "Cheetah", "Tiger", "Hyena");
        System.out.println("Which animal to create? Pick from the list below: ");
        allowedAnimals.forEach(System.out::println);
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
     * Prompts the user for the number of animals to create and attempts to create them
     * using a corresponding factory. Successfully created animals are added to the ecosystem.
     *
     * @param input           the Scanner used for reading user input
     * @param kind            the type of animal to create (e.g., "Zebra", "Lion")
     * @param group           the group name the animal will belong to
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void createMultipleAnimals(Scanner input, String kind, String group, Ecosystem chosenEcosystem) {
        boolean isInvalidInteger = true;
        while (isInvalidInteger) {
            System.out.println("Needed amount (integer): ");
            if (input.hasNextInt()) {
                int amount = input.nextInt();
                input.nextLine();
                animalFactory.createAnimals(chosenEcosystem, kind, group, amount);
                isInvalidInteger = false;
            } else {
                System.out.println("Invalid input! Please enter a valid integer.");
                input.nextLine();
            }
        }
    }

    /**
     * Allows the user to choose an existing ecosystem from a list or create a new one.
     *
     * @param ecosystems the set of available ecosystems
     * @return the ecosystem chosen or created by the user
     */
    private Ecosystem selectOrCreateEcosystem(List<Ecosystem> ecosystems, Scanner input) {
        Ecosystem pickedEcosystem = ecosystems.stream().findFirst().orElseThrow();
        System.out.println("1 - I will chose ecosystem from the list below");
        System.out.println("2 - I want to create new ecosystem");
        if (input.nextLine().equals("1")) {
            for (int i = 0; i < ecosystems.size(); i++) {
                System.out.println(i + " " + ecosystems.get(i).getBiome());
            }
            pickedEcosystem = pickEcosystem(ecosystems, input.nextInt());
            input.nextLine();
        } else if (input.nextLine().equals("2")) {
            Ecosystem createdEcosystem = createEcosystem();
            ecosystems.add(createdEcosystem);
            pickedEcosystem = pickEcosystem(ecosystems, ecosystems.size() - 1);
        }
        return pickedEcosystem;
    }

    /**
     * Finds an ecosystem from the given set by biome name.
     *
     * @param ecosystems      the set of available ecosystems
     * @param ecosystemNumber the number of the biome to search for
     * @return the matching Ecosystem instance
     * @throws EcosystemNotFoundException if no ecosystem with the given name is found
     */
    private Ecosystem pickEcosystem(List<Ecosystem> ecosystems, int ecosystemNumber) {
        return ecosystems.get(ecosystemNumber);
    }

    /**
     * Returns the Biome enum constant matching the provided number of biome
     *
     * @param biomeNumber the number of the biome
     * @return matching provided number biome
     */
    private Biome getBiome(int biomeNumber) {
        return values()[biomeNumber];
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
