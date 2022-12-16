package producer_consumer;

import java.util.ArrayList;
import java.util.List;

public class Producer<T> extends Actor<T> {
    private static final String NAME = "Producer";
    private static int id = 0;
    private final T product;

    public Producer(Buffer<T> buffer,
                    int maxCount,
                    int workIterations,
                    T product) {
        this(buffer, maxCount, maxCount, workIterations, product);
    }

    public Producer(Buffer<T> buffer,
                    int minCount,
                    int maxCount,
                    int workIterations,
                    T product) {
        super(NAME + " " + id, buffer, minCount, maxCount, workIterations);
        this.product = product;
        id++;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
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
