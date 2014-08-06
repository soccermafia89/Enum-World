/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import ethier.alex.world.addon.ElementListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class SimpleProcessor implements Processor {
    
    protected static Logger logger = LogManager.getLogger(SimpleProcessor.class);
    
    protected Collection<Partition> incompletePartitions;
    protected Collection<ElementList> finalCombinations;
    protected Map<ElementList, SortedSet<Integer>> uncheckedRadicesMap;
    
    public SimpleProcessor() {
        uncheckedRadicesMap = new HashMap<ElementList, SortedSet<Integer>>();
        
        incompletePartitions = new ArrayList<Partition>();
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
        while(!incompletePartitions.isEmpty()) {
            this.runSet();
        }
    }
    
    @Override
    public void runSet() {    
        
        logger.trace("");
        logger.trace("Processing set with {} partitions", incompletePartitions.size());

        Collection<Partition> newPartitionSet = new ArrayList<Partition>();
                
        for(Partition partition : incompletePartitions) {
            
            if(partition.getFilters().isEmpty()) {
                ElementList completedElementList = this.completePartition(partition);
                
                logger.trace("Final combination found: {}", completedElementList);
                finalCombinations.add(completedElementList);                
            } else {
                newPartitionSet.addAll(this.computeNewPartitions(partition));
            }
        }
        
        incompletePartitions = newPartitionSet;
    }
    
    protected Collection<Partition> computeNewPartitions(Partition partition) {
        SortedSet<Integer> uncheckedRadices = uncheckedRadicesMap.get(partition.getElements());
        Collection<Partition> splitPartitions = new ArrayList<Partition>();       
        
        if(!uncheckedRadices.isEmpty()) {
            int uncheckedRadix = uncheckedRadices.first();
            splitPartitions = this.splitPartition(partition, uncheckedRadix);

            SortedSet<Integer> newUncheckedRadices = new TreeSet<Integer>();
            newUncheckedRadices.addAll(uncheckedRadices);
            newUncheckedRadices.remove(uncheckedRadix);
            for(Partition splitPartition : splitPartitions) {
                uncheckedRadicesMap.put(splitPartition.getElements(), newUncheckedRadices);
            }
        }
        uncheckedRadicesMap.remove(partition.getElements());
        return splitPartitions;
    }
    
    protected ElementList completePartition(Partition partition) {
        // Switch all UNSET elements to ALL
        ElementList elements = partition.getElements();
        ElementListBuilder elementsCopy = ElementListBuilder.newInstance().copy(elements);
        for (int i = 0; i < elements.getLength(); i++) {

            Element element = partition.getElements().getElement(i);
            if (element.getElementState() == ElementState.UNSET)  {
                Element allElement = new Element(ElementState.ALL);
                elementsCopy.setElement(i, allElement);
            }
        }
        
        return elementsCopy.getElementList();
    } 
    
    private Collection<Partition> splitPartition(Partition partition, int splitIndex) {
        
        Collection<Partition> newPartitions = new ArrayList<Partition>();
        Collection<FilterList> filters = partition.getFilters();
        
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
                throw new RuntimeException("Invalid state, filterBit: {} should have correct FilterElementState." + filterElement);
            }
        }

        // In the special case that all filters contain a '*' then we don't need to return multiple splits.
        // This is the key to computing quickly.
        if(allBothFilters) {
            
            Element allElement = new Element(ElementState.ALL);
            ElementList elements = partition.getElements();
            ElementList elementsCopy = ElementListBuilder.newInstance()
                    .copy(elements)
                    .setElement(splitIndex, allElement)
                    .getElementList();
                        
            Partition allPartition = PartitionBuilder.newInstance()
                    .setElements(elementsCopy)
                    .setRadices(partition.getRadices())
                    .addFilters(filterSplits[0])
                    .getPartition();

            logger.trace("New partition added: {}", allPartition.printElements());
            newPartitions.add(allPartition);

        } else {
            ElementList[] combinationSplits = partition.getSplits(splitIndex);
            
            for(int i=0;i < filterSplits.length;i++) {
                Collection<FilterList> filterCollection = filterSplits[i];
                ElementList splitCombination = combinationSplits[i];
                Partition splitPartition = PartitionBuilder.newInstance()
                        .setRadices(partition.getRadices())
                        .setElements(splitCombination)
                        .addFilters(filterCollection)
                        .getPartition();

                logger.trace("New partition added: {}", splitPartition.printElements());
                newPartitions.add(splitPartition);
            }
        }

        return newPartitions;
    }
    
