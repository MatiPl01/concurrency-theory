public class Producer extends Actor {
    public Producer(Counter counter, int maxCount) {
        super(counter, maxCount);
    }

    public Producer(Counter counter, int minCount, int maxCount) {
        super(counter, minCount, maxCount);
    }

    @Override
    public String toString() {
        return super.toString() + " (producer)";
    }

    @Override
    public void run() {
        while (true) {
            counter.produce(getCount());
            System.out.println(this +
                               ", " +
                               counter +
                               ", executed " +
                               getExecutionCount() +
                               " times");
        }
    }
}
