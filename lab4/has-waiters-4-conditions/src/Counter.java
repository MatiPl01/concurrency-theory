import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition firstProducer = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition remainingProducers = lock.newCondition();
    private final Condition remainingConsumers = lock.newCondition();

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCount() {
        lock.lock();
        int result = count;
        lock.unlock();
        return result;
    }

    public void produce(int producedCount,
                        Runnable onAccess,
                        Runnable onHasWaitersWait,
                        Runnable onBufferWait) {
        lock.lock();
        try {
            while (lock.hasWaiters(firstProducer)) {
                remainingProducers.await();
                onHasWaitersWait.run();
            }
            while (maxCount - count < producedCount) {
                firstProducer.await();
                onBufferWait.run();
            }
            count += producedCount;
            onAccess.run();
            firstConsumer.signal();
            remainingProducers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void consume(int consumedCount,
                        Runnable onAccess,
                        Runnable onHasWaitersWait,
                        Runnable onBufferWait) {
        lock.lock();
        try {
            while (lock.hasWaiters(firstConsumer)) {
                remainingConsumers.await();
                onHasWaitersWait.run();
            }
            while (count < consumedCount) {
                firstConsumer.await();
                onBufferWait.run();
            }
            count -= consumedCount;
            onAccess.run();
            firstProducer.signal();
            remainingConsumers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
