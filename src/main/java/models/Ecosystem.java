package models;

import enums.AnimalKind;
import enums.Biome;
import services.ProbabilitiesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ecosystem {
    private final String ecosystemName;
    private final Biome biome;
    private final List<Group> updatedGroups;
    private final Map<AnimalKind, List<Group>> groupedAnimals;
    private final ProbabilitiesService probabilitiesService;

    //TODO implement method for decreasing predator hungriness

    public Ecosystem(String ecosystemName, Biome biome, List<Group> updatedGroups, Map<AnimalKind, List<Group>> groupedAnimals, ProbabilitiesService probabilitiesService) {
        this.ecosystemName = ecosystemName;
        this.biome = biome;
        this.updatedGroups = updatedGroups;
        this.groupedAnimals = groupedAnimals;
        this.probabilitiesService = probabilitiesService;
    }

    /**
     * Attacks herbivore and if attack was succeed removes herbivore from it group
     *
     * @param predatorId carnivore id which attempts to attack
     * @param victimId   herbivore id which under attack
     */
    public void attack(long predatorId, long victimId) {
        //TODO isAttacking herbivore actually?
        Animal predator = findAnimalById(predatorId, groupedAnimals);
        Animal victim = findAnimalById(victimId, groupedAnimals);
        if (isAttackSucceed(predator, victim)) {
            List<Group> initialGroups = groupedAnimals.get(victim.getAnimalKind());
            initialGroups.forEach(group -> {
                List<Animal> animals = group.getGroupedAnimals();
                removeAnimalById(victim.getId(), animals); //I use static id increment so there's no chance to have same ids
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
     * @param animal animal to add to a group (must not be {@code null})
     */
    private void addToGroupedAnimals(Animal animal) {
        if (isGroupExists(animal, updatedGroups)) {
            addNewMember(animal);
        }
        groupedAnimals.put(animal.getAnimalKind(), updatedGroups);
    }

    /**
     * Adds new member to the group of animals depending on
     * animal living type and kind
     *
     * @param animal animal which pretend to be a part of group
     */
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

    /**
     * Tries to add loner herbivore to loners group within herbivores
     *
     * @param animal receive animal which may be a loner
     * @return if animal actually have a living type ALONE returns true,
     * otherwise — false
     */
    private boolean isAddLonerHerbivoreSucceed(Animal animal) {
        if (!isCarnivore(animal) && !animal.isInGroup()) {
            List<Group> herbivores = groupedAnimals.get(animal.getAnimalKind());
            herbivores.forEach(group -> {
                if (group.getGroupName().equals("Loners")) {
                    group.getGroupedAnimals().add(animal);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Tries to add loner carnivore to loners group within carnivores
     *
     * @param animal receive animal which may be a loner
     * @return if animal actually have a living type ALONE returns true,
     * otherwise — false
     */
    private boolean isAddLonerCarnivoreSucceed(Animal animal) {
        if (isCarnivore(animal) && !animal.isInGroup()) {
            List<Group> predators = groupedAnimals.get(animal.getAnimalKind());
            predators.forEach(group -> {
                if (group.getGroupName().equals("Loners")) {
                    group.getGroupedAnimals().add(animal);
                }
            });
            return true;
        }
        return false;
    }

    private boolean isCarnivore(Animal animal) {
        return animal.getAnimalType().toString().equalsIgnoreCase("carnivore");
    }

    /**
     * Tries to apply attack attempt on the victim,
     * uses attack chances and special formula to calculate
     * succeed attack percentage
     *
     * @param predator apply carnivore kind as an attacker
     * @param victim   apply herbivore kind as a victim
     * @return returns true in case of success, otherwise false
     */
    private boolean isAttackSucceed(Animal predator, Animal victim) {
        int attackPoints = calculateScaledPoints(predator);
        int escapePoints = calculateScaledPoints(victim);

        if (!predator.isInGroup()) {
            attackPoints -= attackPoints / 2;
        }
        if (victim.isInGroup()) {
            escapePoints += calculateHerbivoreGroupBonus(escapePoints);
        }
        int succeedAttackChance = (attackPoints / (attackPoints + escapePoints)) * 100;

        if (!isPredatorHavier(predator, victim)) {
            succeedAttackChance = calculateReducedSucceedAttackChance(succeedAttackChance, predator, victim);
        }
        return probabilitiesService.getChanceForAttack() <= succeedAttackChance;
    }

    /**
     * Calculates succeed of attack attempt depending on predator&victim weights
     *
     * @param succeedChance apply initial succeedChance
     * @param predator      apply predator
     * @param victim        apply victim
     * @return returns reduced chance of successful attack
     */
    private int calculateReducedSucceedAttackChance(int succeedChance, Animal predator, Animal victim) {
        int reducedOn = victim.getWeight() / predator.getWeight();
        return succeedChance / reducedOn;
    }

    /**
     * Calculates herbivore escape points if herbivore in group
     *
     * @param escapePoints apply initial escape points
     * @return returns calculated group bonus
     */
    private int calculateHerbivoreGroupBonus(int escapePoints) {
        return (int) Math.ceil(escapePoints * 0.3);
    }

    private boolean isPredatorHavier(Animal predator, Animal victim) {
        return predator.getWeight() > victim.getWeight();
    }

    /**
     * Calculates scaled escape/attack points for animal
     * which depends on animal current age and max age
     *
     * @param animal apply animal as a parameter
     * @return returns calculated points
     */
    private int calculateScaledPoints(Animal animal) {
        return 100 - (animal.getCurrentAge() * 100 / animal.getMaxAge());
    }

    private void removeAnimalById(long id, List<Animal> animals) {
        animals.stream()
                .filter(animal ->
                        animal.getId() == id)
                .findFirst()
                .ifPresent(animals::remove);
    }

    private Animal findAnimalById(long targetId, Map<AnimalKind, List<Group>> groupedAnimals) {
        final List<Animal> foundAnimal = new ArrayList<>();
        groupedAnimals.forEach((animalKind, groups) -> {
            groups.forEach(group ->
                    group.getGroupedAnimals().forEach(
                            animal -> {
                                if (animal.getId() == targetId) {
                                    foundAnimal.add(animal);
                                }
                            }));
        });
        return foundAnimal.getFirst();
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
        return Map.copyOf(groupedAnimals);
    }
}
