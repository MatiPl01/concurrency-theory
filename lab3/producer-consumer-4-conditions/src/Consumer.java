public class Consumer extends Actor {
    public Consumer(Counter counter, int maxCount) {
        super(counter, maxCount);
    }

    public Consumer(Counter counter, int minCount, int maxCount) {
        super(counter, minCount, maxCount);
    }

    @Override
    public String toString() {
        return super.toString() + " (consumer)";
    }

    @Override
    public void run() {
        while (true) {
            counter.consume(getCount());
            System.out.println(this +
                               ", " +
                               counter +
                               ", executed " +
                               getExecutionCount() +
                               " times");
        }
    }
}
