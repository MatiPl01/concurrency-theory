package producer_consumer;

import active_object.Proxy;
import utils.Random;

public abstract class Actor<T> extends Thread {
    private final String name;
    protected final Proxy<T> proxy;
    private final int minCount;
    private final int maxCount;

    protected Actor(String name, Proxy<T> proxy, int maxCount) {
        this(name, proxy, maxCount, maxCount);
    }

    protected Actor(String name, Proxy<T> proxy, int minCount, int maxCount) {
        this.name = name;
        this.proxy = proxy;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return name;
    }

    protected abstract void work();

    protected int getRandomCount() {
        return Random.randInt(minCount, maxCount);
    }
}
