public abstract class Actor implements Runnable {
    protected final Counter counter;
    private final int minCount;
    private final int maxCount;
    private int executionCount = 0;

    protected Actor(Counter counter, int maxCount) {
        this.counter = counter;
        this.minCount = maxCount;
        this.maxCount = maxCount;
    }

    protected Actor(Counter counter, int minCount, int maxCount) {
        this.counter = counter;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return Thread.currentThread().getName();
    }

    protected int getCount() {
        executionCount++;
        return Random.randInt(minCount, maxCount);
    }

    protected int getExecutionCount() {
        return executionCount;
    }
}
