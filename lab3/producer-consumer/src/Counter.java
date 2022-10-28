import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;

    private static final Lock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public Counter(int maxCount, int initialCount) {
        this.maxCount = maxCount;
        this.count = initialCount;
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
            while (count == maxCount) notEmpty.await();
            count++;
            notFull.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        lock.lock();
        try {
            while (count == 0) notFull.await();
            count--;
            notEmpty.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
