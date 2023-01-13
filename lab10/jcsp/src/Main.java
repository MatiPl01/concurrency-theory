import org.jcsp.lang.*;
import utils.Interval;

import java.util.ArrayList;
import java.util.List;

public final class Main {
    private static final int SUMMARY_INTERVAL = 10000;

    private static final int PRODUCER_COUNT = 10;
    private static final int CONSUMER_COUNT = 10;
    private static final int BUFFER_SIZE = 10;
    private static final int BUFFER_COUNT = 8;

    public static void main(String[] args) {
        List<CSProcess> processes = new ArrayList<>();
        List<OperationsCounter> actors = new ArrayList<>();
        List<OperationsCounter> buffers = new ArrayList<>();

        // Create consumers
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            List<One2OneChannel> inChannels = createOne2OneChannels(BUFFER_COUNT);
            List<One2OneChannel> outChannels = createOne2OneChannels(BUFFER_COUNT);
            Actor consumer = new Consumer(inChannels, outChannels);
            actors.add(consumer);
            processes.add(consumer);
        }

        // Create producers
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            List<One2OneChannel> inChannels = createOne2OneChannels(BUFFER_COUNT);
            List<One2OneChannel> outChannels = createOne2OneChannels(BUFFER_COUNT);
            Actor producer = new Producer(inChannels, outChannels);
            actors.add(producer);
            processes.add(producer);
        }

        // Buffers
        for (int i = 0; i < BUFFER_COUNT; i++) {
            List<One2OneChannel> inChannels = new ArrayList<>();
            List<One2OneChannel> outChannels = new ArrayList<>();
            for (int j = 0; j < PRODUCER_COUNT + CONSUMER_COUNT; j++) {
                inChannels.add(((Actor) actors.get(j)).getOutChannels().get(i));
                outChannels.add(((Actor) actors.get(j)).getInChannels().get(i));
            }
            Buffer buffer = new Buffer(inChannels, outChannels, BUFFER_SIZE);
            processes.add(buffer);
            buffers.add(buffer);
        }

        // Create the summary interval
        new Interval(() -> summarize(actors, buffers), SUMMARY_INTERVAL).start();

        // Run all CSProcesses
        Parallel parallel = new Parallel(processes.toArray(new CSProcess[0]));
        parallel.run();
    }

    private static List<One2OneChannel> createOne2OneChannels(int count) {
        List<One2OneChannel> channels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            channels.add(Channel.one2one());
        }
        return channels;
    }

    private static void summarize(List<OperationsCounter> actors, List<OperationsCounter> buffers)  {
        System.out.println("====== Summary ======");
        System.out.println("Buffers:");
        printSummary(buffers);
        System.out.println("\nClients:");
        printSummary(actors);
        System.out.println("=====================\n");
    }

    private static void printSummary(List<OperationsCounter> operationsCounters) {
        long totalOperations = operationsCounters.stream().reduce(0L,
            (total, buffer) -> total + buffer.getOperationsCount(), Long::sum
        );
        double avgBufferOperations = (double) totalOperations / operationsCounters.size();
        operationsCounters.forEach(counter -> {
            double percentOfTotal = (double) counter.getOperationsCount() / totalOperations * 100;
            System.out.printf(
                    counter + ": " + counter.getOperationsCount()
                    + " (%.4f%% of total) (%+.2f compared to average)\n",
                    percentOfTotal,
                    counter.getOperationsCount() - avgBufferOperations
            );
        });
    }
}
