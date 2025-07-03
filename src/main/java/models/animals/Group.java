package models.animals;

import java.util.List;
import java.util.Map;

public class Group {
    private final Map<String, List<Animal>> groupedAnimals;

    public Group(Map<String, List<Animal>> groupedAnimals) {
        this.groupedAnimals = groupedAnimals;
    }

    public void addNewMember(Animal animal) {
        List<Animal> animals = groupedAnimals.get(animal.getGroupName());
        animals.add(animal);
        groupedAnimals.put(animal.getGroupName(), animals);
    }

    public Map<String, List<Animal>> getGroupedAnimals() {
        return groupedAnimals;
    }
}
