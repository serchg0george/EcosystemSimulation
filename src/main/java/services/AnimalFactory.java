package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.*;
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
 * A factory class for creating and registering {@link Animal} instances.
 * <p>
 * This factory predefines a wide range of herbivores and carnivores adapted to various biomes
 * including {@link Biome#SAVANNA}, {@link Biome#TUNDRA}, {@link Biome#TROPICAL_FOREST}, and {@link Biome#DESERT}.
 * Each animal is assigned its main habitat (e.g., {@link enums.Habitat#LAND}),
 * reproduction and combat parameters, and social behavior type (e.g., {@link enums.LivingType#GROUP}).
 * <p>
 * The factory supports:
 * <ul>
 *   <li>Creation of animals by kind and group</li>
 *   <li>Dynamic registration of new animal types at runtime</li>
 *   <li>Filtering of available animals by biome</li>
 * </ul>
 *
 * <p><b>Predefined animals:</b>
 * <ul>
 *   <li><b>Herbivores:</b> zebra, hare, gazelle, buffalo, reindeer, lemming, monkey, tapir, camel, jerboa, boar</li>
 *   <li><b>Carnivores:</b> lion, cheetah, tiger, hyena, arctic_fox, snowy_owl, jaguar, ocelot, fennec_fox, caracal, wild_dog</li>
 * </ul>
 *
 * Many of the animals inhabit multiple biomes, reflecting adaptive traits. For example, the {@code reindeer}
 * lives in both tundra and desert, while the {@code wild_dog} is registered across all four biomes.
 *
 * @see Animal
 * @see Carnivore
 * @see Herbivore
 * @see Ecosystem
 * @see Biome
 */
public class AnimalFactory {
    private static final String LONERS_GROUP = "Loners";
    private final Map<String, Function<String, Animal>> animals = new HashMap<>();

    /**
     * Constructs an AnimalFactory and registers all predefined animals
     * across various biomes and habitats.
     * <p>
     * Animals are registered for the following biomes: savanna, tundra,
     * tropical forest, desert, and also as universal species adaptable
     * to multiple biomes.
     * <p>
     * Both herbivores and carnivores are included, configured with appropriate
     * traits such as age, weight, speed, group behavior, and hunting style.
     */
    public AnimalFactory() {
        registerAllAnimals();
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
     * Registers all animals across different biomes.
     * This method delegates to specialized registration methods for each biome,
     * ensuring animals are correctly initialized according to their habitat.
     */
    private void registerAllAnimals() {
        registerSavannaAnimals();
        registerTundraAnimals();
        registerTropicalForestAnimals();
        registerDesertAnimals();
        registerUniversalAnimals();
    }

    /**
     * Registers animals which may live in all of the {@link Biome} biomes.
     */
    private void registerUniversalAnimals() {
        //herbivore
        registerAnimal("boar", group -> new Herbivore(Set.of(SAVANNA, TUNDRA, TROPICAL_FOREST, DESERT), 0, true, 25, 100, 4, LAND, HERBIVORE, "Boar", GROUP, true, 70, group));

        //carnivore
        registerAnimal("wild_dog", group -> new Carnivore(Set.of(SAVANNA, TUNDRA, TROPICAL_FOREST, DESERT), 0, true, 20, 20, 4, LAND, CARNIVORE, GROUP, "Wild Dog", true, 75, group, 13));
    }

    /**
     * Registers animals specific to the {@link Biome#DESERT} biome.
     */
    private void registerDesertAnimals() {
        //herbivore
        registerAnimal("camel", group -> new Herbivore(Set.of(DESERT, SAVANNA), 0, true, 40, 600, 4, LAND, HERBIVORE, "Camel", GROUP, true, 70, group));
        registerAnimal("jerboa", group -> new Herbivore(Set.of(DESERT), 0, true, 24, 2, 2, LAND, HERBIVORE, "Jerboa", ALONE, false, 100, LONERS_GROUP));

        //carnivore
        registerAnimal("fennec_fox", group -> new Carnivore(Set.of(DESERT, TUNDRA), 0, true, 24, 1, 3, LAND, CARNIVORE, ALONE, "Fennec Fox", false, 90, LONERS_GROUP, 9));
        registerAnimal("caracal", group -> new Carnivore(Set.of(DESERT, SAVANNA), 0, true, 22, 15, 4, LAND, CARNIVORE, ALONE, "Caracal", false, 80, LONERS_GROUP, 12));
    }

    /**
     * Registers animals specific to the {@link Biome#TROPICAL_FOREST} biome.
     */
    private void registerTropicalForestAnimals() {
        //herbivore
        registerAnimal("monkey", group -> new Herbivore(Set.of(TROPICAL_FOREST), 0, true, 28, 15, 5, LAND, HERBIVORE, "Monkey", GROUP, true, 85, group));
        registerAnimal("tapir", group -> new Herbivore(Set.of(TROPICAL_FOREST, SAVANNA), 0, true, 26, 250, 5, LAND, HERBIVORE, "Tapir", ALONE, false, 75, LONERS_GROUP));

        //carnivore
        registerAnimal("jaguar", group -> new Carnivore(Set.of(TROPICAL_FOREST), 0, true, 20, 100, 5, LAND, CARNIVORE, ALONE, "Jaguar", false, 85, LONERS_GROUP, 16));
        registerAnimal("ocelot", group -> new Carnivore(Set.of(TROPICAL_FOREST, TUNDRA), 0, true, 22, 10, 4, LAND, CARNIVORE, ALONE, "Ocelot", false, 80, LONERS_GROUP, 12));

    }

    /**
     * Registers animals specific to the {@link Biome#TUNDRA} biome.
     */
    private void registerTundraAnimals() {
        //herbivore
        registerAnimal("reindeer", group -> new Herbivore(Set.of(TUNDRA, DESERT), 0, true, 22, 180, 6, LAND, HERBIVORE, "Reindeer", GROUP, true, 90, group));
        registerAnimal("lemming", group -> new Herbivore(Set.of(TUNDRA), 0, true, 24, 1, 2, LAND, HERBIVORE, "Lemming", ALONE, false, 100, LONERS_GROUP));

        //carnivore
        registerAnimal("arctic_fox", group -> new Carnivore(Set.of(TUNDRA), 0, true, 20, 5, 4, LAND, CARNIVORE, ALONE, "Arctic Fox", false, 95, LONERS_GROUP, 10));
        registerAnimal("snowy_owl", group -> new Carnivore(Set.of(TUNDRA, SAVANNA), 0, true, 18, 3, 3, LAND, CARNIVORE, ALONE, "Snowy Owl", false, 90, LONERS_GROUP, 9));
    }

    /**
     * Registers animals specific to the {@link Biome#SAVANNA} biome.
     */
    private void registerSavannaAnimals() {
        //herbivore
        registerAnimal("zebra", group -> new Herbivore(Set.of(SAVANNA), 0, true, 50, 300, 10, LAND, HERBIVORE, "Zebra", GROUP, true, 80, group));
        registerAnimal("hare", group -> new Herbivore(Set.of(SAVANNA), 0, true, 24, 5, 3, LAND, HERBIVORE, "Hare", ALONE, false, 100, LONERS_GROUP));
        registerAnimal("gazelle", group -> new Herbivore(Set.of(SAVANNA), 0, true, 25, 25, 5, LAND, HERBIVORE, "Gazelle", GROUP, true, 80, group));
        registerAnimal("buffalo", group -> new Herbivore(Set.of(SAVANNA), 0, true, 35, 800, 9, LAND, HERBIVORE, "Buffalo", GROUP, true, 40, group));

        //carnivore
        registerAnimal("lion", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 150, 6, LAND, CARNIVORE, ALONE, "Lion", true, 110, group, 20));
        registerAnimal("cheetah", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, LONERS_GROUP, 15));
        registerAnimal("tiger", group -> new Carnivore(Set.of(SAVANNA), 0, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "Tiger", false, 75, LONERS_GROUP, 18));
        registerAnimal("hyena", group -> new Carnivore(Set.of(SAVANNA), 0, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "Hyena", true, 80, group, 14));

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
