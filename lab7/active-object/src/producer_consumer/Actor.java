package producer_consumer;

import active_object.Proxy;
import utils.MyRandom;

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

    protected abstract void work();

    @Override
    public String toString() {
        return name;
    }

    protected int getRandomCount() {
        return MyRandom.randInt(minCount, maxCount);
    }
}
