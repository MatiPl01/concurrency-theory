public class ConsumerThread extends ActorThread {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public ConsumerThread(Counter counter, int maxCount) {
        this(counter, maxCount, maxCount);
    }

    public ConsumerThread(Counter counter, int minCount, int maxCount) {
        super(NAME + " " + id, counter, minCount, maxCount);
        id++;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int count = getRandomCount();
                counter.consume(count);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
