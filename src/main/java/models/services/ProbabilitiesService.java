package models.services;

import java.util.Random;

public class ProbabilitiesService {
    private static final Random random = new Random();

    public static int getChanceForAttack() {
        return random.nextInt(0, 100);
    }
}
