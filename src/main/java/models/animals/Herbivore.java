package models.animals;

import enums.*;

import java.util.Set;

public class Herbivore extends Animal {
    private final int escapePoints;

    public Herbivore(Set<Biome> biomes, int currentAge, boolean isAlive, int maxAge, double weight, int reproductiveRate, Habitat mainHabitat, AnimalType animalType, AnimalKind animalKind, LivingType livingType, boolean isInGroup, int escapePoints, String groupName) {
        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, livingType, animalKind, isInGroup, groupName);
        this.escapePoints = escapePoints;
    }

    @Override
    protected Animal breed(Animal animal) {
        final int initialAge = 0;
        return new Herbivore(
                animal.getBiomes(),
                initialAge,
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

    public int getEscapePoints() {
        return escapePoints;
    }
}
