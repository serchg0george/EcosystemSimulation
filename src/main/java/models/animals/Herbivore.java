package models.animals;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;

import java.util.List;

public class Herbivore extends Animal {
    private final Herbivore herbivoreKind;
    private final int escapePoints;

    public Herbivore(List<Biome> biomes, int currentAge, boolean isAlive, int maxAge, double weight, int reproductiveRate, Habitat mainHabitat, AnimalType animalType, LivingType livingType, boolean isInGroup, Herbivore herbivoreKind, int escapePoints) {
        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, livingType, isInGroup);
        this.herbivoreKind = herbivoreKind;
        this.escapePoints = escapePoints;
    }

    public Herbivore getHerbivoreKind() {
        return herbivoreKind;
    }

    public int getEscapePoints() {
        return escapePoints;
    }
}
