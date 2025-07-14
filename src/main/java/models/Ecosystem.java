package models;

import enums.AnimalType;
import enums.Biome;
import exceptions.AnimalNotFoundException;
import services.FeedingService;
import services.ProbabilitiesService;

import java.util.*;

/**
 * Represents an ecosystem within a specific biome, managing groups of animals and their interactions.
 * Handles animal attacks, hunger mechanics, group feeding dynamics, and extinction checks.
 * Also manages the addition and removal of animals and groups as the simulation progresses.
 *
 * @see Biome
 * @see Animal
 * @see AnimalType
 * @see ProbabilitiesService
 * @see FeedingService
 */
public class Ecosystem {
    private final Biome biome;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals;
    private final ProbabilitiesService probabilitiesService;
    private final FeedingService feedingService;

    public Ecosystem(Biome biome,
                     Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals,
                     ProbabilitiesService probabilitiesService,
                     FeedingService feedingService) {

        this.biome = biome;
        this.ecosystemGroupedAnimals = ecosystemGroupedAnimals;
        this.probabilitiesService = probabilitiesService;
        this.feedingService = feedingService;
    }

    /**
     * An animal attacks a herbivore. If the attack is successful, the predator's hunger is reduced,
     * and the herbivore is removed from its group.
     *
     * @param predatorId carnivore id which attempts to attack
     * @param victimId   herbivore id which is under attack
     */
    public void attack(long predatorId, long victimId) {
        Carnivore predator = (Carnivore) findAnimalById(predatorId, ecosystemGroupedAnimals);
        Herbivore victim = (Herbivore) findAnimalById(victimId, ecosystemGroupedAnimals);
        System.out.println("Predator " + predator.getAnimalKind() + " applied attack attempt onto " + victim.getAnimalKind());
        if (isAttackSucceed(predator, victim)) {
            System.out.println("Succeed attack!");

            List<Animal> predatorGroup = ecosystemGroupedAnimals.get(predator.getAnimalType()).get(predator.getGroupName());
            feedingService.processSuccessfulHunt(predator, victim, predatorGroup);

            System.out.println(victim.getAnimalKind() + " WAS KILLED!");
            removeDeadAnimal(victim);
        }
    }

    /**
     * Adds a new member to a group of animals depending on the
     * animal's living type and kind.
     *
     * @param animal animal which pretends to be a part of the group
     */
    public void addAnimalToEcosystem(Animal animal) {
        AnimalType type = animal.getAnimalType();
        String groupName = animal.getGroupName();
        Map<String, List<Animal>> groups = ecosystemGroupedAnimals.computeIfAbsent(type, k -> new HashMap<>());
        List<Animal> groupMembers = groups.computeIfAbsent(groupName, animals -> new ArrayList<>());
        groupMembers.add(animal);
    }

    /**
     * Checks if any animal type has gone extinct (no living members remain).
     *
     * @return true if at least one animal type has no living members, false otherwise
     */
    public boolean hasExtinctAnimalType() {
        for (AnimalType type : AnimalType.values()) {
            Map<String, List<Animal>> groups = ecosystemGroupedAnimals.get(type);
            if (groups == null || groups.isEmpty()) continue;

            boolean anyAlive = groups.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .anyMatch(Animal::isAlive);
            if (!anyAlive) {
                System.out.println("All animals of type " + type + " are dead.");
                return true;
            }
        }
        return false;
    }

    /**
     * Increases hunger levels for all carnivores in the ecosystem.
     * Removes carnivores that die from hunger.
     *
     * @param groups Map of carnivore groups to process
     */
    public void increaseHungerOfCarnivore(Map<String, List<Animal>> groups) {
        groups.forEach((groupName, animals) -> {
            Iterator<Animal> iterator = animals.iterator();
            while (iterator.hasNext()) {
                Carnivore carnivore = (Carnivore) iterator.next();
                if (carnivore.hasDiedFromHunger()) {
                    iterator.remove();
                } else {
                    carnivore.increaseHunger();
                }
            }
        });
    }

