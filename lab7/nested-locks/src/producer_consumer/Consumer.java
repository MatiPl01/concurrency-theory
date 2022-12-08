package producer_consumer;

import utils.ExpensiveComputation;

import java.util.List;

public class Consumer<T> extends Actor<T> {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public Consumer(Buffer<T> buffer, int maxCount) {
        this(buffer, maxCount, maxCount);
    }

    public Consumer(Buffer<T> buffer, int minCount, int maxCount) {
        super(NAME + " " + id, buffer, minCount, maxCount);
        id++;
    }

    @Override
    protected void work() {
        ExpensiveComputation.compute();
    }

    @Override
    public void run() {
        try {
            while (true) {
                int consumeCount = getRandomCount();
                List<T> products = buffer.consume(consumeCount);
//                System.out.println("Consume: " + products.size());
                work();
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
