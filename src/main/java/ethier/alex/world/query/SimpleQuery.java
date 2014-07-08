/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.query;

import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.SimpleProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**

 @author alex
 */
public class SimpleQuery implements Query {

    private static Logger logger = Logger.getLogger(SimpleQuery.class);
    Collection<ElementList> elements;
    int[] radices;
    BigDecimal worldSize;

    public SimpleQuery(int[] myRadices, Collection<ElementList> myElements) {
        radices = myRadices;
        elements = myElements;

        worldSize = BigDecimal.valueOf(0L);
        for (ElementList elementList : myElements) {

            BigDecimal weight = BigDecimal.valueOf(1L);
            for (int i = 0; i < elementList.getLength(); i++) {
                Element element = elementList.getElement(i);

                if (element.getElementState() == ElementState.ALL) {
                    weight = weight.multiply(BigDecimal.valueOf(radices[i]));
                }
            }

            worldSize = worldSize.add(weight);
        }

    }

    public long getWorldSize() {
        return worldSize.longValueExact();
    }

    public double query(FilterList filter) {
        Collection<FilterList> filters = new ArrayList<FilterList>();
        filters.add(filter);
        return query(filters);
    }

    public double query(Collection<FilterList> filters) {
        BigDecimal score = BigDecimal.valueOf(0L);

        for (ElementList element : elements) {

            score = score.add(this.getWeightedMatch(filters, element));
        }

        return score.divide(worldSize, 10, RoundingMode.UP).doubleValue();
    }

    private BigDecimal getWeightedMatch(Collection<FilterList> filterLists, ElementList elementList) {

        BigDecimal weight = BigDecimal.valueOf(1L);

        for (int i = 0; i < radices.length; i = i + 1) {

            Collection<Filter> filters = new ArrayList<Filter>();
            for (FilterList filterList : filterLists) {
                filters.add(filterList.getFilter(i));
            }

            int elementWeight = getUnionMaxWeight(filters, elementList.getElement(i), i);

            if (elementWeight == 0) {
                return BigDecimal.valueOf(0L);
            } else {
                weight = weight.multiply(BigDecimal.valueOf(elementWeight));
            }
        }

        return weight;
    }

    private int getUnionMaxWeight(Collection<Filter> filters, Element element, int matchPos) {

        boolean allFilterPresent = false;
        Set<Integer> filterOrdinals = new HashSet<Integer>();
        for (Filter filter : filters) {

            if (filter.getFilterState() == FilterState.ALL) {
                allFilterPresent = true;
                break;
            }

            filterOrdinals.add(filter.getOrdinal());
        }

        if (allFilterPresent && element.getElementState() == ElementState.ALL) {
            return radices[matchPos];
        } else if (allFilterPresent && element.getElementState() == ElementState.SET) {
            return 1;
        } else if (!allFilterPresent && element.getElementState() == ElementState.ALL) {
            return filterOrdinals.size();
        } else if (!allFilterPresent && element.getElementState() == ElementState.SET) {
            for (int filterOrdinal : filterOrdinals) {
                if (filterOrdinal == element.getOrdinal()) {
                    return 1;
                }
            }

            return 0;
        } else {
            throw new RuntimeException("Invalid State During Query Pos: + " + matchPos
                    + " Element State: " + element.getElementState()
                    + " Filters: " + filters);
        }
    }
}
