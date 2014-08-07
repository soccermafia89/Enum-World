/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.processor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ethier.alex.world.addon.ElementListBuilder;
import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */

//TODO: Create an abstract class: base processor instead of extending this class.
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
        logger.info("Running processor.");
//        int tmpCount = 0;
        while(!incompletePartitions.isEmpty()) {
            this.runSet();
//            break;
//            tmpCount++;
//            if(tmpCount > 5) {
//                break;
//            }
        }
        
        logger.info("Finished computing.");
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
        
        // TODO: re-evaluate the usage of unchecked radices.  Is it necessary?
        // What is proper behavior when the uncheckedRadices is empty?
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
//        else {
//            logger.info("Unchecked radices is empty for partition: {}", partition.getElements());
//        }
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
        logger.trace("Splitting partition: {}", partition.getElements());
        Collection<Partition> newPartitions = new ArrayList<Partition>();
        Collection<FilterList> filters = partition.getFilters();
        ElementList elements = partition.getElements();
        
        int radix = partition.getRadices()[splitIndex];
        
        Collection<FilterList> allFilters = new ArrayList<FilterList>();
        Set<Integer> filterOrdinals = new HashSet<Integer>();
        Multimap<Integer, FilterList> filterMap = HashMultimap.create();
        
        for(FilterList filter : filters) {
            
            Filter filterElement = filter.getFilter(splitIndex);
            
            // TODO check checkcount after registering!
            if(filterElement.getFilterState() == FilterState.ALL) {
                allFilters.add(filter);
            } else {
                
                int filterOrdinal = filterElement.getOrdinal();
                
                if(filter.getCheckCount() == 1) {                    
                    filterOrdinals.add(filterOrdinal);
                } else {
                    
                    FilterList newFilter = FilterListBuilder.newInstance()
                            .copy(filter)
                            .getFilterList();

                    newFilter.registerCheck();
                    filterMap.put(filterOrdinal, newFilter);
                }
            }
        }
        
        
        // In the special case that all filters contain a '*' then we don't need to return multiple splits.
        // This is the key to computing quickly.
        if(filterMap.keySet().isEmpty() && filterOrdinals.isEmpty()) {
            Element allElement = new Element(ElementState.ALL);
            ElementList elementsCopy = ElementListBuilder.newInstance()
                    .copy(elements)
                    .setElement(splitIndex, allElement)
                    .getElementList();
                        
            Partition allPartition = PartitionBuilder.newInstance()
                    .setElements(elementsCopy)
                    .setRadices(partition.getRadices())
                    .addFilters(filters)
                    .getPartition();

            logger.trace("New partition added: {}", allPartition.printElements());
//            for(FilterList filter : allPartition.getFilters()) {
//                logger.info("With filter: " + filter + " check count: " + filter.getCheckCount());
//            }
            newPartitions.add(allPartition);
        } else {
            // Otherwise create a new set of partitions containing only the filters that apply to them.
            
            for(int ordinal=0; ordinal<radix;ordinal++) {                
                if(!filterOrdinals.contains(ordinal)) {
                    
                    Collection<FilterList> ordinalFilters = filterMap.get(ordinal);

                    Element newElement = new Element(ordinal);

                    ElementList newSplit = ElementListBuilder.newInstance()
                            .copy(elements)
                            .setElement(splitIndex, newElement)
                            .getElementList();

                    Partition splitPartition = PartitionBuilder.newInstance()
                            .setElements(newSplit)
                            .setRadices(partition.getRadices())
                            .addFilters(allFilters)
                            .addFilters(ordinalFilters)
                            .getPartition();

                    logger.trace("New partition added: {}", splitPartition.printElements());
//                    for(FilterList filter : splitPartition.getFilters()) {
//                        logger.info("With filter: " + filter + " check count: " + filter.getCheckCount());
//                    }
                    newPartitions.add(splitPartition);
                }
            }
        }
        
        return newPartitions;
    }
    
//    private Collection<Partition> splitPartition(Partition partition, int splitIndex) {
//        
//        Collection<Partition> newPartitions = new ArrayList<Partition>();
//        Collection<FilterList> filters = partition.getFilters();
//        
//        int radix = partition.getRadices()[splitIndex];
//        
//        Collection<FilterList>[] filterSplits = new Collection[radix];
//        for(int i=0; i < filterSplits.length;i++) {
//            filterSplits[i] = new ArrayList<FilterList>();
//        }
//
//        boolean allBothFilters = true;
//        for(FilterList filter : filters) {
//            Filter filterElement = filter.getFilter(splitIndex);
//            
//            if(filterElement.getFilterState() == FilterState.ALL) {
//                
//                for(Collection<FilterList> filterSplit : filterSplits) {
//                    filterSplit.add(filter);
//                }
//                
//            } else if(filterElement.getFilterState() == FilterState.ONE) {
//                int ordinal = filterElement.getOrdinal();
//                filterSplits[ordinal].add(filter);
//                
//                allBothFilters = false;
//            } else {
//                throw new RuntimeException("Invalid state, filterBit: {} should have correct FilterElementState." + filterElement);
//            }
//        }
//
//        // In the special case that all filters contain a '*' then we don't need to return multiple splits.
//        // This is the key to computing quickly.
//        if(allBothFilters) {
//            
//            Element allElement = new Element(ElementState.ALL);
//            ElementList elements = partition.getElements();
//            ElementList elementsCopy = ElementListBuilder.newInstance()
//                    .copy(elements)
//                    .setElement(splitIndex, allElement)
//                    .getElementList();
//                        
//            Partition allPartition = PartitionBuilder.newInstance()
//                    .setElements(elementsCopy)
//                    .setRadices(partition.getRadices())
//                    .addFilters(filterSplits[0])
//                    .getPartition();
//
//            logger.trace("New partition added: {}", allPartition.printElements());
//            newPartitions.add(allPartition);
//
//        } else {
//            ElementList[] combinationSplits = partition.getSplits(splitIndex);
//            
//            for(int i=0;i < filterSplits.length;i++) {
//                Collection<FilterList> filterCollection = filterSplits[i];
//                ElementList splitCombination = combinationSplits[i];
//                Partition splitPartition = PartitionBuilder.newInstance()
//                        .setRadices(partition.getRadices())
//                        .setElements(splitCombination)
//                        .addFilters(filterCollection)
//                        .getPartition();
//                
//                // Rather than continuing to split partitions, we should check to see if any filters already match
//                // This way we can remove partitions early.
//                if(!matchExists(splitPartition, uncheckedRadicesMap.get(partition.getElements()))) {
//                    logger.trace("New partition added: {}", splitPartition.printElements());
//                    newPartitions.add(splitPartition);
//                } 
//            }
//        }
//
//        return newPartitions;
//    }
    
    // TODO: Create a new uncheckedRadicesMap that uses both the elementList and Filter list as the key
    // This will prevent duplicate checks.
    private boolean matchExists(Partition partition, Collection<Integer> uncheckedRadices) {
        
        ElementList elements = partition.getElements();
        
        FilterLoop:
        for(FilterList filterList : partition.getFilters()) {
            boolean matches = true;
            
            for(int uncheckedRadix : uncheckedRadices) {
                Element element = elements.getElement(uncheckedRadix);
                Filter filter = filterList.getFilter(uncheckedRadix);
                
                if(filter.getFilterState() == FilterState.ALL) {
                    continue;
                } else if(element.getOrdinal() != filter.getOrdinal()) {
                    matches = false;
                    break;
                }
            }
            
            if(matches) {
                return true;
            }
        }
        
        return false;
    }

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
