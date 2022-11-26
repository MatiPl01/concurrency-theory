package producer_consumer;

import active_object.MessageFuture;
import active_object.Proxy;

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

    void consume(int count) throws InterruptedException {
        MessageFuture<T> messageFuture = proxy.get(count);
        System.out.println("Consumed: " + count);

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
                consume(count);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
