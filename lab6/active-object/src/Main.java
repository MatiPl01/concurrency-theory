import active_object.Proxy;
import active_object.Scheduler;
import producer_consumer.Actor;
import producer_consumer.Consumer;
import producer_consumer.Producer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int producerCount = 10;
        int consumerCount = 10;
        int bufferSize = 10;
        int maxCount = bufferSize / 2;

        Scheduler<String> scheduler = new Scheduler<>(bufferSize);
        Proxy<String> proxy = new Proxy<>(scheduler);

        List<Actor<String>> actors = new ArrayList<>();

        // Create producers
        for (int i = 0; i < producerCount; i++) {
            actors.add(new Producer<>(proxy, 1, maxCount, "Some product"));
        }

        // Create consumers
        for (int i = 0; i < consumerCount; i++) {
            actors.add(new Consumer<>(proxy, 1, maxCount));
        }

        // Start all threads
        scheduler.start();
        actors.forEach(Thread::start);
    }
}
