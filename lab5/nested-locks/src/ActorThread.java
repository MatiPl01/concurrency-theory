import utils.MyRandom;

public abstract class ActorThread extends Thread {
    private final String name;
    protected final Counter counter;
    private final int minCount;
    private final int maxCount;

    protected ActorThread(String name, Counter counter, int maxCount) {
        this(name, counter, maxCount, maxCount);
    }

    protected ActorThread(String name, Counter counter, int minCount, int maxCount) {
        this.name = name;
        this.counter = counter;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return name;
    }

    protected int getRandomCount() {
        return MyRandom.randInt(minCount, maxCount);
    }
}
