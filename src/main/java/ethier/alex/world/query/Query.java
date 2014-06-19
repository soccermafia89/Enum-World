/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.query;

import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.SimpleProcessor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class Query {

    private static Logger logger = Logger.getLogger(SimpleProcessor.class);
    Collection<ElementList> elements;
    int[] radices;
    BigDecimal worldSize;

    public Query(int[] myRadices, Collection<ElementList> myElements) {
        radices = myRadices;
        elements = myElements;

        worldSize = BigDecimal.valueOf(0L);
        for (ElementList elementList : myElements) {

            BigDecimal weight = BigDecimal.valueOf(1L);
            for (int i = 0; i < elementList.getLength(); i++) {
                Element element = elementList.getElement(i);

                if (element.getElementState() == ElementState.ALL) {
//                    weight = weight * radices[i];
                    weight = weight.multiply(BigDecimal.valueOf(radices[i]));
                }
            }

            worldSize = worldSize.add(weight);
//            logger.info("World weight for: " + elementList + " is: " + weight);
        }

//        logger.info("Radices: " + Arrays.toString(radices));
//        logger.info("Query World Size: " + worldSize);
    }

    public double query(FilterList filter) {
        BigDecimal score = BigDecimal.valueOf(0L);

        for (ElementList element : elements) {

            score = score.add(this.getWeightedMatch(filter, element));
        }

        logger.info("Query Score: " + score + " World Size: " + worldSize + " max value: " + Long.MAX_VALUE);
        return score.divide(worldSize, 10, RoundingMode.UP).doubleValue();

    }
    
//      TODO: In order to calculate this properly we have to substract the union of the filters, not an easy task.
//    public double query(Collection<FilterList> filters) {
//    }

    private BigDecimal getWeightedMatch(FilterList filterList, ElementList elementList) {

        BigDecimal weight = BigDecimal.valueOf(1L);

        for (int i = 0; i < filterList.getLength(); i++) {
            Filter filter = filterList.getFilter(i);
            Element element = elementList.getElement(i);

            if (element.getElementState() == ElementState.ALL) {
                if (filter.getFilterState() == FilterState.ALL) {
                    weight = weight.multiply(BigDecimal.valueOf(radices[i]));
                }
            } else if (element.getElementState() == ElementState.SET) {
                if (filter.getFilterState() == FilterState.ONE && filter.getOrdinal() != element.getOrdinal()) {
                    return BigDecimal.valueOf(0L);
                }
            } else {
                throw new RuntimeException("Invalid element state in query: " + element.getElementState());
            }
        }

//        logger.info("Weighteed match for: " + filterList + " => " + elementList + " is: " + weight);

        return weight;
    }
}
