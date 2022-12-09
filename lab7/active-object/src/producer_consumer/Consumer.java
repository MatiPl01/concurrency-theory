package producer_consumer;

import active_object.MessageFuture;
import active_object.Proxy;
import utils.ExpensiveComputation;

public class Consumer<T> extends Actor<T> {
    private static final String NAME = "Consumer";
    private static int id = 0;

    public Consumer(Proxy<T> proxy, int maxCount) {
        this(proxy, maxCount, maxCount);
    }

    public Consumer(Proxy<T> proxy, int minCount, int maxCount) {
        super(NAME + " " + id, proxy, minCount, maxCount);
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
