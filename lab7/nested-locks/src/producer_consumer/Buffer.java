package producer_consumer;

import utils.ExpensiveComputation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer<T> {
    private final Stack<T> buffer = new Stack<>();
    private final int size;
    private final int bufferWork;
    private long tasksDone = 0;

    private final Lock bufferLock = new ReentrantLock();
    private final Lock producerLock = new ReentrantLock();
    private final Lock consumerLock = new ReentrantLock();

    private final Condition producerCondition = bufferLock.newCondition();
    private final Condition consumerCondition = bufferLock.newCondition();

    public Buffer(int size, int bufferWork) {
        this.size = size;
        this.bufferWork = bufferWork;
    }

    public long getTasksDone() {
        return tasksDone;
    }

    public void produce(List<T> products) throws InterruptedException {
        producerLock.lock();
        try {
            bufferLock.lock();
            try {
                while (size - buffer.size() < products.size()) {
                    producerCondition.await();
                }
                ExpensiveComputation.compute(bufferWork);
                buffer.addAll(products);
                tasksDone++;
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
                ExpensiveComputation.compute(bufferWork);
                List<T> result = new ArrayList<>();
                for (int i = 0; i < consumedCount; i++) {
                    result.add(buffer.pop());
                }
                tasksDone++;
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
