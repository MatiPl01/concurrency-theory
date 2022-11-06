import utils.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Main {
    public static void main(String[] args) {
        int smallProducerCount = 10;
        int smallConsumerCount = 10;
        int bufferSize = 100000;
        int maxCount = bufferSize / 2;

        Counter counter = new Counter(bufferSize);
        List<Actor> producers = new ArrayList<>();
        List<Actor> consumers = new ArrayList<>();

        // Crete one big consumer thread (that consumes max possible
        // number of products that is half of the buffer size)
        Consumer bigConsumer = new Consumer(counter, maxCount, maxCount);
        consumers.add(bigConsumer);

        // Create small producers
        for (int i = 0; i < smallProducerCount; i++) {
            producers.add(new Producer(counter, 1, 3));
        }

        // Create small consumers
        for (int i = 0; i < smallConsumerCount; i++) {
            consumers.add(new Consumer(counter, 1, 3));
        }

        // Start all threads
        for (Actor consumer : consumers) new Thread(consumer).start();
        for (Actor producer : producers) new Thread(producer).start();

        // Add interval that prints summary
        new Interval(() -> summarize(producers, consumers), 1).start();
    }

    private static void summarize(List<Actor> producers, List<Actor> consumers) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("\n=====================");
        joiner.add("Producers: ");
        producers.forEach(p -> joiner.add(Main.getActorAccessText(p)));
        joiner.add("\nConsumers: ");
        consumers.forEach(c -> joiner.add(Main.getActorAccessText(c)));
        joiner.add("=====================\n");
        System.out.println(joiner);
    }

    private static String getActorAccessText(Actor actor) {
        int count = actor.getMonitorAccessCount();
        return String.format("%-14s", actor) + ": " + String.format("%8d", count);
    }
}
