import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;

    private final Lock lock = new ReentrantLock();

    private boolean isFirstProducer = false;
    private boolean isFirstConsumer = false;
    private final Condition firstProducer = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition remainingProducers = lock.newCondition();
    private final Condition remainingConsumers = lock.newCondition();

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return "Counter: " + count + "/" + maxCount;
    }

    public void produce(int producedCount) {
        lock.lock();
        try {
            while (isFirstProducer) remainingProducers.await();
            while (maxCount - count < producedCount) firstProducer.await();
            count += producedCount;
            firstConsumer.signal();
            remainingProducers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void consume(int consumedCount) {
        lock.lock();
        try {
            while (isFirstConsumer) remainingConsumers.await();
            while (count < consumedCount) firstConsumer.await();
            count -= consumedCount;
            firstProducer.signal();
            remainingConsumers.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
