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

public class SimulationRunner {
    private final ProbabilitiesService probabilitiesService;
    private final AnimalFactory animalFactory;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals = new EnumMap<>(AnimalType.class);

    public SimulationRunner(ProbabilitiesService probabilitiesService, AnimalFactory animalFactory) {
        this.probabilitiesService = probabilitiesService;
        this.animalFactory = animalFactory;
        Map<String, List<Animal>> lonerCarnivores = new HashMap<>();
        Map<String, List<Animal>> lonerHerbivores = new HashMap<>();
        ecosystemGroupedAnimals.put(CARNIVORE, lonerCarnivores);
        ecosystemGroupedAnimals.put(HERBIVORE, lonerHerbivores);
    }

    public void startSimulation() {
        final Ecosystem savanna = new Ecosystem(SAVANNA, ecosystemGroupedAnimals, probabilitiesService);
        final Ecosystem tundra = new Ecosystem(TUNDRA, ecosystemGroupedAnimals, probabilitiesService);
        final Ecosystem desert = new Ecosystem(DESERT, ecosystemGroupedAnimals, probabilitiesService);
        final Set<Ecosystem> ecosystems = new HashSet<>();
        ecosystems.add(savanna);
        ecosystems.add(tundra);
        ecosystems.add(desert);
        Ecosystem chosenEcosystem = choseEcosystem(ecosystems);
        if (isEcosystemChosen(chosenEcosystem)) {
            addAnimalsToEcosystem(chosenEcosystem); //TODO in ecosystem while removing animal from ecosystem need to check if this animal last in its group and remove group in case it is
            while (chosenEcosystem.isContainAnimals(chosenEcosystem)) {
                runTurnOfLifecycle(chosenEcosystem);
            }
        }
    }

    private void runTurnOfLifecycle(Ecosystem ecosystem) {
        //TODO here I should implement logic of attacking onto my herbivores and need to check
        // how hungriness increasing works. Also I need to make sure that animals will be removed
        // from ecosystem

    }

    /**
     * Initiates the process of adding animals to a chosen ecosystem.
     * Displays a list of allowed animals and prompts the user to select one or more for creation.
     *
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void addAnimalsToEcosystem(Ecosystem chosenEcosystem) {
        final List<String> allowedAnimals = List.of("Zebra", "Hare", "Gazelle", "Buffalo",
                "Lion", "Cheetah", "Tiger", "Hyena");
        System.out.println("Which animal to create? Pick from the list below: ");
        allowedAnimals.forEach(System.out::println);
        try (Scanner input = new Scanner(System.in)) {
            handleAnimalCreation(input, chosenEcosystem);
        }
    }

    /**
     * Handles the user input loop for creating animals within a chosen ecosystem.
     * Prompts the user to repeatedly create animals until they choose to stop.
     *
     * @param input           the Scanner used for reading user input
     * @param chosenEcosystem the ecosystem to which animals will be added
     */
    private void handleAnimalCreation(Scanner input, Ecosystem chosenEcosystem) {
        while (true) {
            System.out.println("Will you want to create another animal/-s? Y/N");
            if (input.nextLine().equalsIgnoreCase("n")) {
                break;
            }
            System.out.println("Print animal kind: ");
            String kind = input.nextLine();
            System.out.println("Print animal group: ");
            String group = input.nextLine();
            createMultipleAnimals(input, kind, group, chosenEcosystem);
        }
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
    private Ecosystem choseEcosystem(Set<Ecosystem> ecosystems) {
        try (Scanner input = new Scanner(System.in)) {
            Ecosystem pickedEcosystem = null;
            System.out.println("1 - I will chose ecosystem from the list below");
            System.out.println("2 - I want to create new ecosystem");
            if (input.nextLine().equalsIgnoreCase("1")) {
                ecosystems.forEach(System.out::println);
                pickedEcosystem = pickEcosystem(ecosystems, input.nextLine());
            }
            if (input.nextLine().equalsIgnoreCase("2")) {
                Ecosystem createdEcosystem = createEcosystem();
                ecosystems.add(createdEcosystem);
                pickedEcosystem = pickEcosystem(ecosystems, createdEcosystem.getBiome().name());
            }
            return pickedEcosystem;
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
     * Finds an ecosystem from the given set by biome name.
     *
     * @param ecosystems the set of available ecosystems
     * @param name       the name of the biome to search for
     * @return the matching Ecosystem instance
     * @throws EcosystemNotFoundException if no ecosystem with the given name is found
     */
    private Ecosystem pickEcosystem(Set<Ecosystem> ecosystems, String name) {
        return ecosystems.stream()
                .filter(ecosystem -> ecosystem.getBiome().name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new EcosystemNotFoundException("Ecosystem with provided name: " + name + " not found"));
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
