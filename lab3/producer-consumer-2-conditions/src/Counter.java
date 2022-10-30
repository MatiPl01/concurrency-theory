import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;

    private final Lock lock = new ReentrantLock();
    private final Condition producers = lock.newCondition();
    private final Condition consumers = lock.newCondition();

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCount() {
        lock.lock();
        int result = count;
        lock.unlock();
        return result;
    }

    public void produce() {
        lock.lock();
        try {
            while (count == maxCount) producers.await();
            count++;
            consumers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        lock.lock();
        try {
            while (count == 0) consumers.await();
            count--;
            producers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
