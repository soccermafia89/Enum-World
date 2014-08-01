/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.data.PartitionExport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**

 @author alex
 */
// Stores incomplete partitions in a stack.
public class DeepProcessor implements Processor {

    private Stack<Partition> incompletePartitions;
    private Collection<ElementList> finalCombinations;
    private boolean runSetFlag;

    public DeepProcessor() {
        incompletePartitions = new Stack();
        finalCombinations = new ArrayList<ElementList>();
    }

    @Override
    public Collection<ElementList> getCompletedPartitions() {
        return finalCombinations;
    }

    @Override
    public Collection<Partition> getIncompletePartitions() {
        return incompletePartitions;
    }

    @Override
    public void runAll() {
        while (incompletePartitions.size() > 0) {
            this.runSet();
        }
    }

    @Override
    public void runSet() {
        runSetFlag = true;
        while(runSetFlag == true) {
            Partition nextPartition = incompletePartitions.pop();
            
        }
    }

    @Override
    public void importPartitions(PartitionExport partitionExport) {

        incompletePartitions.addAll(partitionExport.getIncompletePartitions());
        finalCombinations = partitionExport.getCompletePartitions();
    }

    @Override
    public PartitionExport reset() {
        PartitionExport partitionExport = new PartitionExport(incompletePartitions, finalCombinations);
        incompletePartitions.clear();
        finalCombinations.clear();
        return partitionExport;
    }

    @Override
    public void setPartitions(Collection<Partition> partitions) {
        incompletePartitions.clear();
        incompletePartitions.addAll(partitions);
        finalCombinations.clear();

    }

    @Override
    public void setPartition(Partition partition) {
        Collection<Partition> newIncompletePartitions = new ArrayList<Partition>();
        newIncompletePartitions.add(partition);
        this.setPartitions(newIncompletePartitions);
    }
}
