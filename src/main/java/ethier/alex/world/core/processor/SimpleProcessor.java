/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.core.data.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class SimpleProcessor {
    
    private static Logger logger = Logger.getLogger(SimpleProcessor.class);
    
    private Collection<Partition> incompletePartitions;
    private Collection<ElementList> finalCombinations;
    
    public SimpleProcessor(Partition myPartition) {
        incompletePartitions = new ArrayList<Partition>();
        incompletePartitions.add(myPartition);
        finalCombinations = new ArrayList<ElementList>();
    }
    
    public SimpleProcessor(Collection<Partition> myPartitions) {
        incompletePartitions = myPartitions;
        finalCombinations = new ArrayList<ElementList>();   
    }
    
    public Collection<ElementList> getCompletedPartitions() {
        return finalCombinations;
    }
    
    public void runAll() {
        while(incompletePartitions.size() > 0) {
            this.runSet();
        }
    }
    
    public void runSet() {        
        logger.debug("Processing " + incompletePartitions.size() + " partitions");
        Collection<Partition> newPartitionSet = new ArrayList<Partition>();
                
        Iterator<Partition> it = incompletePartitions.iterator();
        while(it.hasNext()) {
            Partition partition = it.next();
            if(partition.hasSplit()) {
                Collection<Partition> splitPartitions = this.splitPartition(partition);
                newPartitionSet.addAll(splitPartitions);
            } else {
                finalCombinations.add(partition.getElements());
            }
            
            it.remove();
        }
        
        logger.debug(newPartitionSet.size() + " new incomplete partitions created.");
        incompletePartitions = newPartitionSet;
    }
    
    public Collection<Partition> splitPartition(Partition partition) {
        logger.debug("Splitting partition with combination: " + partition.printElements() + " with split index: " + partition.getSplitIndex());
        
        Collection<Partition> newPartitions = new ArrayList<Partition>();
        
        Collection<FilterList> filters = partition.getFilters();
        int splitIndex = partition.getSplitIndex();
        
        int radix = partition.getRadices()[splitIndex];
        
        Collection<FilterList>[] filterSplits = new Collection[radix];
        for(int i=0; i < filterSplits.length;i++) {
            filterSplits[i] = new ArrayList<FilterList>();
        }

        boolean allBothFilters = true;
        for(FilterList filter : filters) {
            Filter filterElement = filter.getFilter(splitIndex);
            
            if(filterElement.getFilterState() == FilterState.ALL) {
                
                for(Collection<FilterList> filterSplit : filterSplits) {
                    filterSplit.add(filter);
                }
                
            } else if(filterElement.getFilterState() == FilterState.ONE) {
                int ordinal = filterElement.getOrdinal();
                filterSplits[ordinal].add(filter);
                
                allBothFilters = false;
            } else {
                throw new RuntimeException("Invalid state, filterBit: " + filterElement + " should have correct FilterElementState.");
            }
        }

        // In the special case that all filters contain a '*' then we don't need to return multiple splits.
        // This is the key to computing quickly.
        if(allBothFilters) {
            
            Element allElement = new Element(ElementState.ALL);
            partition.getElements().set(splitIndex, allElement);

            Partition allPartition = new Partition(partition.getRadices(), partition.getElements(), filterSplits[0]);
            
            if(!matchExists(allPartition)) {
                newPartitions.add(allPartition);
            }

        } else {
            ElementList[] combinationSplits = partition.getSplits();
            
            for(int i=0;i < filterSplits.length;i++) {
                Collection<FilterList> filterCollection = filterSplits[i];
                ElementList splitCombination = combinationSplits[i];
                Partition splitPartition = new Partition(partition.getRadices(), splitCombination, filterCollection);
                
                logger.info("New partition made: " + splitPartition.printElements());
                
                if(!matchExists(splitPartition)) {
                    newPartitions.add(splitPartition);
                }
            }
        }
        
        for(Partition newPartition : newPartitions) {
            if(newPartition.verifyIntegrity() == false) {
                logger.error("Integrity not maintained for partition: "  + newPartition.printElements());
            } else {
                logger.debug("New Partition: " + newPartition.printElements());
            }
        }

        return newPartitions;
    }
    
    private boolean matchExists(Partition partition) {
        
        if(partition == null) {
            logger.error("Passed in partition is null!");
        }
        
        if(partition.getFilters() == null) {
            logger.error("Partiton filters is null!");
        }
        
        ElementList combination = partition.getElements();
        
        for(FilterList filter : partition.getFilters()) {
            Matches matchOutcome = combination.getMatch(filter, partition.getSplitIndex());
            
            if(matchOutcome == Matches.ENTIRELY) {
                return true;
            }
        }
        
        return false;
    }
}
