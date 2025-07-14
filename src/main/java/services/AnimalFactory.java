package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import static enums.LivingType.GROUP;
import enums.Biome;
import models.Animal;
import models.Carnivore;
import models.Ecosystem;
import models.Herbivore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A factory class for creating and registering animal instances.
 * Provides functionality to create predefined animal types and add them to ecosystems.
 * Supports dynamic registration of new animal types at runtime.
 *
 * @see models.Animal
 * @see models.Ecosystem
 */
public class AnimalFactory {
    private static final String LONERS_GROUP = "Loners";
    private final Map<String, Function<String, Animal>> animals = new HashMap<>();

    /**
     * Constructs an AnimalFactory with predefined animal registrations.
     * Pre-registered herbivores: zebra, hare, gazelle, buffalo.
     * Pre-registered carnivores: lion, cheetah, tiger, hyena.
     * All animals are configured for savanna biome and land habitat.
     */
    public AnimalFactory() {
        //Herbivores
        registerAnimal("zebra", group -> new Herbivore(Set.of(SAVANNA), 0, true, 50, 300, 10, LAND, HERBIVORE, "Zebra", GROUP, true, 80, group));
        registerAnimal("hare", group -> new Herbivore(Set.of(SAVANNA), 0, true, 24, 5, 3, LAND, HERBIVORE, "Hare", ALONE, false, 100, LONERS_GROUP));
        registerAnimal("gazelle", group -> new Herbivore(Set.of(SAVANNA), 0, true, 25, 25, 5, LAND, HERBIVORE, "Gazelle", GROUP, true, 80, group));
        registerAnimal("buffalo", group -> new Herbivore(Set.of(SAVANNA), 0, true, 35, 800, 9, LAND, HERBIVORE, "Buffalo", GROUP, true, 40, group));

        //Carnivores
        registerAnimal("lion", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 150, 6, LAND, CARNIVORE, ALONE, "Lion", true, 110, group, 20));
        registerAnimal("cheetah", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, LONERS_GROUP, 15));
        registerAnimal("tiger", group -> new Carnivore(Set.of(SAVANNA), 0, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "Tiger", false, 75, LONERS_GROUP, 18));
        registerAnimal("hyena", group -> new Carnivore(Set.of(SAVANNA), 0, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "Hyena", true, 80, group, 14));
    }

    /**
     * Creates multiple animals of a specified type and adds them to an ecosystem.
     *
     * @param ecosystem  the target ecosystem to add animals to
     * @param animalKind the type of animal to create (must be pre-registered)
     * @param groupName  the name of the animal group (use {@code LONERS_GROUP} for solitary animals)
     * @param count      the number of animals to create
     * @throws NullPointerException if the animal kind is not registered
     */
    public void createAnimals(Ecosystem ecosystem, String animalKind, String groupName, int count) {
        Function<String, Animal> animalFunction = animals.get(animalKind.toLowerCase());
        for (int i = 0; i < count; i++) {
            ecosystem.addAnimalToEcosystem(animalFunction.apply(groupName));
        }
    }

    /**
     * Prints all registered animals that can live in the given biome.
     *
     * @param currentBiome the biome to filter animals by (e.g., SAVANNA, TUNDRA)
     */
    public void printAllowedAnimals(Biome currentBiome) {
        System.out.println("Which animal to create? Pick from the list below: ");
        animals.forEach((animalKind, animalFunction) -> {
            Animal sampleAnimal = animalFunction.apply("");
            if (sampleAnimal.getBiomes().contains(currentBiome)) {
                System.out.println(animalKind);
            }
        });
    }

    /**
     * Registers a new animal type with the factory.
     * The animal kind is case-insensitive (converted to lowercase internally).
     *
     * @param kind     the name of the animal type to register (e.g., "zebra")
     * @param function the creation function that accepts a group name and returns
     *                 a configured {@link models.Animal} instance
     */
    private void registerAnimal(String kind, Function<String, Animal> function) {
        animals.put(kind.toLowerCase(), function);
    }
}
