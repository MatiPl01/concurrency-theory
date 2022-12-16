package utils;

public record Summary(
        int bufferSize,
        int threadCount,
        int bufferWork,
        int actorWork,
        double tasksDone,
        double totalCPUTime,
        double averageCPUTime,
        double tasksDonePerCPUSecond
) implements ConvertableToJSON {
    @Override
    public String toJSON() {
        return "{" +
               "\"buffer_size\": " + bufferSize + "," +
               "\"thread_count\": " + threadCount + "," +
               "\"buffer_work\": " + bufferWork + "," +
               "\"actor_work\": " + actorWork + "," +
               "\"tasks_done\": " + tasksDone + "," +
               "\"total_cpu_time\": " + totalCPUTime + "," +
               "\"average_cpu_time\": " + averageCPUTime + "," +
               "\"tasks_done_per_cpu_second\": " + tasksDonePerCPUSecond
           + "}";
    }
}
