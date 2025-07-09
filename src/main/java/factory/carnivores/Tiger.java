package factory.carnivores;

import enums.AnimalType;
import enums.Biome;
import enums.Habitat;
import enums.LivingType;
import factory.AnimalCreator;
import models.Carnivore;

import java.util.Set;

public class Tiger extends Carnivore implements AnimalCreator {
    public Tiger(Set<Biome> biomes,
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

        super(biomes, currentAge, isAlive, maxAge, weight, reproductiveRate, mainHabitat, animalType, livingType, animalKind, isInGroup, attackPoints, groupName, hungerRate);
    }

    @Override
    public void create(String animalKind) {
        System.out.println("Tiger has been created!");
    }
}
