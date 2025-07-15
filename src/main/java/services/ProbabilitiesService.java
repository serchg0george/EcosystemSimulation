package services;

import java.util.Random;

/**
 * Provides probability-related services for game mechanics.
 * This class generates random values used to determine probabilistic outcomes,
 * such as attack success chances in game scenarios.
 */
public class ProbabilitiesService {
    private final Random random = new Random();

    /**
     * Calculates a random attack success probability.
     * Generates a value between 0 (inclusive) and 100 (inclusive) representing
     * the percentage chance of a successful attack.
     *
     * @return Random integer in the range [0, 100] representing attack success probability
     */
    public int getChanceForAttack() {
        return random.nextInt(0, 101);
    }
}