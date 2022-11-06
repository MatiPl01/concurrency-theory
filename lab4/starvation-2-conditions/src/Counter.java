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

    public void produce(int producedCount, Runnable callback) {
        lock.lock();
        try {
            while (maxCount - count < producedCount) producers.await();
            count += producedCount;
            callback.run();
            consumers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void consume(int consumedCount, Runnable callback) {
        lock.lock();
        try {
            while (count < consumedCount) consumers.await();
            count -= consumedCount;
            callback.run();
            producers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
