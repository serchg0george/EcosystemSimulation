package models;

import java.util.List;

public class Group {
    private final String groupName;
    private List<Animal> groupedAnimals;

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

    public void setGroupedAnimals(List<Animal> groupedAnimals) {
        this.groupedAnimals = groupedAnimals;
    }
}
