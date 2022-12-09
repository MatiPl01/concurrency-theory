package producer_consumer;

import active_object.MessageFuture;
import active_object.Proxy;
import utils.ExpensiveComputation;

import java.util.ArrayList;
import java.util.List;

public class Producer<T> extends Actor<T> {
    private static final String NAME = "Producer";
    private static int id = 0;
    private final T product;

    public Producer(Proxy<T> proxy, int maxCount, T product) {
        this(proxy, maxCount, maxCount, product);
    }

    public Producer(Proxy<T> proxy, int minCount, int maxCount, T product) {
        super(NAME + " " + id, proxy, minCount, maxCount);
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
            while (!isInterrupted()) {
                int produceCount = getRandomCount();
                List<T> messages = new ArrayList<>();
                for (int i = 0; i < produceCount; i++) {
                    messages.add(product);
                }
                MessageFuture<Void> promise = proxy.put(messages);
                // Do some expensive work while consumption request is being processed
                while (!promise.isCompleted() && !isInterrupted()) work();
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
