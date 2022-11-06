public abstract class Actor implements Runnable {
    private final String name;
    protected final Counter counter;
    private final int minCount;
    private final int maxCount;
    private int monitorAccessCount = 0;

    protected Actor(String name, Counter counter, int maxCount) {
        this(name, counter, maxCount, maxCount);
    }

    protected Actor(String name, Counter counter, int minCount, int maxCount) {
        this.name = name;
        this.counter = counter;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return name;
    }

    public void printAccessInfo(int count) {
        System.out.println(String.format("%-14s", "(" + this + "):") +
                           " buffer size: " +
                           String.format("%2d", counter.getCount()) +
                           String.format("%-6s",
                                         " (" +
                                         (count > 0 ? "+" : "") +
                                         count +
                                         ")"
                           ) +
                           " access count: " +
                           monitorAccessCount);
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
