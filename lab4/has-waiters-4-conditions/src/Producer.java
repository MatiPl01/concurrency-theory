public class Producer extends Actor {
    private static final String NAME = "Producer";
    private static int id = 0;

    public Producer(Counter counter, int maxCount) {
        this(counter, maxCount, maxCount);
    }

    public Producer(Counter counter, int minCount, int maxCount) {
        super(NAME + " " + id, counter, minCount, maxCount);
        id++;
    }

    @Override
    public void run() {
        while (true) {
            int count = getRandomCount();
            counter.produce(count, () -> {
                registerMonitorAccess();
                printAccessInfo(count);
            });
        }
    }
}
