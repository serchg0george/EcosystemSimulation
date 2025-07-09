package factory.herbivores;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;
import factory.AnimalCreator;
import models.Herbivore;

import java.util.Set;

public class Zebra extends Herbivore implements AnimalCreator {
    public Zebra(Set<Biome> biomes,
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

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, animalKind, livingType, isInGroup, escapePoints, groupName);
    }

    @Override
    public void create(String animalKind) {
        System.out.println("Zebra has been created!");
    }
}
