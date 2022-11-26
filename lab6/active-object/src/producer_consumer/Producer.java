package producer_consumer;

import active_object.MessageFuture;
import active_object.Proxy;

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

    void produce(int count) throws InterruptedException {
        List<T> messages = new ArrayList<>();

        for (int i = 0; i < count; i++) messages.add(product);

        MessageFuture<Void> messageFuture = proxy.put(messages);
        System.out.println("Produced: " + count);

        // TODO - improve waiting
        while (!messageFuture.isCompleted()) {
            Thread.sleep(10);
        }
    }

    @Override
    public void run() {
        while (true) {
            int count = getRandomCount();
            try {
                produce(count);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
