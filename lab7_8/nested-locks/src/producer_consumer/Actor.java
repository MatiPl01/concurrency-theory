package producer_consumer;

import utils.ExpensiveComputation;
import utils.MyRandom;

public abstract class Actor<T> extends Thread {
    private final String name;
    protected final Buffer<T> buffer;
    private final int minCount;
    private final int maxCount;
    private final int workIterations;

    protected Actor(String name,
                    Buffer<T> buffer,
                    int maxCount,
                    int workIterations) {
        this(name, buffer, maxCount, maxCount, workIterations);
    }

    protected Actor(String name,
                    Buffer<T> buffer,
                    int minCount,
                    int maxCount,
                    int workIterations) {
        this.name = name;
        this.buffer = buffer;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.workIterations = workIterations;
    }

    protected void work() {
        ExpensiveComputation.compute(workIterations);
    }

    @Override
    public String toString() {
        return name;
    }

    protected int getRandomCount() {
        return MyRandom.randInt(minCount, maxCount);
    }
}
