package utils;

import java.util.Random;

public class MyRandom {
    private static final Random rand = new Random(0);

    public static int randInt(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }
}
