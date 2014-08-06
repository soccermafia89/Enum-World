/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.FilterList;
import ethier.alex.world.core.data.Partition;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class PartitionBuilder {

    private static Logger logger = LogManager.getLogger(PartitionBuilder.class);
    private Partition partition;
    private Collection<FilterList> filters;
    private ElementList elements;
    private int[] radices;
//    private Set<Integer> checkedRadices;
    private boolean isBlankWorld = false;

    public static PartitionBuilder newInstance() {
        return new PartitionBuilder();
    }

    public PartitionBuilder() {
        filters = new ArrayList();
    }
    
    public PartitionBuilder setElements(ElementList myElements) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        elements = myElements;
        return this;
    }

    public PartitionBuilder setRadices(int[] myRadices) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        radices = myRadices;

        return this;
    }

    public PartitionBuilder setBlankWorld() {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        isBlankWorld = true;

        return this;
    }

    public PartitionBuilder addFilter(FilterList newFilter) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.add(newFilter);

        return this;
    }

    public PartitionBuilder addFilters(Collection<FilterList> newFilters) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.addAll(newFilters);

        return this;
    }

    public Partition getPartition() {
        
        if(radices == null) {
            throw new RuntimeException("PartitionBuilder does not have valid radices set.");
        }

        if (partition != null) {
            return partition;
        } else {
            if(isBlankWorld) {
                
                elements = ElementListBuilder.newInstance()
                        .setBlankWorld(radices.length)
                        .getElementList();
            }
            
            if (elements == null) {
                throw new RuntimeException("PartitionBuilder does not have valid elements list set.");
            } else if(filters == null) {
                throw new RuntimeException("PartitionBuilder does not have valid filters list set.");
            } else {
                
//                if(checkedRadices == null) {
//                    checkedRadices = new HashSet<Integer>();
//                }
                
//                partition = new Partition(radices, elements, filters, checkedRadices);
                partition = new Partition(radices, elements, filters);
                return partition;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (elements != null) {
            stringBuilder.append("Combination: ").append(elements.toString()).append("\n");
        } else {
            stringBuilder.append("Combination: null\n");
        }

        for (FilterList filter : filters) {
            stringBuilder.append(filter.toString()).append("\n");
        }

        return stringBuilder.toString();
    }
}
