package producer_consumer;

import utils.MyRandom;

public abstract class Actor<T> extends Thread {
    private final String name;
    protected final Buffer<T> buffer;
    private final int minCount;
    private final int maxCount;

    protected Actor(String name, Buffer<T> buffer, int maxCount) {
        this(name, buffer, maxCount, maxCount);
    }

    protected Actor(String name, Buffer<T> buffer, int minCount, int maxCount) {
        this.name = name;
        this.buffer = buffer;
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
