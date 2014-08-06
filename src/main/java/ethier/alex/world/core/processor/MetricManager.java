/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import java.util.ArrayList;
import java.util.List;

/**

 @author alex
 */
public class MetricManager {

    List<Metric> metrics;

    public MetricManager() {
        metrics = new ArrayList<Metric>();
    }

    public void addRunData(long time,
                           long usedMemory,
                           long freeMemory,
                           long numQueuedIncompletePartitions,
                           long newCompletedPartitions,
                           long newIncompletePartitions) {

        Metric metric = new Metric(time,
                                   usedMemory,
                                   freeMemory,
                                   numQueuedIncompletePartitions,
                                   newCompletedPartitions,
                                   newIncompletePartitions);

        metrics.add(metric);
    }

    public String printMetrics() {
        StringBuilder statisticsOutput = new StringBuilder();

        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            long maxMemory = Runtime.getRuntime().maxMemory();
            double usedMemoryPercent = (1.0 * metric.usedMemory) / (1.0 * maxMemory);

            statisticsOutput.append("\n");
            statisticsOutput.append("Set Count: \t\t\t").append(i).append("\n");
            statisticsOutput.append("Used Memory: \t\t\t").append(metric.usedMemory).append("\n");
            statisticsOutput.append("Free Memory: \t\t\t").append(metric.freeMemory).append("\n");
            statisticsOutput.append("Memory Usage Percent: \t\t").append(usedMemoryPercent).append("\n");
            statisticsOutput.append("Queued Incomplete Partitions: \t").append(metric.numQueuedIncompletePartitions).append("\n");
            statisticsOutput.append("New Completed Partitions: \t").append(metric.newCompletedPartitions).append("\n");
            statisticsOutput.append("New Incompleted Partitions: \t").append(metric.newIncompletePartitions).append("\n");
            statisticsOutput.append("Set took: \t\t\t" + metric.time + " milliseconds.").append("\n");
        }

        return statisticsOutput.toString();
    }

    public String printAggregateMetrics() {
        int numSets = metrics.size();
        long maxMemory = Runtime.getRuntime().maxMemory();

        double averageMemoryUsagePercent = 0L;
        long averageQueuedPartitions = 0L;
        long totalCompletedPartitions = 0L;
        long averageIncompletedPartitions = 0L;
        long totalTime = 0L;
        double averageTime;
        int processedPartitionsPerSecond;

        for (Metric metric : metrics) {
            averageMemoryUsagePercent += (1.0 * metric.usedMemory) / (1.0 * maxMemory);
            averageQueuedPartitions += metric.numQueuedIncompletePartitions;
            if (metric.newCompletedPartitions > totalCompletedPartitions) {
                totalCompletedPartitions = metric.newCompletedPartitions;
            }

            averageIncompletedPartitions += metric.newIncompletePartitions;
            totalTime += metric.time;
        }

        averageMemoryUsagePercent = (averageMemoryUsagePercent / (1.0 * numSets));
        averageQueuedPartitions = (averageQueuedPartitions / numSets);
        averageIncompletedPartitions = (averageIncompletedPartitions / numSets);
        averageTime = (totalTime / numSets);
        if(totalTime == 0) {
            processedPartitionsPerSecond = Integer.MAX_VALUE;
        } else {
            processedPartitionsPerSecond = (int) (1000 * averageQueuedPartitions / totalTime);
        }
        

        StringBuilder statisticsOutput = new StringBuilder();
        statisticsOutput.append("\n");
        statisticsOutput.append("Num Sets: \t\t\t\t").append(numSets).append("\n");
        statisticsOutput.append("Avg. Memory Usage Percent: \t\t").append(averageMemoryUsagePercent).append("\n");
        statisticsOutput.append("Avg. Queued Incomplete Partitions: \t").append(averageQueuedPartitions).append("\n");
        statisticsOutput.append("Total Completed Partitions: \t\t").append(totalCompletedPartitions).append("\n");
        statisticsOutput.append("Avg. Incompleted Partitions: \t\t").append(averageIncompletedPartitions).append("\n");
        statisticsOutput.append("Total Time: \t\t\t\t").append(totalTime).append(" milliseconds.").append("\n");
        statisticsOutput.append("Avg. Time: \t\t\t\t").append(averageTime).append(" milliseconds.").append("\n");
        statisticsOutput.append("Avg. Partitions/sec: \t\t\t").append(processedPartitionsPerSecond).append("\n");
        
        return statisticsOutput.toString();
    }
}
