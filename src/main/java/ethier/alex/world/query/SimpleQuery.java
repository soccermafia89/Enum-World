/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.query;

import ethier.alex.world.addon.ElementListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.Processor;
import ethier.alex.world.core.processor.SimpleProcessor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class SimpleQuery implements Query {

    private static Logger logger = LogManager.getLogger(SimpleQuery.class);
    Collection<ElementList> elements;
    int[] radices;
    BigDecimal worldSize;

    public SimpleQuery(int[] myRadices, Collection<ElementList> myElements) {
        radices = myRadices;
        elements = myElements;
        
        worldSize = this.computeWorldSize(myElements);
    }
    
    private BigDecimal computeWorldSize(Collection<ElementList> myElements) {
        BigDecimal newWorldSize = BigDecimal.valueOf(0L);
        for (ElementList elementList : myElements) {

            BigDecimal weight = BigDecimal.valueOf(1L);
            for (int i = 0; i < elementList.getLength(); i++) {
                Element element = elementList.getElement(i);

                if (element.getElementState() == ElementState.ALL) {
                    weight = weight.multiply(BigDecimal.valueOf(radices[i]));
                }
            }

            newWorldSize = newWorldSize.add(weight);
        }
        
        return newWorldSize;
    }

    @Override
    public BigDecimal getWorldSize() {
        return worldSize;
    }

    @Override
    public double query(FilterList filter) {
        Collection<FilterList> filters = new ArrayList<FilterList>();
        filters.add(filter);
        return query(filters);
    }

    @Override
    public double query(Collection<FilterList> filters) {
        
        Collection<Partition> partitions = new ArrayList<Partition>();
        outerLoop:
        for(ElementList element : elements) {
            
//            ElementList partitionElement = this.transformElements(element);
//            Collection<FilterList> partitionFilters = new ArrayList<FilterList>();
//            for(FilterList filter : filters) {
//                Matches match = filter.applyMatch(partitionElement);
//                if(match == Matches.ENTIRELY) {
//                    logger.info("Filter " + filter + " matches entirely with " + element);
//                    continue outerLoop;
//                } else if(match == Matches.PARTLY) {
//                    logger.info("Filter " + filter + " matches partly with " + element);
//                    partitionFilters.add(filter);
//                }
//            }
            
            Partition queryPartition = PartitionBuilder.newInstance()
                    .addFilters(filters)
                    .setElements(element)
                    .setRadices(radices)
                    .getPartition();
            
            partitions.add(queryPartition);
            
            logger.info("Adding query partition: {}", queryPartition.printElements());
        }
        
        Processor processor = new SimpleProcessor();
        processor.setPartitions(partitions);
        logger.info("Running query processor.");
        processor.runAll();
        
        BigDecimal newWorldSize = this.computeWorldSize(processor.getCompletedPartitions());
        logger.info("Query new world size: {}", newWorldSize);
        BigDecimal score = (worldSize.subtract(newWorldSize)).divide(worldSize, 10, RoundingMode.UP);
        return score.doubleValue();
        
//        BigDecimal score = BigDecimal.valueOf(0L);
//
//        for (ElementList element : elements) {
//            BigDecimal weightedMatch = this.getWeightedMatch(filters, element);
//            logger.info("Element: " + element + " has weight: " + weightedMatch.toPlainString());
//            score = score.add(weightedMatch);
//        }
//
//        return score.divide(worldSize, 10, RoundingMode.UP).doubleValue();
    }
    
//    // Swap all ALL states to UNSET states, for query processing.
//    private ElementList transformElements(ElementList elements) {
//        
//        ElementListBuilder elementsCopy = ElementListBuilder.newInstance().copy(elements);
//        for(int i=0; i < elements.getLength(); i++) {
//            Element element = elements.getElement(i);
//            
//            if(element.getElementState() == ElementState.ALL) {
//                Element unsetElement = new Element(ElementState.UNSET);
//                elementsCopy.setElement(i, unsetElement);
//            }
//        }
//        
//        return elementsCopy.getElementList();
//    }
    
//    private BigDecimal getWeightedMatch(Collection<FilterList> filterLists, ElementList elementList) {
//
//        BigDecimal weight = BigDecimal.valueOf(1L);
//
//        for (int i = 0; i < radices.length; i = i + 1) {
//
//            Collection<Filter> filters = new ArrayList<Filter>();
//            for (FilterList filterList : filterLists) {
//                filters.add(filterList.getFilter(i));
//            }
//
//            int elementWeight = getUnionMaxWeight(filters, elementList.getElement(i), i);
//
//            if (elementWeight == 0) {
//                return BigDecimal.valueOf(0L);
//            } else {
//                weight = weight.multiply(BigDecimal.valueOf(elementWeight));
//            }
//        }
//
//        return weight;
//    }
//
//    private int getUnionMaxWeight(Collection<Filter> filters, Element element, int matchPos) {
//
//        boolean allFilterPresent = false;
//        Set<Integer> filterOrdinals = new HashSet<Integer>();
//        for (Filter filter : filters) {
//
//            if (filter.getFilterState() == FilterState.ALL) {
//                allFilterPresent = true;
//                break;
//            }
//
//            filterOrdinals.add(filter.getOrdinal());
//        }
//
//        if (allFilterPresent && element.getElementState() == ElementState.ALL) {
//            return radices[matchPos];
//        } else if (allFilterPresent && element.getElementState() == ElementState.SET) {
//            return 1;
//        } else if (!allFilterPresent && element.getElementState() == ElementState.ALL) {
//            return filterOrdinals.size();
//        } else if (!allFilterPresent && element.getElementState() == ElementState.SET) {
//            for (int filterOrdinal : filterOrdinals) {
//                if (filterOrdinal == element.getOrdinal()) {
//                    return 1;
//                }
//            }
//
//            return 0;
//        } else {
//            throw new RuntimeException("Invalid State During Query Pos: + " + matchPos
//                    + " Element State: " + element.getElementState()
//                    + " Filters: " + filters);
//        }
//    }
}
