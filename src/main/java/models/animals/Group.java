package models.animals;

import enums.CarnivoreKind;

import java.util.List;
import java.util.Map;

public class Group {
    private final Map<String, List<Animal>> groupedAnimals;

    public Group(Map<String, List<Animal>> groupedAnimals) {
        this.groupedAnimals = groupedAnimals;
    }

    public void addNewMember(Animal animal) {
        if (isAddLonerCarnivoreSucceed(animal)) return;
        if (isAddLonerHerbivoreSucceed(animal)) return;
        List<Animal> animals = groupedAnimals.get(animal.getGroupName());
        animals.add(animal);
        groupedAnimals.put(animal.getGroupName(), animals);
    }

    private boolean isAddLonerHerbivoreSucceed(Animal animal) {
        if (!isCarnivore(animal) && !animal.isInGroup()) {
            List<Animal> lonerPredators = groupedAnimals.get("Loner carnivores");
            lonerPredators.add(animal);
            return true;
        }
        return false;
    }

    private boolean isAddLonerCarnivoreSucceed(Animal animal) {
        if (isCarnivore(animal) && !animal.isInGroup()) {
            List<Animal> lonerPredators = groupedAnimals.get("Loner carnivores");
            lonerPredators.add(animal);
            return true;
        }
        return false;
    }

    private boolean isCarnivore(Animal animal) {
        return animal.getAnimalKind() instanceof CarnivoreKind;
    }

    public Map<String, List<Animal>> getGroupedAnimals() {
        return groupedAnimals;
    }
}
