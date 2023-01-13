package utils;

import java.util.Random;

public class MyRandom {
    private static final Random rand = new Random(0);

    public static int randInt(int minInt, int maxInt) {
        return rand.nextInt(maxInt - minInt + 1) + minInt;
    }

    public static int randInt(int maxInt) {
        return rand.nextInt(maxInt);
    }
}
