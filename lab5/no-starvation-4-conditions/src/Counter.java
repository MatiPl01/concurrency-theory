import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;
    private final Lock lock = new ReentrantLock();

    private boolean isFirstProducerWaiting = false;
    private boolean isFirstConsumerWaiting = false;
    private final Condition firstProducer = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition remainingProducers = lock.newCondition();
    private final Condition remainingConsumers = lock.newCondition();

    private int accessCount = 0;

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getTotalAccessCount() {
        return accessCount;
    }

    public void produce(int producedCount) throws InterruptedException {
        lock.lock();
        try {
            while (isFirstProducerWaiting) remainingProducers.await();
            isFirstProducerWaiting = true;
            while (maxCount - count < producedCount) firstProducer.await();
            count += producedCount;
            accessCount++;
            firstConsumer.signal();
            remainingProducers.signal();
            isFirstProducerWaiting = false;
        } finally {
            lock.unlock();
        }
    }

    public void consume(int consumedCount) throws InterruptedException {
        lock.lock();
        try {
            while (isFirstConsumerWaiting) remainingConsumers.await();
            isFirstConsumerWaiting = true;
            while (count < consumedCount) firstConsumer.await();
            count -= consumedCount;
            accessCount++;
            firstProducer.signal();
            remainingConsumers.signal();
            isFirstConsumerWaiting = false;
        } finally {
            lock.unlock();
        }
    }
}
