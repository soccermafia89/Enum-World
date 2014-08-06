/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

/**

 @author alex
 */
public class Metric {
    
    public final long time;
    public final long usedMemory;
    public final long freeMemory;
    public final long numQueuedIncompletePartitions;
    public final long newCompletedPartitions;
    public final long newIncompletePartitions;

    public Metric(long myTime,
                  long myUsedMemory,
                  long myFreeMemory,
                  long myNumQueuedIncompletePartitions,
                  long myNewCompletedPartitions,
                  long myNewIncompletePartitions) {
        
        time = myTime;
        usedMemory = myUsedMemory;
        freeMemory = myFreeMemory;
        numQueuedIncompletePartitions = myNumQueuedIncompletePartitions;
        newCompletedPartitions = myNewCompletedPartitions;
        newIncompletePartitions = myNewIncompletePartitions;
    }
}
