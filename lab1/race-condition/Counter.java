public class Counter {
    private static int count = 0;

    public static synchronized void increment() {
        count++;
    }

    public static synchronized void decrement() {
        count--;
    }

    public static int getCount() {
        return count;
    }
}
