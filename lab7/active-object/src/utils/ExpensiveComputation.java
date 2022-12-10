package utils;

public final class ExpensiveComputation {
    public static void compute(int iterations) {
        double total = 0;
        for (int i = 0; i < iterations; i++) {
            total += Math.sin(i) * Math.cos(i);
        }
    }
}
