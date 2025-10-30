package models;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;
import exceptions.InvalidBreedingException;

import java.util.Set;

/**
 * Represents a herbivorous animal that Extends the base {@link Animal} class
 * with herbivore-specific attributes. Herbivores have specialized escape
 * capabilities to avoid predators.
 */
public class Herbivore extends Animal {
    private final int escapePoints;

    public Herbivore(Set<Biome> biomes,
                     int currentAge,
                     boolean isAlive,
                     int maxAge,
                     int weight,
                     int reproductiveRate,
                     Habitat mainHabitat,
                     AnimalType animalType,
                     String animalKind,
                     LivingType livingType,
                     boolean isInGroup,
                     int escapePoints,
                     String groupName) {

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, animalKind, livingType, isInGroup, groupName);
        this.escapePoints = escapePoints;
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
        if (animal instanceof Herbivore) {
            System.out.println("New herbivore " + animal.getAnimalKind() + " was born!");
            return new Herbivore(
                    animal.getBiomes(),
                    0,
                    animal.isAlive(),
                    animal.getMaxAge(),
                    animal.getWeight(),
                    animal.getReproductiveRate(),
                    getMainHabitat(),
                    getAnimalType(),
                    getAnimalKind(),
                    animal.getLivingType(),
                    animal.isInGroup(),
                    getEscapePoints(),
                    animal.getGroupName());
        }
        throw new InvalidBreedingException("Expected " + getAnimalKind() + " animal kind but was " + animal.getAnimalKind() + "!");
    }

    public int getEscapePoints() {
        return escapePoints;
    }
}
