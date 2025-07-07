package models;

import enums.*;

import java.util.Set;

public class Carnivore extends Animal {
    private final int attackPoints;
    private final int hungerRate;
    private int currentHunger;

    public Carnivore(Set<Biome> biomes,
                     int currentAge,
                     boolean isAlive,
                     int maxAge,
                     int weight,
                     int reproductiveRate,
                     Habitat mainHabitat,
                     AnimalType animalType,
                     LivingType livingType,
                     AnimalKind animalKind,
                     boolean isInGroup,
                     int attackPoints,
                     String groupName,
                     int hungerRate) {

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, animalKind, livingType, isInGroup, groupName);
        this.attackPoints = attackPoints;
        this.hungerRate = hungerRate;
    }

    @Override
    protected Animal breed(Animal animal) {
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

    protected boolean isDiedFromHunger() {
        if (currentHunger >= 100) {
            setAlive(false);
            return true;
        }
        return false;
    }

    protected void feed() {
        currentHunger += hungerRate;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getHungerRate() {
        return hungerRate;
    }

    public int getCurrentHunger() {
        return currentHunger;
    }
}
