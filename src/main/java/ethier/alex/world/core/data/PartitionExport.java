/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.ArrayList;
import java.util.Collection;

/**

 @author alex
 */
public class PartitionExport {
    private Collection<Partition> incompletePartitions;
    private Collection<ElementList> completePartitions;
    
    public PartitionExport(Collection<Partition> myIncompletePartitions, Collection<ElementList> myCompletePartitions) {
        incompletePartitions = new ArrayList<Partition>();
        completePartitions = new ArrayList<ElementList>();
        
        incompletePartitions.addAll(myIncompletePartitions);
        completePartitions.addAll(myCompletePartitions);
    }
    
    public Collection<Partition> getIncompletePartitions() {
        return incompletePartitions;
    }
    
    public Collection<ElementList> getCompletePartitions() {
        return completePartitions;
    }
}
