package models;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;

import java.util.Set;

/**
 * Represents a carnivorous animal in a wildlife simulation. Extends the base {@link Animal} class
 * with carnivore-specific attributes like attack strength and hunger management. Carnivores can
 * attack prey, experience hunger, and die if starvation reaches critical levels.
 */
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

    /**
     * Creates a new offspring through reproduction.
     * The offspring inherits characteristics from this parent and starts at age 0.
     *
     * @param animal The parent animal providing genetic traits
     * @return New Herbivore offspring with initialized traits
     */
    @Override
    public Animal breed(Animal animal) {
        final int initialAge = 0;
        System.out.println("New carnivore " + animal.getAnimalKind() + " was born!");
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

    /**
     * Checks if this carnivore has starved to death. Sets alive status to false
     * if hunger reaches 100%.
     *
     * @return true if animal died from hunger, false otherwise
     */
    protected boolean hasDiedFromHunger() {
        if (currentHunger >= 100) {
            System.out.println("Carnivore died " + getAnimalKind() + " from hunger!");
            setAlive(false);
            return true;
        }
        return false;
    }

    /**
     * Increases current hunger level by this carnivore's predefined hunger rate.
     */
    public void increaseHunger() {
        System.out.println("Hunger of " + getAnimalKind() + " increased!");
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
