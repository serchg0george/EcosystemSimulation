package models;

import enums.AnimalType;
import enums.Biome;
import exceptions.AnimalNotFoundException;
import services.ProbabilitiesService;

import java.util.*;

public class Ecosystem {
    private final Biome biome;
    private final Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals;
    private final ProbabilitiesService probabilitiesService;

    public Ecosystem(Biome biome,
                     Map<AnimalType, Map<String, List<Animal>>> ecosystemGroupedAnimals,
                     ProbabilitiesService probabilitiesService) {
        this.biome = biome;
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
        Carnivore predator = (Carnivore) findAnimalById(predatorId, ecosystemGroupedAnimals);
        Herbivore victim = (Herbivore) findAnimalById(victimId, ecosystemGroupedAnimals);
        if (isAttackSucceed(predator, victim)) {
            decreaseHunger(predator, victim);
            System.out.println(victim.getAnimalKind() + " WAS KILLED!");
            removeDeadAnimal(victim);
        }
    }

    /**
     * Adds new member to the group of animals depending on
     * animal living type and kind
     *
     * @param animal animal which pretend to be a part of the group
     */
    public void addAnimalToEcosystem(Animal animal) {
        AnimalType type = animal.getAnimalType();
        String groupName = animal.getGroupName();
        Map<String, List<Animal>> groups = ecosystemGroupedAnimals.computeIfAbsent(type, animalType -> new HashMap<>());
        List<Animal> groupMembers = groups.computeIfAbsent(groupName, animals -> new ArrayList<>());
        groupMembers.add(animal);
    }

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
     * Decreases the hunger level of the predator after a successful attack.
     * The hunger decrease is distributed among group members if the predator is in a group.
     *
     * @param predator the attacking carnivore
     * @param victim   the herbivore that was attacked
     */
    private void decreaseHunger(Carnivore predator, Herbivore victim) {
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
     * @param victim   the herbivore that was attacked
     */
    private void decreaseGroupHunger(Carnivore predator, Herbivore victim) {
        List<Animal> predatorGroup = ecosystemGroupedAnimals.get(predator.getAnimalType()).get(predator.getGroupName());
        double hungerDecreasePerAnimal = calculateHungerDecreaseAmount(predator, victim) / ((double) predatorGroup.size() + 1);
        predatorGroup.forEach(groupMember -> feedGroupMember(predator, groupMember, hungerDecreasePerAnimal));
    }

    private void feedGroupMember(Carnivore predator, Animal groupMember, double hungerDecreasePerAnimal) {
        if (groupMember.getId() == predator.getId()) {
            feedAttackerIntoGroup(hungerDecreasePerAnimal, predator);
        } else if (groupMember instanceof Carnivore supportingCarnivore && !isUpdatedHungerGreaterThanInitial(hungerDecreasePerAnimal, supportingCarnivore.getCurrentHunger(), supportingCarnivore)) {
            feedSupportersIntoGroup(hungerDecreasePerAnimal, supportingCarnivore);
        }
    }

    private void feedAttackerIntoGroup(double hungerDecreasePerAnimal, Carnivore attacker) {
        if (isUpdatedHungerGreaterThanInitial(hungerDecreasePerAnimal, attacker.getCurrentHunger(), attacker)) {
            return;
        }
        double calculatedHunger = attacker.getCurrentHunger() - (hungerDecreasePerAnimal * 2);
        attacker.setCurrentHunger(roundToOneDecimal(calculatedHunger));
    }

    private void feedSupportersIntoGroup(double hungerDecreasePerAnimal, Carnivore supportingCarnivore) {
        double calculatedHunger = supportingCarnivore.getCurrentHunger() - hungerDecreasePerAnimal;
        supportingCarnivore.setCurrentHunger(roundToOneDecimal(calculatedHunger));
    }

    /**
     * Round applied double value to one symbol after point
     *
     * @param value applied double
     * @return returns rounded double
     */
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Decreases hunger for a solitary predator after a successful attack.
     *
     * @param predator the attacking carnivore
     * @param victim   the herbivore that was attacked
     */
    private void decreaseLonerHunger(Carnivore predator, Herbivore victim) {
        if (!predator.hasDiedFromHunger()) {
            feedLoner(predator, victim);
        } else {
            removeDeadAnimal(predator);
        }
    }

    private void feedLoner(Carnivore predator, Herbivore victim) {
        double initHunger = predator.getCurrentHunger();
        double updatedHunger = calculateHungerDecreaseAmount(predator, victim);
        if (isUpdatedHungerGreaterThanInitial(updatedHunger, initHunger, predator)) return;
        predator.setCurrentHunger(updatedHunger);
    }

    private void removeDeadAnimal(Animal target) {
        ecosystemGroupedAnimals.get(target.getAnimalType())
                .get(target.getGroupName())
                .removeIf(animal -> animal.getId() == target.getId());
        if (ecosystemGroupedAnimals.get(target.getAnimalType()).get(target.getGroupName()).isEmpty()) {
            removeExtinctGroup(target);
        }
    }

    private void removeExtinctGroup(Animal target) {
        ecosystemGroupedAnimals.get(target.getAnimalType()).remove(target.getGroupName());
    }

    /**
     * Checks if the updated hunger value would be greater than the initial hunger.
     * If true, sets the current hunger to 0.
     *
     * @param updatedHunger the proposed new hunger value
     * @param initHunger    the current hunger value
     * @param carnivore     the carnivore whose hunger is being adjusted
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
     * @param victim   the herbivore that was attacked
     * @return the calculated hunger decrease amount
     */
    private double calculateHungerDecreaseAmount(Carnivore predator, Herbivore victim) {
        return (((double) victim.getWeight() / (double) predator.getWeight()) * 100);
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
    private boolean isAttackSucceed(Carnivore predator, Herbivore victim) {
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
     * @return returns reduced the chance of successful attack
     */
    private int calculateReducedSucceedAttackChance(int succeedChance, Carnivore predator, Herbivore victim) {
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
    private boolean isPredatorHavier(Carnivore predator, Herbivore victim) {
        return predator.getWeight() > victim.getWeight();
    }

    public Biome getBiome() {
        return biome;
    }

    public Map<AnimalType, Map<String, List<Animal>>> getEcosystemGroupedAnimals() {
        return ecosystemGroupedAnimals;
    }
}
