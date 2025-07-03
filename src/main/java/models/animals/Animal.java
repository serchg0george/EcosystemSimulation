package models.animals;

import enums.*;

import java.util.Set;

public abstract class Animal {
    private final int maxAge;
    private final double weight;
    private final int reproductiveRate;
    private final Habitat mainHabitat;
    private final AnimalType animalType;
    private final LivingType livingType;
    private final Set<Biome> biomes;
    private final AnimalKind animalKind;
    private int currentAge;
    private boolean isAlive;
    private boolean isInGroup;
    private String groupName;

    public Animal(Set<Biome> biomes,
                  int currentAge,
                  boolean isAlive,
                  int maxAge,
                  double weight,
                  int reproductiveRate,
                  Habitat mainHabitat,
                  AnimalType animalType,
                  LivingType livingType,
                  AnimalKind animalKind,
                  boolean isInGroup,
                  String groupName) {

        this.biomes = biomes;
        this.currentAge = currentAge;
        this.isAlive = isAlive;
        this.maxAge = maxAge;
        this.weight = weight;
        this.reproductiveRate = reproductiveRate;
        this.mainHabitat = mainHabitat;
        this.animalType = animalType;
        this.livingType = livingType;
        this.animalKind = animalKind;
        this.isInGroup = isInGroup;
        this.groupName = groupName;
    }

    protected abstract Animal breed(Animal animal);

    protected int growUp(int currentAge) {
        return ++currentAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public double getWeight() {
        return weight;
    }

    public int getReproductiveRate() {
        return reproductiveRate;
    }

    public Habitat getMainHabitat() {
        return mainHabitat;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public LivingType getLivingType() {
        return livingType;
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isInGroup() {
        return isInGroup;
    }

    public AnimalKind getAnimalKind() {
        return animalKind;
    }

    public String getGroupName() {
        return groupName;
    }
}
