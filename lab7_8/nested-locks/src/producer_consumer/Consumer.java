package producer_consumer;

import java.util.List;

public class Consumer<T> extends Actor<T> {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public Consumer(Buffer<T> buffer, int maxCount, int workIterations) {
        this(buffer, maxCount, maxCount, workIterations);
    }

    public Consumer(Buffer<T> buffer,
                    int minCount,
                    int maxCount,
                    int workIterations) {
        super(NAME + " " + id, buffer, minCount, maxCount, workIterations);
        id++;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
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
