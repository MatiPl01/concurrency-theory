package producer_consumer;

import active_object.Proxy;
import utils.ExpensiveComputation;
import utils.MyRandom;

public abstract class Actor<T> extends Thread {
    private final String name;
    protected final Proxy<T> proxy;
    private final int minCount;
    private final int maxCount;
    private final int workIterations;

    protected Actor(String name,
                    Proxy<T> proxy,
                    int maxCount,
                    int workIterations) {
        this(name, proxy, maxCount, maxCount, workIterations);
    }

    protected Actor(String name,
                    Proxy<T> proxy,
                    int minCount,
                    int maxCount,
                    int workIterations) {
        this.name = name;
        this.proxy = proxy;
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
