import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final int maxCount;
    private int accessCount = 0;

    private final Lock bufferLock = new ReentrantLock();
    private final Lock producerLock = new ReentrantLock();
    private final Lock consumerLock = new ReentrantLock();

    private final Condition producerCondition = bufferLock.newCondition();
    private final Condition consumerCondition = bufferLock.newCondition();

    public Counter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getTotalAccessCount() {
        return accessCount;
    }

    public void produce(int producedCount) throws InterruptedException {
        producerLock.lock();
        try {
            bufferLock.lock();
            try {
                while (maxCount - count < producedCount) {
                    producerCondition.await();
                }
                count += producedCount;
                accessCount++;
            } finally {
                consumerCondition.signal();
                bufferLock.unlock();
            }
        } finally {
            producerLock.unlock();
        }
    }

    public void consume(int consumedCount) throws InterruptedException {
        consumerLock.lock();
        try {
            bufferLock.lock();
            try {
                while (count < consumedCount) {
                    consumerCondition.await();
                }
                count -= consumedCount;
                accessCount++;
            } finally {
                producerCondition.signal();
                bufferLock.unlock();
            }
        } finally {
            consumerLock.unlock();
        }
    }
}
