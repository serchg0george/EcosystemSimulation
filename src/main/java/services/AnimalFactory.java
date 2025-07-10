package services;

import static enums.AnimalType.CARNIVORE;
import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import static enums.LivingType.GROUP;
import models.Animal;
import models.Carnivore;
import models.Ecosystem;
import models.Herbivore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class AnimalFactory {
    private static final String LONERS_GROUP = "Loners";
    private final Map<String, Function<String, Animal>> animals = new HashMap<>();

    public AnimalFactory() {
        registerAnimal("zebra", group -> new Herbivore(Set.of(SAVANNA), 0, true, 50, 300, 10, LAND, HERBIVORE, "Zebra", GROUP, true, 80, group));
        registerAnimal("hare", group -> new Herbivore(Set.of(SAVANNA), 0, true, 24, 5, 3, LAND, HERBIVORE, "Hare", ALONE, false, 100, LONERS_GROUP));
        registerAnimal("gazelle", group -> new Herbivore(Set.of(SAVANNA), 0, true, 25, 25, 5, LAND, HERBIVORE, "Gazelle", GROUP, true, 80, group));
        registerAnimal("buffalo", group -> new Herbivore(Set.of(SAVANNA), 0, true, 35, 800, 9, LAND, HERBIVORE, "Buffalo", GROUP, true, 40, group));

        registerAnimal("lion", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 150, 6, LAND, CARNIVORE, ALONE, "Lion", false, 110, group, 20));
        registerAnimal("cheetah", group -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, LONERS_GROUP, 15));
        registerAnimal("tiger", group -> new Carnivore(Set.of(SAVANNA), 0, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "Tiger", false, 75, LONERS_GROUP, 18));
        registerAnimal("hyena", group -> new Carnivore(Set.of(SAVANNA), 0, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "Hyena", true, 80, group, 14));
    }

    public void registerAnimal(String kind, Function<String, Animal> function) {
        animals.put(kind.toLowerCase(), function);
    }

    public void createAnimals(Ecosystem ecosystem, String animalKind, String groupName, int count) {
        Function<String, Animal> animalFunction = animals.get(animalKind.toLowerCase());
        for (int i = 0; i < count; i++) {
            ecosystem.addAnimalToEcosystem(animalFunction.apply(groupName));
        }
    }
}
