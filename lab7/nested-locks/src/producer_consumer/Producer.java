package producer_consumer;

import utils.ExpensiveComputation;

import java.util.ArrayList;
import java.util.List;

public class Producer<T> extends Actor<T> {
    private static final String NAME = "Producer";
    private static int id = 0;
    private final T product;

    public Producer(Buffer<T> buffer, int maxCount, T product) {
        this(buffer, maxCount, maxCount, product);
    }

    public Producer(Buffer<T> buffer, int minCount, int maxCount, T product) {
        super(NAME + " " + id, buffer, minCount, maxCount);
        this.product = product;
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
                int produceCount = getRandomCount();
                List<T> products = new ArrayList<>();
                for (int i = 0; i < produceCount; i++) {
                    products.add(product);
                }
                buffer.produce(products);
//                System.out.println("Produce: " + produceCount);
                work();
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
