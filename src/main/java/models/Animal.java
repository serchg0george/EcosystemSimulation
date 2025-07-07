package models;

import enums.*;

import java.util.Set;

public abstract class Animal {
    private final int maxAge;
    private final int weight;
    private final int reproductiveRate;
    private final Habitat mainHabitat;
    private final AnimalType animalType;
    private final LivingType livingType;
    private final Set<Biome> biomes;
    private final AnimalKind animalKind;
    private final String groupName;
    private final long id;
    private static long nextId = 0;
    private int currentAge;
    private boolean isAlive;
    private boolean isInGroup;

    public Animal(Set<Biome> biomes,
                  int currentAge,
                  boolean isAlive,
                  int maxAge,
                  int weight,
                  int reproductiveRate,
                  Habitat mainHabitat,
                  AnimalType animalType,
                  AnimalKind animalKind,
                  LivingType livingType,
                  boolean isInGroup,
                  String groupName) {

        this.id = nextId++;
        this.biomes = biomes;
        this.currentAge = currentAge;
        this.isAlive = isAlive;
        this.maxAge = maxAge;
        this.weight = weight;
        this.reproductiveRate = reproductiveRate;
        this.mainHabitat = mainHabitat;
        this.animalType = animalType;
        this.animalKind = animalKind;
        this.livingType = livingType;
        this.isInGroup = isInGroup;
        this.groupName = groupName;
    }

    protected abstract Animal breed(Animal animal);

    protected void growUp(int currentAge) {
        this.currentAge = ++currentAge;
    }

    public long getId() {
        return id;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getWeight() {
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
        return Set.copyOf(biomes);
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

    public String getGroupName() {
        return groupName;
    }

    public AnimalKind getAnimalKind() {
        return animalKind;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
