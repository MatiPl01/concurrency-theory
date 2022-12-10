import active_object.Proxy;
import active_object.Scheduler;
import producer_consumer.Actor;
import producer_consumer.Consumer;
import producer_consumer.Producer;
import utils.Counter;
import utils.JSONArrayWriter;
import utils.Logger;
import utils.Summary;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Main {
    private static final String LOG_FILE_NAME = "nested-locks.txt";
    private static final String JSON_FILE_NAME = "nested-locks.json";

    private static final int TEST_DURATION = 100000; // ms
    private static final int REPETITIONS = 5; // per single test params
    private static final int MAX_PRODUCED_COUNT = 10;
    private static final int MAX_CONSUMED_COUNT = 10;
    private static final int[] BUFFER_SIZES = { 10, 1000, 100000 };
    private static final int[] THREAD_COUNTS = { 2, 4, 6, 8, 10 };
    private static final int[] BUFFER_WORK = { 0,
                                               10,
                                               25,
                                               50,
                                               100,
                                               250,
                                               500,
                                               1000,
                                               2500,
                                               5000,
                                               10000,
                                               25000,
                                               50000,
                                               100000,
                                               250000,
                                               500000,
                                               1000000
    };
    private static final int[] ACTOR_WORK = { 0,
                                              10,
                                              25,
                                              50,
                                              100,
                                              250,
                                              500,
                                              1000,
                                              2500,
                                              5000,
                                              10000,
                                              25000,
                                              50000,
                                              100000,
                                              250000,
                                              500000,
                                              1000000
    };

    private static final ThreadMXBean threadManager =
            ManagementFactory.getThreadMXBean();

    private static Logger logger;
    private static JSONArrayWriter<Summary> jsonWriter;

    public static void main(String[] args) throws IOException {
        logger = new Logger(LOG_FILE_NAME);
        jsonWriter = new JSONArrayWriter<>(JSON_FILE_NAME);

        try {
            for (int bufferSize : BUFFER_SIZES) {
                for (int threadCount : THREAD_COUNTS) {
                    for (int bufferWork : BUFFER_WORK) {
                        for (int actorWork : ACTOR_WORK) {
                            runTest(
                                    bufferSize,
                                    threadCount,
                                    bufferWork,
                                    actorWork
                            );
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            logger.close();
            jsonWriter.close();
        }
    }

    private static void runTest(int bufferSize,
                                int threadCount,
                                int bufferWork,
                                int actorWork)
            throws InterruptedException, IOException {
        long totalTasksDone = 0;
        double totalCPUTime = 0;

        logger.log("=========================");
        logger.log("- buffer size:  " + bufferSize);
        logger.log("- thread count: " + threadCount);
        logger.log("- buffer work:  " + bufferWork);
        logger.log("- actor work:   " + actorWork);

        for (int r = 0; r < REPETITIONS; r++) {
            logger.log("\nTest run: " + (r + 1));
            Scheduler<String> scheduler = new Scheduler<>(bufferSize, bufferWork);
            Proxy<String> proxy = new Proxy<>(scheduler);
            List<Actor<String>> threads = new ArrayList<>();

            int consumerCount = threadCount / 2;
            int producerCount = threadCount - consumerCount;

            int maxConsumed = Math.min(bufferSize / 2, MAX_CONSUMED_COUNT);
            int maxProduced = Math.min(bufferSize / 2, MAX_PRODUCED_COUNT);

            // Create producers
            for (int i = 0; i < producerCount; i++) {
                threads.add(new Producer<>(
                        proxy,
                        1,
                        maxProduced,
                        actorWork,
                        "Some product"
                ));
            }

            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                threads.add(new Consumer<>(
                        proxy,
                        1,
                        maxConsumed,
                        actorWork
                ));
            }

            // Start all threads, wait the desired test time and put all threads
            // in the waiting state (remove them from the running state)
            scheduler.start();
            threads.forEach(Thread::start);
            Thread.sleep(TEST_DURATION);
            threads.forEach(Thread::suspend);

            // Print the test summary
            double CPUTime = summarizeTestRun(threads);
            totalTasksDone += Counter.getTasksDoneCount();
            totalCPUTime += CPUTime;

            // Resume and remove all threads
            // (Threads must be resumed in order to get interrupted)
            threads.forEach(Thread::resume);
            threads.forEach(Thread::interrupt);
            scheduler.interrupt();
            Counter.reset();
        }

        logger.log("\n=========================");
        summarizeTestResults(
                bufferSize,
                threadCount,
                bufferWork,
                actorWork,
                totalTasksDone,
                totalCPUTime
        );
        logger.log("=========================\n\n");
    }

    private static double summarizeTestRun(List<Actor<String>> threads)
            throws IOException {
        double totalCPUTime = threads
              .stream()
              // .getThreadCpuTime() includes only time spent in the runnable state
              .mapToDouble(thread -> threadManager.getThreadCpuTime(
                      thread.getId()))
              .sum() / 1000000; // convert to ms
        double avgCPUTime = totalCPUTime / threads.size();

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Total monitor access count: " +
                   Counter.getTasksDoneCount());
        joiner.add("Total real time:            " +
                   TEST_DURATION +
                   " ms");
        joiner.add("Total CPU time:             " +
                   String.format("%.4f", totalCPUTime) +
                   " ms");
        joiner.add("Average thread CPU time:    " +
                   String.format("%.4f", avgCPUTime) +
                   " ms");
        logger.log(joiner.toString());

        return totalCPUTime;
    }

    private static void summarizeTestResults(int bufferSize,
                                             int threadCount,
                                             int bufferWork,
                                             int actorWork,
                                             long tasksDone,
                                             double totalCPUTime)
            throws IOException {
        Summary summary = new Summary(
                bufferSize,
                threadCount,
                bufferWork,
                actorWork,
                (double) tasksDone / REPETITIONS,
                totalCPUTime / REPETITIONS,
                totalCPUTime / threadCount / REPETITIONS,
                tasksDone / (totalCPUTime / 1000)
        );

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Average results after running " +
                   REPETITIONS + " tests:");
        joiner.add("Total tasks done:                    " +
                   String.format("%.2f", summary.tasksDone())
        );
        joiner.add("Total CPU time:                      " +
                   String.format("%.2f", summary.totalCPUTime()) + " ms");
        joiner.add("Average thread CPU time:             " +
                   String.format("%.2f", summary.averageCPUTime()) + " ms");
        joiner.add("Total monitor access per CPU second: " +
                   String.format("%.2f", summary.tasksDonePerCPUSecond()) +
                   " 1/s"
        );

        logger.log(joiner.toString());
        jsonWriter.write(summary);
    }
}
