package producer_consumer;

import active_object.MessageFuture;
import active_object.Proxy;

public class Consumer<T> extends Actor<T> {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public Consumer(Proxy<T> proxy, int maxCount, int workIterations) {
        this(proxy, maxCount, maxCount, workIterations);
    }

    public Consumer(Proxy<T> proxy,
                    int minCount,
                    int maxCount,
                    int workIterations) {
        super(NAME + " " + id, proxy, minCount, maxCount, workIterations);
        id++;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                int consumeCount = getRandomCount();
                MessageFuture<T> messageFuture = proxy.get(consumeCount);
                // Do some expensive work while consumption request is being processed
                while (!messageFuture.isCompleted() && !isInterrupted()) work();
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
