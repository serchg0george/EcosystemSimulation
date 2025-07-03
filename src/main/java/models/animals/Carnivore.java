package models.animals;

import enums.*;

import java.util.Set;

public class Carnivore extends Animal {
    private final Carnivore carnivoreKind;
    private final int attackPoints;
    private int hungerRate;

    public Carnivore(Set<Biome> biomes,
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
                     Carnivore carnivoreKind,
                     int attackPoints,
                     String groupName) {

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, livingType, animalKind, isInGroup, groupName);
        this.carnivoreKind = carnivoreKind;
        this.attackPoints = attackPoints;
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
                getCarnivoreKind(),
                getAttackPoints(),
                getGroupName());
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
