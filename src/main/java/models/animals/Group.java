package models.animals;

import java.util.List;

public class Group {
    private final String groupName;
    private final List<Animal> groupedAnimals;

    public Group(String groupName, List<Animal> groupedAnimals) {
        this.groupName = groupName;
        this.groupedAnimals = groupedAnimals;
    }

    private void addToGroup(Animal animal) {
        groupedAnimals.add(animal);
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Animal> getGroup() {
        return groupedAnimals;
    }
}