//    private boolean matchExists(Partition partition) {
//        
//        if(partition == null) {
//            logger.error("Passed in partition is null!");
//        }
//        
//        if(partition.getFilters() == null) {
//            logger.error("Partiton filters is null!");
//        }
//        
//        ElementList combination = partition.getElements();
//        
//        for(FilterList filter : partition.getFilters()) {
//            Matches matchOutcome = filter.applyMatch(combination);
//            
//            if(matchOutcome == Matches.ENTIRELY) {
//                return true;
//            }
//        }
//        
//        return false;
//    }

    @Override
    public void importPartitions(PartitionExport partitionExport) {
        incompletePartitions = partitionExport.getIncompletePartitions();
        finalCombinations = partitionExport.getCompletePartitions();
        
        incompletePartitions = this.computeConformedPartition(incompletePartitions);
    }

    @Override
    public PartitionExport reset() {
        PartitionExport partitionExport = new PartitionExport(incompletePartitions, finalCombinations);
        incompletePartitions = null;
        finalCombinations = null;
        return partitionExport;
    }

    @Override
    public void setPartitions(Collection<Partition> partitions) {
        incompletePartitions = partitions;
        finalCombinations = new ArrayList<ElementList>();
        
        incompletePartitions = this.computeConformedPartition(incompletePartitions);
    }

    @Override
    public void setPartition(Partition partition) {
        Collection<Partition> newIncompletePartitions = new ArrayList<Partition>();
        newIncompletePartitions.add(partition);
        this.setPartitions(newIncompletePartitions);
    }
    
    //TODO: Find an elegant way to break down this method.
    /*
    
    It must do several things:
    
    1) remove filters that do not match or elements that are immediately filtered.
    2) For elements that partially match on an ALL element, change it to UNSET
    3) For all UNSET elements, add the radix to the uncheckedRadicesMap
    
    */
    protected Collection<Partition> computeConformedPartition(Collection<Partition> nonConformedPartitions) {
        
        logger.trace("Conforming partitions.");
        
        Collection<Partition> conformedPartitions = new ArrayList<Partition>();
        
        Iterator<Partition> partitionIt = nonConformedPartitions.iterator();
        partitionLoop:
        while(partitionIt.hasNext()) {
            Partition partition = partitionIt.next();
            
            ElementList elements = partition.getElements();
            Collection<Integer> badAllRadices = new ArrayList<Integer>();
            
            Collection<FilterList> filters = partition.getFilters();
            Iterator<FilterList> filterIt = filters.iterator();
            
            filterLoop:
            while(filterIt.hasNext()) {
                FilterList filterList = filterIt.next();
                boolean entirelyMatches = true;
                
                for(int i=0; i<elements.getLength(); i++) {
                    Element element = elements.getElement(i);
                    ElementState elementState = element.getElementState();
                    
                    Filter filterElement = filterList.getFilter(i);
                    FilterState filterState = filterElement.getFilterState();
                    if(elementState == ElementState.ALL) {
                        if(filterState == FilterState.ALL) {
                            continue;
                        } else if(filterState == FilterState.ONE) {
                            badAllRadices.add(i);
                            entirelyMatches = false;
                            continue;
                        }
                    } else if(elementState == ElementState.SET) {
                        if(filterState == FilterState.ALL) {
                            continue;
                        } else if(filterState == FilterState.ONE) {
                            if(filterElement.getOrdinal() == element.getOrdinal()) {
                                continue;
                            } else {
                                filterIt.remove();
                                continue filterLoop;
                            }
                        }
                    } else if(elementState == ElementState.UNSET) {
                        if(filterState == filterState.ALL) {
                            continue;
                        } else if(filterState == filterState.ONE) {
                            entirelyMatches = false;
                        }
                    }
                }
                
                if(entirelyMatches) {
                    partitionIt.remove();
                    continue partitionLoop;
                }              
            }
            
            ElementListBuilder conformedElementsBuilder = ElementListBuilder.newInstance()
                    .copy(elements);
            for(int badRadix : badAllRadices) {
                Element unsetElement = new Element(ElementState.UNSET);
                
                conformedElementsBuilder.setElement(badRadix, unsetElement);
            }
            ElementList conformedElements = conformedElementsBuilder.getElementList();
            Partition conformedPartition = PartitionBuilder.newInstance()
                    .setElements(conformedElements)
                    .addFilters(filters)
                    .setRadices(partition.getRadices())
                    .getPartition();
            
            conformedPartitions.add(conformedPartition);
        }
        
        
        for(Partition partition: conformedPartitions) {
            logger.trace("Conformed partition: {}", partition.printElements());
            
            SortedSet<Integer> uncheckedRadicesSet = this.buildUncheckedRadicesSet(partition);
            uncheckedRadicesMap.put(partition.getElements(), uncheckedRadicesSet);
        }
        
        return conformedPartitions;
    } 
    
    private SortedSet<Integer> buildUncheckedRadicesSet(Partition partition) {
        
        ElementState[] elementStates = partition.getElements().getElementStates();
        SortedSet<Integer> uncheckedRadicesSet = new TreeSet<Integer>();
        
        for(int i=0; i<elementStates.length;i++) {
            if(elementStates[i] == ElementState.UNSET) {
                uncheckedRadicesSet.add(i);
            }
        }
        
        return uncheckedRadicesSet;
    }
}
