package models.ecosystems;

import enums.Biome;
import models.animals.Animal;
import models.animals.Group;

import java.util.List;
import java.util.Map;

public class Savanna {
    private final String ecosystemName;
    private final Biome biome;
    private final List<Animal> ecosystemAnimals;
    private final Map<String, List<Group>> groupsMembers;

    public Savanna(String ecosystemName, Biome biome, List<Animal> ecosystemAnimals, Map<String, List<Group>> groupsMembers) {
        this.ecosystemName = ecosystemName;
        this.biome = biome;
        this.ecosystemAnimals = ecosystemAnimals;
        this.groupsMembers = groupsMembers;
    }

    private void attack() {
    }

    private void growUp() {

    }

    private Animal breed(Animal animal) {
        return null;
    }

    private boolean isEscapedFromPredator() {
        return false;
    }

    public String getEcosystemName() {
        return ecosystemName;
    }

    public Biome getBiome() {
        return biome;
    }

    public List<Animal> getEcosystemAnimals() {
        return ecosystemAnimals;
    }

    public Map<String, List<Group>> getGroupsMembers() {
        return groupsMembers;
    }
}
