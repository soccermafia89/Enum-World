/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import ethier.alex.world.core.data.NumeralArray;
import ethier.alex.world.core.data.Partition;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class PartitionBuilder {

    private static Logger logger = Logger.getLogger(PartitionBuilder.class);
    
    private Partition partition;
    private Collection<NumeralArray> filters;
    private NumeralArray elements;

    public static PartitionBuilder newInstance() {
        return new PartitionBuilder();
    }

    public PartitionBuilder() {
        filters = new ArrayList();
    }

    public PartitionBuilder setRadices(int[] myRadices) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        elements = NumeralArrayBuilder.newInstance()
                .setRadices(myRadices)
                .setBlankWorld()
                .getNumeralArray();        

        return this;
    }

    public PartitionBuilder addFilter(NumeralArray newFilter) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.add(newFilter);

        return this;
    }

    public PartitionBuilder addFilters(Collection<NumeralArray> filters) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.addAll(filters);

        return this;
    }

    public Partition getPartition() {

        if (partition != null) {
            return partition;
        } else {
            if (elements == null) {
                throw new RuntimeException("PartitionBuilder does not have valid bit list set.");
            } else {
                
                partition = new Partition(elements, filters);
                return partition;
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (elements != null) {
            stringBuilder.append("Combination: " + elements.toString() + "\n");
        } else {
            stringBuilder.append("Combination: null\n");
        }

        for (NumeralArray filter : filters) {
            stringBuilder.append(filter.toString() + "\n");
        }

        return stringBuilder.toString();
    }
}
