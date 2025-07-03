package models.animals;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;

import java.util.List;

public class Carnivore extends Animal {
    private final Carnivore carnivoreKind;
    private final int attackPoints;
    private int hungerRate;

    public Carnivore(List<Biome> biomes, int currentAge, boolean isAlive, int maxAge, double weight, int reproductiveRate, Habitat mainHabitat, AnimalType animalType, LivingType livingType, boolean isInGroup, Carnivore carnivoreKind, int attackPoints) {
        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, livingType, isInGroup);
        this.carnivoreKind = carnivoreKind;
        this.attackPoints = attackPoints;
    }

    public Carnivore getCarnivoreKind() {
        return carnivoreKind;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getHungerRate() {
        return hungerRate;
    }

    public void setHungerRate(int hungerRate) {
        this.hungerRate = hungerRate;
    }
}
