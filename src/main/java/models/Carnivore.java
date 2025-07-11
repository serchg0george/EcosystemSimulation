package models;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;

import java.util.Set;

public class Carnivore extends Animal {
    private final int attackPoints;
    private final int hungerRate;
    private double currentHunger;

    public Carnivore(Set<Biome> biomes,
                     int currentAge,
                     boolean isAlive,
                     int maxAge,
                     int weight,
                     int reproductiveRate,
                     Habitat mainHabitat,
                     AnimalType animalType,
                     LivingType livingType,
                     String animalKind,
                     boolean isInGroup,
                     int attackPoints,
                     String groupName,
                     int hungerRate) {

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, animalKind, livingType, isInGroup, groupName);
        this.attackPoints = attackPoints;
        this.hungerRate = hungerRate;
    }

    @Override
    public Animal breed(Animal animal) {
        final int initialAge = 0;
        return new Carnivore(
                animal.getBiomes(),
                initialAge,
                animal.isAlive(),
                animal.getMaxAge(),
                animal.getMaxAge(),
                animal.getReproductiveRate(),
                getMainHabitat(),
                getAnimalType(),
                getLivingType(),
                getAnimalKind(),
                animal.isInGroup(),
                getAttackPoints(),
                getGroupName(),
                getHungerRate());
    }

    protected boolean hasDiedFromHunger() {
        if (currentHunger >= 100) {
            setAlive(false);
            return true;
        }
        return false;
    }

    public void increaseHunger() {
        currentHunger += hungerRate;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getHungerRate() {
        return hungerRate;
    }

    public double getCurrentHunger() {
        return currentHunger;
    }

    public void setCurrentHunger(double currentHunger) {
        this.currentHunger = currentHunger;
    }
}
