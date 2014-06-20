/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class Partition {
    
    private static Logger logger = Logger.getLogger(Partition.class);

    ElementList elements;
    Collection<FilterList> filters;
    private int splitIndex = -1;
    private int[] radices;

    public Partition(int[] myRadices, ElementList myElements, Collection<FilterList> myFilters) {
        elements = myElements;
        filters = myFilters;
        radices = myRadices;

        for (int i = 0; i < elements.getLength(); i++) {

            Element element = elements.getElement(i);
            if (splitIndex < 0 && element.getElementState() == ElementState.UNSET) {
                splitIndex = i;
            }
        }
    }
    
    public int[] getRadices() {
        return radices;
    }
    
    public Collection<FilterList> getFilters() {
        return filters;
    }

    public ElementList getElements() {
        return elements;
    }
    
    public int getSplitIndex() {
        return splitIndex;
    }
    
    public ElementList[] getSplits() {
        int radix = radices[splitIndex];
        
        ElementList[] elementSplits = new ElementList[radix];
        
        for(int i=0;i < radix;i++) {
            ElementList newSplit = elements.copy();
            Element newElement = new Element(i);
            newSplit.set(splitIndex, newElement);
            
            elementSplits[i] = newSplit;
        }
        
        return elementSplits;
    }

    public boolean hasSplit() {
//        logger.info("elements: " + elements.toString());
        if (splitIndex > -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public String printElements() {
        StringBuilder stringBuilder = new StringBuilder();
        
        Iterator<Element> it = elements.iterator();
        while(it.hasNext()) {
            Numeral numeral = it.next();
            stringBuilder.append(numeral.toString());
        }
        
        return stringBuilder.toString();
    }
    
    public boolean verifyIntegrity() {
        for(FilterList filter : filters) {
            if(filter.checkMatch(elements) != Matches.PARTLY) {
                return false;
            }
        }
        return true;
    }

}
