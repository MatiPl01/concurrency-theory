public class Consumer extends Actor {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public Consumer(Counter counter, int maxCount) {
        this(counter, maxCount, maxCount);
    }

    public Consumer(Counter counter, int minCount, int maxCount) {
        super(NAME + " " + id, counter, minCount, maxCount);
        id++;
    }

    @Override
    public void run() {
        while (true) {
            int count = getRandomCount();
            counter.consume(
                    count,
                    () -> {
                        registerMonitorAccess();
//                        printAccessInfo(-count);
                        setWaitsOnCondition(null);
                    },
                    () -> setWaitsOnCondition("hasWaiters"),
                    () -> setWaitsOnCondition("buffer")
            );
        }
    }
}