    /**
     * Removes a dead animal from its group and removes the group if it becomes extinct.
     *
     * @param target the dead animal to remove
     */
    private void removeDeadAnimal(Animal target) {
        System.out.println("Dead animal " + target.getAnimalKind() + " was removed!");
        ecosystemGroupedAnimals.get(target.getAnimalType())
                .get(target.getGroupName())
                .removeIf(animal -> animal.getId() == target.getId());
        if (ecosystemGroupedAnimals.get(target.getAnimalType()).get(target.getGroupName()).isEmpty()) {
            removeExtinctGroup(target);
        }
    }

    /**
     * Removes the group of the given animal from the ecosystem, as the group has become extinct.
     *
     * @param target an animal whose group is extinct
     */
    private void removeExtinctGroup(Animal target) {
        System.out.println("Group " + target.getGroupName() + " extincted and was removed!");
        ecosystemGroupedAnimals.get(target.getAnimalType()).remove(target.getGroupName());
    }

    /**
     * Tries to apply an attack attempt on the victim,
     * using attack chances and a special formula to calculate
     * the success percentage of the attack.
     *
     * @param predator the carnivore attacker
     * @param victim   the herbivore victim
     * @return returns true in case of success, otherwise false
     */
    private boolean isAttackSucceed(Carnivore predator, Herbivore victim) {
        int attackPoints = calculateScaledPoints(predator);
        int escapePoints = calculateScaledPoints(victim);

        if (!predator.isInGroup()) {
            attackPoints -= attackPoints / 2;
        }
        if (victim.isInGroup()) {
            escapePoints += calculateHerbivoreGroupBonus(escapePoints);
        }
        int succeedAttackChance = (int) (((double) attackPoints / (attackPoints + escapePoints)) * 100);

        if (!isPredatorHeavier(predator, victim)) {
            succeedAttackChance = calculateReducedSucceedAttackChance(succeedAttackChance, predator, victim);
        }
        return probabilitiesService.getChanceForAttack() <= succeedAttackChance;
    }

    /**
     * Calculates the reduced chance of a successful attack depending on predator & victim weights.
     *
     * @param succeedChance the initial succeedChance
     * @param predator      the predator
     * @param victim        the victim
     * @return returns the reduced chance of a successful attack
     */
    private int calculateReducedSucceedAttackChance(int succeedChance, Carnivore predator, Herbivore victim) {
        double ratio = (double) predator.getWeight() / victim.getWeight();
        if (ratio >= 1) return succeedChance;
        return (int) (succeedChance * ratio);
    }

    /**
     * Calculates herbivore escape points bonus if the herbivore is in a group.
     *
     * @param escapePoints the initial escape points
     * @return returns the calculated group bonus
     */
    private int calculateHerbivoreGroupBonus(int escapePoints) {
        return (int) Math.ceil(escapePoints * 0.3);
    }

    /**
     * Calculates scaled escape/attack points for an animal,
     * which depends on the animal's current age and max age.
     *
     * @param animal the animal as a parameter
     * @return returns the calculated points
     */
    private int calculateScaledPoints(Animal animal) {
        return 100 - (animal.getCurrentAge() * 100 / animal.getMaxAge());
    }

    /**
     * Finds an animal by its ID in the grouped animals structure.
     *
     * @param targetId       the ID of the animal to find
     * @param groupedAnimals the map of grouped animals to search in
     * @return the found animal
     * @throws AnimalNotFoundException if the animal is not found
     */
    private Animal findAnimalById(long targetId, Map<AnimalType, Map<String, List<Animal>>> groupedAnimals) {
        return groupedAnimals.values().stream()
                .flatMap(map -> map.values().stream())
                .flatMap(List::stream)
                .filter(animal -> animal.getId() == targetId)
                .findFirst()
                .orElseThrow(() -> new AnimalNotFoundException("Animal with id " + targetId + " not found"));
    }

    /**
     * Checks if the predator is heavier than the victim.
     *
     * @param predator the attacking animal
     * @param victim   the animal being attacked
     * @return true if a predator is heavier, false otherwise
     */
    private boolean isPredatorHeavier(Carnivore predator, Herbivore victim) {
        return predator.getWeight() > victim.getWeight();
    }

    public Biome getBiome() {
        return biome;
    }

    public Map<AnimalType, Map<String, List<Animal>>> getEcosystemGroupedAnimals() {
        return ecosystemGroupedAnimals;
    }
}
