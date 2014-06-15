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
    private Collection<NumeralArray> finalCombinations;
    
    public SimpleProcessor(Partition myPartition) {
        incompletePartitions = new ArrayList<Partition>();
        incompletePartitions.add(myPartition);
        finalCombinations = new ArrayList<NumeralArray>();
    }
    
    public SimpleProcessor(Collection<Partition> myPartitions) {
        incompletePartitions = myPartitions;
        finalCombinations = new ArrayList<NumeralArray>();   
    }
    
    public Collection<NumeralArray> getCompletedPartitions() {
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
        
        NumeralArray combination = partition.getElements();
        
        

//        BitList zeroList = bitLists[0];
//        BitList oneList = bitLists[1];

        Collection<NumeralArray> filters = partition.getFilters();
        int splitIndex = partition.getSplitIndex();
        
//        Collection<BitList> zeroFilters = new ArrayList<BitList>();
//        Collection<BitList> oneFilters = new ArrayList<BitList>();
        int radix = combination.get(splitIndex).getRadix();
        
        Collection<NumeralArray>[] filterSplits = new Collection[radix];
        for(int i=0; i < filterSplits.length;i++) {
            filterSplits[i] = new ArrayList<NumeralArray>();
        }

        boolean allBothFilters = true;
        for(NumeralArray filter : filters) {
            Numeral filterBit = filter.get(splitIndex);
            
            if(filterBit.getState() == FilterElementState.ALL) {
                
                for(Collection<NumeralArray> filterSplit : filterSplits) {
                    filterSplit.add(filter);
                }
                
            } else if(filterBit.getState() == FilterElementState.ONE) {
                int ordinal = filterBit.getOrdinal();
                filterSplits[ordinal].add(filter);
                
                allBothFilters = false;
            } else {
                throw new RuntimeException("Invalid state, filterBit: " + filterBit + " should have correct FilterElementState.");
            }
            
//            if(filterBit == Bit.BOTH) {
//                zeroFilters.add(filter);
//                oneFilters.add(filter);
//            } else if(filterBit == Bit.ZERO) {
//                zeroFilters.add(filter);
//                allBothFilters = false;
//            } else if(filterBit == Bit.ONE) {
//                oneFilters.add(filter);
//                allBothFilters = false;
//            }
        }

        // In the special case that all filters contain a '*' then we don't need to return multiple splits.
        if(allBothFilters) {
            
            Numeral numeral = combination.get(splitIndex);
            Element allElement = new Element(numeral.getRadix(), ElementState.ALL);
            partition.getElements().set(splitIndex, allElement);

            Partition allPartition = new Partition(partition.getElements(), filterSplits[0]);
            
            if(!matchExists(allPartition)) {
                newPartitions.add(allPartition);
            }

        } else {
            NumeralArray[] combinationSplits = partition.getSplits();
            
            for(int i=0;i < filterSplits.length;i++) {
                Collection<NumeralArray> filterCollection = filterSplits[i];
                NumeralArray splitCombination = combinationSplits[i];
                Partition splitPartition = new Partition(splitCombination, filterCollection);
                
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
        
        NumeralArray combination = partition.getElements();
        
        for(NumeralArray filter : partition.getFilters()) {
            Matches matchOutcome = combination.getMatch(filter, partition.getSplitIndex());
            
            if(matchOutcome == Matches.ENTIRELY) {
                return true;
            }
        }
        
        return false;
    }
}
