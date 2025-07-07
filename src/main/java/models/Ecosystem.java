package models;

import static services.ProbabilitiesService.getChanceForAttack;
import enums.AnimalKind;
import enums.Biome;
import enums.CarnivoreKind;

import java.util.List;
import java.util.Map;

public class Ecosystem {
    private final String ecosystemName;
    private final Biome biome;
    private final List<Group> updatedGroups;
    private final Map<AnimalKind, List<Group>> groupedAnimals;

    public Ecosystem(String ecosystemName, Biome biome, List<Group> updatedGroups, Map<AnimalKind, List<Group>> groupedAnimals) {
        this.ecosystemName = ecosystemName;
        this.biome = biome;
        this.updatedGroups = updatedGroups;
        this.groupedAnimals = groupedAnimals;
    }

    public void attack(Animal predator, Animal victim) {
        if (tryAttack(predator, victim)) {
            List<Group> initialGroups = groupedAnimals.get(victim.getAnimalKind());
            initialGroups.forEach(group -> {
                List<Animal> animals = group.getGroupedAnimals();
                removeAnimalById(victim.getId(), animals);
            });
        }
    }

    /**
     * Adds an animal to the ecosystem. First attempts to add the animal as a loner
     * (if it meets solitary living conditions). If unsuccessful, adds the animal to
     * an appropriate animal group.
     *
     * @param animal The animal to be added to the ecosystem (must not be {@code null})
     */
    public void addAnimalToEcosystem(Animal animal) {
        addToGroupedAnimals(animal);
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
        if (isGroupExists(animal, updatedGroups)) {
            addNewMember(animal);
        }
        groupedAnimals.put(animal.getAnimalKind(), updatedGroups);
    }

    public void addNewMember(Animal animal) {
        if (isAddLonerCarnivoreSucceed(animal)) return;
        if (isAddLonerHerbivoreSucceed(animal)) return;
        for (Group group : updatedGroups) {
            if (group.getGroupName().equals(animal.getGroupName())) {
                group.getGroupedAnimals().add(animal);
            }
        }
        groupedAnimals.put(animal.getAnimalKind(), updatedGroups);
    }

    private boolean isAddLonerHerbivoreSucceed(Animal animal) {
        if (!isCarnivore(animal) && !animal.isInGroup()) {
            List<Group> herbivores = groupedAnimals.get(animal.getAnimalKind());
            herbivores.forEach(group -> {
                if (group.getGroupName().equals("Loner herbivores")) {
                    group.getGroupedAnimals().add(animal);
                }
            });
            return true;
        }
        return false;
    }

    private boolean isAddLonerCarnivoreSucceed(Animal animal) {
        if (isCarnivore(animal) && !animal.isInGroup()) {
            List<Group> predators = groupedAnimals.get(animal.getAnimalKind());
            predators.forEach(group -> {
                if (group.getGroupName().equals("Loner carnivores")) {
                    group.getGroupedAnimals().add(animal);
                }
            });
            return true;
        }
        return false;
    }

    private boolean isCarnivore(Animal animal) {
        return animal.getAnimalKind() instanceof CarnivoreKind;
    }

    private void removeAnimalById(long id, List<Animal> animals) {
        animals.stream()
                .filter(animal ->
                        animal.getId() == id)
                .findFirst()
                .ifPresent(animals::remove);
    }

    private boolean tryAttack(Animal predator, Animal victim) {
        int attackPoints = getScaledPoints(predator);
        int escapePoints = getScaledPoints(victim);
        if (!predator.isInGroup()) {
            attackPoints -= attackPoints / 2;
        }
        if (victim.isInGroup()) {
            escapePoints += getHerbivoreGroupBonus(escapePoints);
        }
        int succeedAttackChance = (attackPoints / (attackPoints + escapePoints)) * 100;

        if (!isPredatorHavier(predator, victim)) {
            succeedAttackChance = getReducedSucceedAttackChance(succeedAttackChance, predator, victim);
        }
        return getChanceForAttack() <= succeedAttackChance;
    }

    private int getReducedSucceedAttackChance(int succeedChance, Animal predator, Animal victim) {
        int reducedOn = victim.getWeight() / predator.getWeight();
        return succeedChance / reducedOn;
    }

    private int getHerbivoreGroupBonus(int escapePoints) {
        return (int) Math.ceil(escapePoints * 0.3);
    }

    private boolean isPredatorHavier(Animal predator, Animal victim) {
        return predator.getWeight() > victim.getWeight();
    }

    private static int getScaledPoints(Animal animal) {
        return 1 - animal.getCurrentAge() / animal.getMaxAge();
    }

    private boolean isGroupExists(Animal animal, List<Group> groups) {
        return groups.stream()
                .anyMatch(group -> group.getGroupName().equals(animal.getGroupName()));
    }

    public String getEcosystemName() {
        return ecosystemName;
    }

    public Biome getBiome() {
        return biome;
    }

    public Map<AnimalKind, List<Group>> getGroupedAnimals() {
        return groupedAnimals;
    }
}
