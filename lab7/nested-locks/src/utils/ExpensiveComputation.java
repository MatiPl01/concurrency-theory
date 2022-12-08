package utils;

public final class ExpensiveComputation {
    private static final int ITERATIONS = 1000;

    public static void compute() {
        double total = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            total += Math.sin(i) * Math.cos(i);
        }
    }
}
