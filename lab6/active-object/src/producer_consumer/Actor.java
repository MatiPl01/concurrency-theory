package producer_consumer;

import active_object.Proxy;
import utils.Random;

public abstract class Actor<T> extends Thread {
    private final String name;
    protected final Proxy<T> proxy;
    private final int minCount;
    private final int maxCount;
    private int monitorAccessCount = 0;

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

    public void printAccessInfo(int count) {
//        System.out.println(String.format("%-14s", "(" + this + "):") +
//                           " buffer size: " +
//                           String.format("%2d", counter.getCount()) +
//                           String.format("%-6s",
//                                         " (" +
//                                         (count > 0 ? "+" : "") +
//                                         count +
//                                         ")"
//                           ) +
//                           " access count: " +
//                           monitorAccessCount);
    }

    public int getMonitorAccessCount() {
        return monitorAccessCount;
    }

    protected int getRandomCount() {
        return Random.randInt(minCount, maxCount);
    }

    public void registerMonitorAccess() {
        monitorAccessCount++;
    }
}
