package models.animals;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;

import java.util.List;

public abstract class Animal {
    private final int maxAge;
    private final double weight;
    private final int reproductiveRate;
    private final Habitat mainHabitat;
    private final AnimalType animalType;
    private final LivingType livingType;
    private final List<Biome> biomes;
    private int currentAge;
    private boolean isAlive;
    private boolean isInGroup;

    public Animal(List<Biome> biomes, int currentAge, boolean isAlive, int maxAge, double weight, int reproductiveRate, Habitat mainHabitat, AnimalType animalType, LivingType livingType, boolean isInGroup) {
        this.biomes = biomes;
        this.currentAge = currentAge;
        this.isAlive = isAlive;
        this.maxAge = maxAge;
        this.weight = weight;
        this.reproductiveRate = reproductiveRate;
        this.mainHabitat = mainHabitat;
        this.animalType = animalType;
        this.livingType = livingType;
        this.isInGroup = isInGroup;
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

    public List<Biome> getBiomes() {
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
}
