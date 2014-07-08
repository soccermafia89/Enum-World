/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.Partition;
import java.util.Collection;

/**

 @author alex
 */
public interface Processor {
    
    // Return list of completed partitions.  A completed partition is one with no filters.
    public Collection<ElementList> getCompletedPartitions();
    
    // Return list of incomplete partitions.
    public Collection<Partition> getIncompletePartitions();
    
    // Process all incomplete partitions into completed partitions.
    public void runAll();
    
    // Process an atomic set of incomplete partitions.
    // If called indefinitely, the number of incomplete partitions must reach zero.
    public void runSet();
}
