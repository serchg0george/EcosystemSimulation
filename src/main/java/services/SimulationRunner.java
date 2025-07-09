package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.*;
import enums.AnimalType;
import enums.Biome;
import exceptions.EcosystemNotFoundException;
import factory.AnimalFactory;
import factory.carnivores.CheetahFactory;
import factory.carnivores.HyenaFactory;
import factory.carnivores.LionFactory;
import factory.carnivores.TigerFactory;
import factory.herbivores.BuffaloFactory;
import factory.herbivores.GazelleFactory;
import factory.herbivores.HareFactory;
import factory.herbivores.ZebraFactory;
import models.Animal;
import models.Ecosystem;

import java.util.*;

public class SimulationRunner {
    private final ProbabilitiesService probabilitiesService;
    private final List<Animal> groups = new ArrayList<>();
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals = new HashMap<>();
    AnimalFactory zebraFactory = new ZebraFactory();
    AnimalFactory hareFactory = new HareFactory();
    AnimalFactory gazelleFactory = new GazelleFactory();
    AnimalFactory buffaloFactory = new BuffaloFactory();
    AnimalFactory lionFactory = new LionFactory();
    AnimalFactory cheetahFactory = new CheetahFactory();
    AnimalFactory tigerFactory = new TigerFactory();
    AnimalFactory hyenaFactory = new HyenaFactory();

    public SimulationRunner(ProbabilitiesService probabilitiesService) {
        this.probabilitiesService = probabilitiesService;
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
        while (true) {
            System.out.println("Needed amount (integer): ");
            if (input.hasNextInt()) {
                int amount = input.nextInt();
                input.nextLine();
                for (int i = 0; i < amount; i++) {
                    Optional<Animal> createdAnimal = callFactory(kind, group);
                    if (createdAnimal.isEmpty()) {
                        System.out.println("Creation failed!");
                        continue;
                    }
                    chosenEcosystem.addAnimalToEcosystem(createdAnimal.get());
                }
                break;
            } else {
                System.out.println("Invalid input! Please enter a valid integer.");
                input.nextLine();
            }
        }
    }

    /**
     * Calls the appropriate animal factory based on the animal kind and returns an Optional
     * containing the created animal if successful.
     *
     * @param kind  the type of animal to create
     * @param group the group name the animal will belong to
     * @return an Optional containing the created Animal or an empty Optional if creation failed
     */
    private Optional<Animal> callFactory(String kind, String group) {
        if (kind.equalsIgnoreCase("zebra")) {
            return Optional.of(zebraFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("hare")) {
            return Optional.of(hareFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("gazelle")) {
            return Optional.of(gazelleFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("buffalo")) {
            return Optional.of(buffaloFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("lion")) {
            return Optional.of(lionFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("cheetah")) {
            return Optional.of(cheetahFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("tiger")) {
            return Optional.of(tigerFactory.create(kind, group));
        }
        if (kind.equalsIgnoreCase("hyena")) {
            return Optional.of(hyenaFactory.create(kind, group));
        }
        return Optional.empty();
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
            System.out.println("Choose biome of new Ecosystem and enter chosen one below");
            for (Biome b : values()) {
                System.out.println(b.name());
            }
            biome = getBiomeFromStringOrDefault(input.nextLine());
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
     * Returns the Biome enum constant matching the provided string, or the default {@code SAVANNA}
     * if no match is found.
     *
     * @param biome the name of the biome to parse
     * @return the corresponding Biome enum value or {@code SAVANNA} if not recognized
     */
    private Biome getBiomeFromStringOrDefault(String biome) {
        for (Biome b : values()) {
            if (b.name().equalsIgnoreCase(biome)) {
                return b;
            }
        }
        return SAVANNA;
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
