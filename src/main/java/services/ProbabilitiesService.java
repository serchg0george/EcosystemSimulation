package services;

import java.util.Random;

public class ProbabilitiesService {
    private final Random random = new Random();

    public int getChanceForAttack() {
        return random.nextInt(0, 101);
    }
}
