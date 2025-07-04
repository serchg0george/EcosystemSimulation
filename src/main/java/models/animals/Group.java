package models.animals;

import enums.CarnivoreKind;

import java.util.List;

public class Group {
    private final String groupName;
    private final List<Animal> groupedAnimals;

    public Group(String groupName, List<Animal> groupedAnimals) {
        this.groupName = groupName;
        this.groupedAnimals = groupedAnimals;
    }

    public List<Animal> getGroupedAnimals() {
        return groupedAnimals;
    }

    public String getGroupName() {
        return groupName;
    }
}
