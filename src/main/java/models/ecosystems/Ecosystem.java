package models.ecosystems;

import static enums.LivingType.ALONE;
import enums.AnimalKind;
import enums.Biome;
import models.animals.Animal;
import models.animals.Carnivore;
import models.animals.Group;
import models.animals.Herbivore;

import java.util.List;
import java.util.Map;

public class Ecosystem {
    private final String ecosystemName;
    private final Biome biome;
    private final List<Animal> lonerAnimals; //todo I may put this List in my groupedAnimals as a group "Loners" for Predators and Grass eaters
    private final List<Group> updatedGroups;
    private final Map<AnimalKind, List<Group>> groupedAnimals;

    public Ecosystem(String ecosystemName, Biome biome, List<Animal> lonerAnimals, List<Group> updatedGroups, Map<AnimalKind, List<Group>> groupedAnimals) {
        this.ecosystemName = ecosystemName;
        this.biome = biome;
        this.lonerAnimals = lonerAnimals;
        this.updatedGroups = updatedGroups;
        this.groupedAnimals = groupedAnimals;
    }

    public void attack(Carnivore predator, Herbivore victim) {
        int scaledAttackPoints = getScaledPoints(predator);
        int scaledEscapePoints = getScaledPoints(victim);
        int initialSucceedAttackChance = (scaledAttackPoints/(scaledAttackPoints + scaledEscapePoints)) * 100;

    }

    public boolean isEscapedFromPredator() {
        return false;
    }

    private static int getScaledPoints(Animal animal) {
        return 1 - animal.getCurrentAge() / animal.getMaxAge();
    }

    /**
     * Adds an animal to the ecosystem. First attempts to add the animal as a loner
     * (if it meets solitary living conditions). If unsuccessful, adds the animal to
     * an appropriate animal group.
     *
     * @param animal The animal to be added to the ecosystem (must not be {@code null})
     */
    public void addAnimalToEcosystem(Animal animal) {
        if (addToLonerAnimals(animal)) {
            return;
        }
        addToGroupedAnimals(animal);
    }

    /**
     * Attempts to add a solitary animal to the collection of loner animals.
     * Addition occurs only if the animal's preferred biomes include this ecosystem's biome
     * and the animal has a solitary living type ({@code ALONE}).
     *
     * @param animal The animal to potentially add as a loner (must not be {@code null})
     * @return {@code true} if the animal was successfully added as a loner,
     *         {@code false} if the animal does not meet loner criteria
     */
    private boolean addToLonerAnimals(Animal animal) {
        if (animal.getBiomes().contains(biome) && animal.getLivingType() == ALONE) {
            lonerAnimals.add(animal);
            return true;
        }
        return false;
    }

    /**
     * Adds a group-living animal to an existing animal group. Iterates through known
     * animal groups to find a matching group (identified by the animal's group name)
     * and adds the animal as a new member. Finally updates the grouped animals mapping
     * with the latest group information.
     *
     * @param animal The group-living animal to add to a group (must not be {@code null})
     */
    private void addToGroupedAnimals(Animal animal) {
        for (Group group : updatedGroups) {
            if (group.getGroupedAnimals().containsKey(animal.getGroupName())) {
                group.addNewMember(animal);
            }
        }
        groupedAnimals.put(animal.getAnimalKind(), updatedGroups);
    }

    public String getEcosystemName() {
        return ecosystemName;
    }

    public Biome getBiome() {
        return biome;
    }

    public List<Animal> getLonerAnimals() {
        return lonerAnimals;
    }

    public Map<AnimalKind, List<Group>> getGroupedAnimals() {
        return groupedAnimals;
    }
}
