/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.data.PartitionExport;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */

// Stores incomplete partitions as a stack.
public class DeepProcessor extends SimpleProcessor implements Processor {
    
    private static Logger logger = LogManager.getLogger(DeepProcessor.class);

    public DeepProcessor() {
        super.uncheckedRadicesMap = new HashMap<ElementList, SortedSet<Integer>>();
        super.incompletePartitions = new Stack();
        super.finalCombinations = new ArrayList<ElementList>();
    }

    @Override
    public void runAll() {
        while (incompletePartitions.size() > 0) {
            this.runSet();
        }
    }

    @Override
    public void runSet() {
        Partition nextPartition = (Partition) ((Stack)incompletePartitions).pop();
        
        if(nextPartition.getFilters().isEmpty()) {
            ElementList completedElementList = super.completePartition(nextPartition);

            logger.trace("Final combination found: {}", completedElementList);
            super.finalCombinations.add(completedElementList);   
        } else {
            Collection<Partition> newPartitions = super.computeNewPartitions(nextPartition);
            ((Stack)super.incompletePartitions).addAll(newPartitions);
        }
    }
    
    @Override
    public void importPartitions(PartitionExport partitionExport) {
        incompletePartitions = partitionExport.getIncompletePartitions();
        finalCombinations = partitionExport.getCompletePartitions();
        
        Collection<Partition> conformedPartitions = super.computeConformedPartition(super.incompletePartitions);
        super.incompletePartitions.clear();
        super.incompletePartitions.addAll(conformedPartitions);
    }
    
    @Override
    public void setPartition(Partition partition) {
        Collection<Partition> newIncompletePartitions = new Stack<Partition>();
        newIncompletePartitions.add(partition);
        this.setPartitions(newIncompletePartitions);
    }
    
    @Override
    public void setPartitions(Collection<Partition> partitions) {
        super.incompletePartitions = partitions;
        super.finalCombinations = new ArrayList<ElementList>();
        
        Collection<Partition> conformedPartitions = super.computeConformedPartition(super.incompletePartitions);
        super.incompletePartitions.clear();
        super.incompletePartitions.addAll(conformedPartitions);
    }
}
