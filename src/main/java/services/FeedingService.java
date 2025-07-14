package services;

import models.Animal;
import models.Carnivore;
import models.Herbivore;

import java.util.List;

/**
 * Handles the logic for feeding carnivores after a successful hunt.
 * This service calculates the hunger decrease based on the victim's weight
 * and distributes it among the predator(s), supporting both solitary hunters and groups.
 */
public class FeedingService {

    /**
     * Processes a successful hunt, decreasing the predator's hunger.
     * Distributes the "food" (hunger reduction) among group members if the predator is in a group,
     * or gives it all to the solitary hunter.
     *
     * @param predator      the attacking carnivore
     * @param victim        the herbivore that was killed
     * @param predatorGroup the list of all members in the predator's group (can be empty if not applicable)
     */
    public void processSuccessfulHunt(Carnivore predator, Herbivore victim, List<Animal> predatorGroup) {
        if (!predator.isInGroup()) {
            decreaseLonerHunger(predator, victim);
        } else {
            decreaseGroupHunger(predator, victim, predatorGroup);
        }
    }

    /**
     * Decreases hunger for a solitary predator after a successful attack.
     *
     * @param predator the attacking carnivore
     * @param victim   the herbivore that was attacked
     */
    protected void decreaseLonerHunger(Carnivore predator, Herbivore victim) {
        if (predator.isAlive()) {
            feedLoner(predator, victim);
        }
    }

    /**
     * Feeds a solitary predator after a successful attack, decreasing its hunger.
     *
     * @param predator the solitary carnivore
     * @param victim   the herbivore that was killed
     */
    protected void feedLoner(Carnivore predator, Herbivore victim) {
        System.out.println("Hunger of " + predator.getAnimalKind() + " was decreased!");
        double initHunger = predator.getCurrentHunger();
        double hungerDecreaseAmount = calculateHungerDecreaseAmount(predator, victim);

        if (isUpdatedHungerGreaterThanInitial(hungerDecreaseAmount, initHunger, predator)) {
            return;
        }
        predator.setCurrentHunger(initHunger - hungerDecreaseAmount);
    }

    /**
     * Decreases hunger for all members of the predator's group after a successful attack.
     * The main predator gets a larger share of the hunger decrease.
     *
     * @param predator      the attacking carnivore
     * @param victim        the herbivore that was attacked
     * @param predatorGroup the list of all members in the predator's group
     */
    protected void decreaseGroupHunger(Carnivore predator, Herbivore victim, List<Animal> predatorGroup) {
        System.out.println("Hunger of " + predator.getAnimalKind() + " and its group " + predator.getGroupName() + " was decreased!");
        double totalDecreaseAmount = calculateHungerDecreaseAmount(predator, victim);
        double hungerDecreasePerAnimal = totalDecreaseAmount / ((double) predatorGroup.size() + 1);
        predatorGroup.forEach(groupMember -> feedGroupMember(predator, (Carnivore) groupMember, hungerDecreasePerAnimal));
    }

    /**
     * Feeds a specific member of the predator's group, either the main attacker or a supporting carnivore.
     *
     * @param predator                the main attacking carnivore
     * @param groupMember             a member of the predator's group to feed
     * @param hungerDecreasePerAnimal the base amount of hunger decrease per group member
     */
    protected void feedGroupMember(Carnivore predator, Carnivore groupMember, double hungerDecreasePerAnimal) {
        if (groupMember.getId() == predator.getId()) {
            feedAttackerWithinGroup(hungerDecreasePerAnimal, predator);
        } else {
            if (!isUpdatedHungerGreaterThanInitial(hungerDecreasePerAnimal, groupMember.getCurrentHunger(), groupMember)) {
                feedSupportersWithinGroup(hungerDecreasePerAnimal, groupMember);
            }
        }
    }

    /**
     * Feeds the main attacker in a group attack, decreasing its hunger by twice the base amount.
     *
     * @param hungerDecreasePerAnimal the base amount of hunger decrease per group member
     * @param attacker                the main attacking carnivore
     */
    protected void feedAttackerWithinGroup(double hungerDecreasePerAnimal, Carnivore attacker) {
        double attackerShare = hungerDecreasePerAnimal * 2;
        if (isUpdatedHungerGreaterThanInitial(attackerShare, attacker.getCurrentHunger(), attacker)) {
            return;
        }
        double calculatedHunger = attacker.getCurrentHunger() - attackerShare;
        attacker.setCurrentHunger(roundToOneDecimal(calculatedHunger));
    }

    /**
     * Feeds a supporting carnivore in the group, decreasing its hunger by the base amount.
     *
     * @param hungerDecreasePerAnimal the base amount of hunger decrease
     * @param supportingCarnivore     a supporting carnivore in the group
     */
    protected void feedSupportersWithinGroup(double hungerDecreasePerAnimal, Carnivore supportingCarnivore) {
        double calculatedHunger = supportingCarnivore.getCurrentHunger() - hungerDecreasePerAnimal;
        supportingCarnivore.setCurrentHunger(roundToOneDecimal(calculatedHunger));
    }

    /**
     * Calculates the amount of hunger decrease based on the weight ratio between victim and predator.
     *
     * @param predator the attacking carnivore
     * @param victim   the herbivore that was attacked
     * @return the calculated hunger decrease amount
     */
    protected double calculateHungerDecreaseAmount(Carnivore predator, Herbivore victim) {
        return (((double) victim.getWeight() / (double) predator.getWeight()) * 100);
    }

    /**
     * Checks if the hunger reduction would result in a negative hunger value.
     * If true, sets the current hunger to 0 to prevent it from going below zero.
     *
     * @param hungerDecrease the amount of hunger reduction
     * @param initialHunger  the current hunger value
     * @param carnivore      the carnivore whose hunger is being adjusted
     * @return true if hunger would become negative, false otherwise
     */
    protected boolean isUpdatedHungerGreaterThanInitial(double hungerDecrease, double initialHunger, Carnivore carnivore) {
        if (hungerDecrease > initialHunger) {
            carnivore.setCurrentHunger(0);
            return true;
        }
        return false;
    }

    /**
     * Rounds the given double value to one decimal place.
     *
     * @param value the double value to round
     * @return returns the rounded double
     */
    protected double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}