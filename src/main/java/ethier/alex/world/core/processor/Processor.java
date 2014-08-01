/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.data.PartitionExport;
import java.util.Collection;

/**

 @author alex
 */
public interface Processor {
    
    // Return list of completed partitions.  A completed partition is one with no filters.
    public Collection<ElementList> getCompletedPartitions();
    
    // Return list of incomplete partitions.
    public Collection<Partition> getIncompletePartitions();
    
    // Import a set of partitions created by the exportPartitions method.
    public void importPartitions(PartitionExport partitionExport);
    
    // Return all complete and incomplete partitions.
    public PartitionExport reset();
    
    // Add a collection of partitions to process.
    public void setPartitions(Collection<Partition> partitions);
    
    // Add a single partition to process.
    public void setPartition(Partition partition);
    
    // Process all incomplete partitions into completed partitions.
    public void runAll();
    
    // Process an atomic set of incomplete partitions.
    // If called indefinitely, the number of incomplete partitions must reach zero.
    public void runSet();
}
