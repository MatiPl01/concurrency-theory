package producer_consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer<T> {
    private final Stack<T> buffer = new Stack<>();
    private final int size;
    private int accessCount = 0;

    private final Lock bufferLock = new ReentrantLock();
    private final Lock producerLock = new ReentrantLock();
    private final Lock consumerLock = new ReentrantLock();

    private final Condition producerCondition = bufferLock.newCondition();
    private final Condition consumerCondition = bufferLock.newCondition();

    public Buffer(int size) {
        this.size = size;
    }

    public int getTotalAccessCount() {
        return accessCount;
    }

    public void produce(List<T> products) throws InterruptedException {
        producerLock.lock();
        try {
            bufferLock.lock();
            try {
                while (size - buffer.size() < products.size()) {
                    producerCondition.await();
                }
                buffer.addAll(products);
                accessCount++;
            } finally {
                consumerCondition.signal();
                bufferLock.unlock();
            }
        } finally {
            producerLock.unlock();
        }
    }

    public List<T> consume(int consumedCount) throws InterruptedException {
        consumerLock.lock();
        try {
            bufferLock.lock();
            try {
                while (buffer.size() < consumedCount) {
                    consumerCondition.await();
                }
                List<T> result = new ArrayList<>();
                for (int i = 0; i < consumedCount; i++) {
                    result.add(buffer.pop());
                }
                accessCount++;
                return result;
            } finally {
                producerCondition.signal();
                bufferLock.unlock();
            }
        } finally {
            consumerLock.unlock();
        }
    }
}
