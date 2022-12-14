import producer_consumer.Actor;
import producer_consumer.Buffer;
import producer_consumer.Consumer;
import producer_consumer.Producer;
import utils.JSONArrayWriter;
import utils.Logger;
import utils.Summary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class Main {
    private static final String LOG_FILE_NAME = "nested-locks.txt";
    private static final String JSON_FILE_NAME = "nested-locks.json";

    private static final int TEST_DURATION = 20000; // ms
    private static final int REPETITIONS = 5; // per single test params
    private static final int MAX_PRODUCED_COUNT = 10;
    private static final int MAX_CONSUMED_COUNT = 10;
    private static final int[] BUFFER_SIZES = { 100 };
    private static final int[] THREAD_COUNTS = { 4, 8, 16 };
    private static final int[] BUFFER_WORK = { 0, 50, 100, 250, 500, 1000 };
    private static final int[] ACTOR_WORK = { 0, 50, 100, 250, 500, 1000 };

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
                            runTest(bufferSize, threadCount, bufferWork, actorWork);
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

        int consumerCount = threadCount / 2;
        int producerCount = threadCount - consumerCount;

        int maxConsumed = Math.min(bufferSize / 2, MAX_CONSUMED_COUNT);
        int maxProduced = Math.min(bufferSize / 2, MAX_PRODUCED_COUNT);

        logger.log("=========================");
        logger.log("- buffer size:    " + bufferSize);
        logger.log("- consumer count: " + consumerCount);
        logger.log("- producer count: " + producerCount);
        logger.log("- buffer work:    " + bufferWork);
        logger.log("- actor work:     " + actorWork);

        for (int r = 0; r < REPETITIONS; r++) {
            logger.log("\nTest run: " + (r + 1));
            Buffer<String> buffer = new Buffer<>(bufferSize, bufferWork);
            List<Actor<String>> threads = new ArrayList<>();

            // Create producers
            for (int i = 0; i < producerCount; i++) {
                threads.add(new Producer<>(
                        buffer,
                        1,
                        maxProduced,
                        actorWork,
                        "Some product"
                ));
            }

            // Create consumers
            for (int i = 0; i < consumerCount; i++) {
                threads.add(new Consumer<>(
                        buffer,
                        1,
                        maxConsumed,
                        actorWork
                ));
            }

            // Start all threads, wait the desired test time and put all threads
            // in the waiting state (remove them from the running state)
            threads.forEach(Thread::start);
            Thread.sleep(TEST_DURATION);
            threads.forEach(Thread::suspend);

            // Print the test summary
            double CPUTime = summarizeTestRun(buffer, threads);
            totalTasksDone += buffer.getTasksDone();
            totalCPUTime += CPUTime;

            // Resume and remove all threads
            // (Threads must be resumed in order to get interrupted)
            threads.forEach(Thread::resume);
            threads.forEach(Thread::interrupt);
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

    private static double summarizeTestRun(
            Buffer<String> buffer,
            List<Actor<String>> threads)
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
                   buffer.getTasksDone());
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
