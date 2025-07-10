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
import java.util.function.Supplier;

public class AnimalFactory {
    private static final String LONERS_GROUP = "Loners";
    private final Map<String, Supplier<Animal>> animals = new HashMap<>();

    public void createAnimals(Ecosystem ecosystem, String animalKind, String groupName, int count) {
        fillAnimals(groupName);
        Supplier<Animal> animalSupplier = animals.get(animalKind.toLowerCase());
        for (int i = 0; i < count; i++) {
            ecosystem.addAnimalToEcosystem(animalSupplier.get());
        }
    }

    private void fillAnimals(String groupName) {

        //Herbivores
        animals.put("zebra", () -> new Herbivore(Set.of(SAVANNA), 0, true, 50, 300, 10, LAND, HERBIVORE, "Zebra", GROUP, true, 80, groupName));
        animals.put("hare", () -> new Herbivore(Set.of(SAVANNA), 0, true, 24, 5, 3, LAND, HERBIVORE, "Hare", ALONE, false, 100, LONERS_GROUP));
        animals.put("gazelle", () -> new Herbivore(Set.of(SAVANNA), 0, true, 25, 25, 5, LAND, HERBIVORE, "Gazelle", GROUP, true, 80, groupName));
        animals.put("buffalo", () -> new Herbivore(Set.of(SAVANNA), 0, true, 35, 800, 9, LAND, HERBIVORE, "Buffalo", GROUP, true, 40, groupName));

        //Carnivores
        animals.put("lion", () -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 150, 6, LAND, CARNIVORE, ALONE, "Lion", false, 110, LONERS_GROUP, 20));
        animals.put("cheetah", () -> new Carnivore(Set.of(SAVANNA), 0, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, LONERS_GROUP, 15));
        animals.put("tiger", () -> new Carnivore(Set.of(SAVANNA), 0, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "Tiger", false, 75, LONERS_GROUP, 18));
        animals.put("hyena", () -> new Carnivore(Set.of(SAVANNA), 0, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "Hyena", true, 80, groupName, 14));
    }
}
