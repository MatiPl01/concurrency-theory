public class Producer implements Runnable {
    private final Counter counter;

    public Producer(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            counter.produce();
            System.out.println("Produce: " + counter.getCount());
        }
    }
}
