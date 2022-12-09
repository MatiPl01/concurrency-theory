import active_object.Proxy;
import active_object.Scheduler;
import producer_consumer.Actor;
import producer_consumer.Consumer;
import producer_consumer.Producer;
import utils.Counter;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Main {
    private static final ThreadMXBean threadManager =
            ManagementFactory.getThreadMXBean();

    private static final int TEST_DURATION = 10000; // ms
    private static final int REPETITIONS = 10; // per single test params
    private static final int[] THREAD_COUNTS = { 2, 4, 6, 8, 10, 15, 20, 25, 30, 40, 50 };
    private static final int[] BUFFER_SIZES = { 10, 1000, 100000 };

    public static void main(String[] args) throws InterruptedException {
        for (int bufferSize : BUFFER_SIZES) {
            for (int threadCount : THREAD_COUNTS) {
                runTest(bufferSize, threadCount);
            }
        }
    }

    private static void runTest(int bufferSize,
                                int threadCount) throws InterruptedException {
        long totalTasksDoneCount = 0;
        double totalCPUTime = 0;

        System.out.println("=========================");
        System.out.println("- buffer size:  " + bufferSize);
        System.out.println("- thread count: " + threadCount);

        for (int r = 0; r < REPETITIONS; r++) {
            System.out.println("\nTest run: " + (r + 1));
            Scheduler<String> scheduler = new Scheduler<>(bufferSize);
            Proxy<String> proxy = new Proxy<>(scheduler);
            List<Actor<String>> threads = new ArrayList<>();

            int consumerCount = threadCount / 2;
            int producerCount = threadCount - consumerCount;

            // Create small producers
            for (int i = 0; i < producerCount; i++) {
                threads.add(new Producer<>(proxy, 1, bufferSize / 2, "Some product"));
            }

            // Create small consumers
            for (int i = 0; i < consumerCount; i++) {
                threads.add(new Consumer<>(proxy, 1, bufferSize / 2));
            }

            // Start all threads, wait the desired test time and put all threads
            // in the waiting state (remove them from the running state)
            scheduler.start();
            threads.forEach(Thread::start);
            Thread.sleep(TEST_DURATION);
            threads.forEach(Thread::suspend);

            // Print the test summary
            double CPUTime = summarizeTestRun(threads);
            totalTasksDoneCount += Counter.getTasksDoneCount();
            totalCPUTime += CPUTime;

            // Resume and remove all threads
            // (Threads must be resumed in order to get interrupted)
            threads.forEach(Thread::resume);
            threads.forEach(Thread::interrupt);
            scheduler.interrupt();
            Counter.reset();
        }

        System.out.println("\n=========================");
        summarizeTestResults(totalTasksDoneCount, threadCount, totalCPUTime);
        System.out.println("=========================\n\n");
    }

    private static double summarizeTestRun(List<Actor<String>> threads) {
        double totalCPUTime = threads
              .stream()
               // .getThreadCpuTime() includes only time spent in the runnable state
              .mapToDouble(thread -> threadManager.getThreadCpuTime(thread.getId()))
              .sum() / 1000000; // convert to ms
        double avgCPUTime = totalCPUTime / threads.size();

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Total tasks done count:  " +
                   Counter.getTasksDoneCount());
        joiner.add("Total real time:         " +
                   TEST_DURATION +
                   " ms");
        joiner.add("Total CPU time:          " +
                   String.format("%.4f", totalCPUTime) +
                   " ms");
        joiner.add("Average thread CPU time: " +
                   String.format("%.4f", avgCPUTime) +
                   " ms");
        System.out.println(joiner);

        return totalCPUTime;
    }

    private static void summarizeTestResults(long totalAccessCount,
                                             int threadCount,
                                             double totalCPUTime) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Average results after running " +
                   REPETITIONS + " tests:");
        joiner.add("Total tasks done count:              " +
                   String.format("%.2f", (double) totalAccessCount / REPETITIONS));
        joiner.add("Total CPU time:                      " +
                   String.format("%.2f", totalCPUTime / REPETITIONS)
                   + " ms");
        joiner.add("Average thread CPU time:             " +
                   String.format("%.2f", totalCPUTime / threadCount/ REPETITIONS)
                   + " ms");
        joiner.add("Total monitor access per CPU second: " +
                   String.format("%.2f", totalAccessCount / (totalCPUTime/ 1000)) +
                   " 1/s"
        );
        System.out.println(joiner);
    }
}