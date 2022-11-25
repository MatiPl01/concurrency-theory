public class ProducerThread extends ActorThread {
    private static final String NAME = "Producer";
    private static int id = 0;

    public ProducerThread(Counter counter, int maxCount) {
        this(counter, maxCount, maxCount);
    }

    public ProducerThread(Counter counter, int minCount, int maxCount) {
        super(NAME + " " + id, counter, minCount, maxCount);
        id++;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int count = getRandomCount();
                counter.produce(count);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
