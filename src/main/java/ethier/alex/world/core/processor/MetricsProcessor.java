/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import com.google.common.base.Stopwatch;
import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.data.PartitionExport;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class MetricsProcessor implements Processor {

    private static Logger logger = LogManager.getLogger(MetricsProcessor.class);
    private Processor processor;
    private Stopwatch timer;
    private int previousIncompletePartitions;
    private MetricManager metricManager;

    public MetricsProcessor(Processor myProcessor) {
        logger.info("Using metrics processor over: {}", myProcessor.getClass().getCanonicalName());
        metricManager = new MetricManager();
        processor = myProcessor;
    }

    @Override
    public Collection<ElementList> getCompletedPartitions() {
        return processor.getCompletedPartitions();
    }

    @Override
    public Collection<Partition> getIncompletePartitions() {
        return processor.getIncompletePartitions();
    }

    @Override
    public void runAll() {
        while(!processor.getIncompletePartitions().isEmpty()) {
            this.runSet();
        }
    }

    @Override
    public void runSet() {
        timer = Stopwatch.createStarted();
        previousIncompletePartitions = processor.getIncompletePartitions().size();
        processor.runSet();
        gatherStatistics();

        
        logger.info("Set finished with: {} partitions left.", processor.getIncompletePartitions().size());
        
    }

    @Override
    public void importPartitions(PartitionExport partitionExport) {
        processor.importPartitions(partitionExport);
        metricManager = new MetricManager();
    }

    @Override
    public PartitionExport reset() {
        metricManager = new MetricManager();
        return processor.reset();
    }

    @Override
    public void setPartitions(Collection<Partition> partitions) {
        metricManager = new MetricManager();
        processor.setPartitions(partitions);
    }

    @Override
    public void setPartition(Partition partition) {
        metricManager = new MetricManager();
        processor.setPartition(partition);
    }

    private void gatherStatistics() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();        
        long numCompletedPartitions = processor.getCompletedPartitions().size();
        long numIncompletePartitions = processor.getIncompletePartitions().size();


        metricManager.addRunData(timer.elapsed(TimeUnit.MILLISECONDS),
                                 usedMemory, 
                                 freeMemory, 
                                 previousIncompletePartitions,
                                 numCompletedPartitions,
                                 numIncompletePartitions);
    }
    
    public void printFullMetrics() {
        System.out.println(metricManager.printMetrics());
    }
    
    public void printAggregateMetrics() {
        System.out.println(metricManager.printAggregateMetrics());
    }
}
