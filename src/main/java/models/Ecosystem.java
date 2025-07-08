package models;

import enums.AnimalType;
import enums.Biome;
import exceptions.AnimalNotFoundException;
import exceptions.IllegalAttackArgumentException;
import services.ProbabilitiesService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ecosystem {
    private final String ecosystemName;
    private final Biome biome;
    private final List<Animal> updatedGroups;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals;
    private final ProbabilitiesService probabilitiesService;

    public Ecosystem(String ecosystemName, Biome biome, List<Animal> updatedGroups, Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals, ProbabilitiesService probabilitiesService) {
        this.ecosystemName = ecosystemName;
        this.biome = biome;
        this.updatedGroups = updatedGroups;
        this.ecosystemGroupedAnimals = ecosystemGroupedAnimals;
        this.probabilitiesService = probabilitiesService;
    }

    /**
     * Attacks herbivore and if attack was succeed removes herbivore from it group
     *
     * @param predatorId carnivore id which attempts to attack
     * @param victimId   herbivore id which under attack
     */
    public void attack(long predatorId, long victimId) {
        Animal predator = findAnimalById(predatorId, ecosystemGroupedAnimals);
        Animal victim = findAnimalById(victimId, ecosystemGroupedAnimals);
        isAttackValid(predator, victim);
        if (isAttackSucceed(predator, victim)) {
            decreaseHunger(predator, victim);
            ecosystemGroupedAnimals.get(victim.getAnimalType())
                    .get(victim.getGroupName())
                    .removeIf(a -> a.getId() == victim.getId());
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
        if (isGroupExists(animal, animal.getGroupName(), ecosystemGroupedAnimals)) {
            addNewMember(animal);
            return;
        }
        Map<String, List<Animal>> groups = new HashMap<>();
        groups.put(animal.getGroupName(), updatedGroups);
        ecosystemGroupedAnimals.put(animal.getAnimalType(), groups);
    }

    /**
     * Decreases the hunger level of the predator after a successful attack.
     * The hunger decrease is distributed among group members if the predator is in a group.
     *
     * @param predator the attacking carnivore
     * @param victim the herbivore that was attacked
     */
    private void decreaseHunger(Animal predator, Animal victim) {
        if (!predator.isInGroup()) {
            decreaseLonerHunger(predator, victim);
        }
        decreaseGroupHunger(predator, victim);
    }

    /**
     * Decreases hunger for all members of the predator's group after a successful attack.
     * The main predator gets a larger share of the hunger decrease.
     *
     * @param predator the attacking carnivore
     * @param victim the herbivore that was attacked
     */
    private void decreaseGroupHunger(Animal predator, Animal victim) {
        List<Animal> predators = ecosystemGroupedAnimals.get(predator.getAnimalType()).get(predator.getGroupName());
        Carnivore attacker = (Carnivore) predator;
        double hungerForDecrease = calculateHungerDecreaseAmount(predator, victim) / ((double) predators.size() + 1);
        predators.forEach(animal -> {
            if (animal.getId() == predator.getId()) {
                if (isUpdatedHungerGreaterThanInitial(hungerForDecrease, attacker.getCurrentHunger(), attacker)) return;
                BigDecimal roundedHunger = BigDecimal.valueOf(attacker.getCurrentHunger() - (hungerForDecrease * 2)).setScale(1, RoundingMode.HALF_UP);
                attacker.setCurrentHunger(roundedHunger.doubleValue());
            } else if (animal instanceof Carnivore carnivore) {
                if (!isUpdatedHungerGreaterThanInitial(hungerForDecrease, carnivore.getCurrentHunger(), carnivore)) {
                    BigDecimal roundedHunger = BigDecimal.valueOf(carnivore.getCurrentHunger() - hungerForDecrease).setScale(1, RoundingMode.HALF_UP);
                    carnivore.setCurrentHunger(roundedHunger.doubleValue());
                }
            }
        });
    }

    /**
     * Decreases hunger for a solitary predator after a successful attack.
     *
     * @param predator the attacking carnivore
     * @param victim the herbivore that was attacked
     */
    private void decreaseLonerHunger(Animal predator, Animal victim) {
        Carnivore carnivore = (Carnivore) predator;
        if (!carnivore.isDiedFromHunger()) {
            double initHunger = carnivore.getCurrentHunger();
            double updatedHunger = calculateHungerDecreaseAmount(predator, victim);
            if (isUpdatedHungerGreaterThanInitial(updatedHunger, initHunger, carnivore)) return;
            carnivore.setCurrentHunger(updatedHunger);
        }
    }

    /**
     * Checks if the updated hunger value would be greater than the initial hunger.
     * If true, sets the current hunger to 0.
     *
     * @param updatedHunger the proposed new hunger value
     * @param initHunger the current hunger value
     * @param carnivore the carnivore whose hunger is being adjusted
     * @return true if updated hunger would be greater than initial, false otherwise
     */
    private boolean isUpdatedHungerGreaterThanInitial(double updatedHunger, double initHunger, Carnivore carnivore) {
        if (updatedHunger > initHunger) {
            carnivore.setCurrentHunger(0);
            return true;
        }
        return false;
    }

    /**
     * Calculates the amount of hunger decrease based on the weight ratio between victim and predator.
     *
     * @param predator the attacking carnivore
     * @param victim the herbivore that was attacked
     * @return the calculated hunger decrease amount
     */
    private double calculateHungerDecreaseAmount(Animal predator, Animal victim) {
        return (((double) victim.getWeight() / (double) predator.getWeight()) * 100);
    }

    /**
     * Validates if an attack is possible between the given animals.
     *
     * @param predator the attacking animal
     * @param victim the animal being attacked
     * @throws IllegalAttackArgumentException if attack conditions are invalid
     */
    private void isAttackValid(Animal predator, Animal victim) {
        if (!isCarnivore(predator)) throw new IllegalAttackArgumentException("Attacker must be a carnivore!");
        if (isCarnivore(victim)) throw new IllegalAttackArgumentException("Target must be a herbivore!");
    }

    /**
     * Adds new member to the group of animals depending on
     * animal living type and kind
     *
     * @param animal animal which pretend to be a part of group
     */
    private void addNewMember(Animal animal) {
        AnimalType kind = animal.getAnimalType();
        String groupName = animal.getGroupName();

        Map<String, List<Animal>> groups = ecosystemGroupedAnimals.computeIfAbsent(kind, k -> new java.util.HashMap<>());

        List<Animal> groupMembers = groups.computeIfAbsent(groupName, g -> new ArrayList<>());
        groupMembers.add(animal);
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
        double reducedOn = (double) victim.getWeight() / (double) predator.getWeight();
        return (int) (succeedChance / reducedOn) * 100;
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

    /**
     * Finds an animal by its ID in the grouped animals structure.
     *
     * @param targetId the ID of the animal to find
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
     * Checks if an animal is a carnivore.
     *
     * @param animal the animal to check
     * @return true if the animal is a carnivore, false otherwise
     */
    private boolean isCarnivore(Animal animal) {
        return animal.getAnimalType().toString().equalsIgnoreCase("carnivore");
    }

    /**
     * Checks if the predator is heavier than the victim.
     *
     * @param predator the attacking animal
     * @param victim the animal being attacked
     * @return true if predator is heavier, false otherwise
     */
    private boolean isPredatorHavier(Animal predator, Animal victim) {
        return predator.getWeight() > victim.getWeight();
    }

    /**
     * Checks if a group exists for the given animal.
     *
     * @param animal the animal to check group for
     * @param groupName the name of the group
     * @param groups the map of all groups
     * @return true if the group exists, false otherwise
     */
    private boolean isGroupExists(Animal animal, String groupName, Map<AnimalType, Map<String, List<Animal>>> groups) {
        Map<String, List<Animal>> animals = groups.get(animal.getAnimalType());
        return animals.containsKey(groupName);
    }
}
