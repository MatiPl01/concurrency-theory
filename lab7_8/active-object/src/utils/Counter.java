package utils;

public final class Counter {
    private static long tasksDoneCount = 0;

    public static void registerTaskDone() {
        tasksDoneCount++;
    }

    public static long getTasksDoneCount() {
        return tasksDoneCount;
    }

    public static void reset() {
        tasksDoneCount = 0;
    }
}
