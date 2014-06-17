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

    NumeralArray elements;
    Collection<NumeralArray> filters;
    private int splitIndex = -1;
    private int[] radices;

    public Partition(int[] myRadices, NumeralArray myElements, Collection<NumeralArray> myFilters) {
        elements = myElements;
        filters = myFilters;
        radices = myRadices;

        for (int i = 0; i < elements.getLength(); i++) {

            Numeral element = elements.get(i);
            if (splitIndex < 0 && element.getState() == ElementState.UNSET) {
                splitIndex = i;
            }
        }
    }
    
    public int[] getRadices() {
        return radices;
    }
    
    public Collection<NumeralArray> getFilters() {
        return filters;
    }

    public NumeralArray getElements() {
        return elements;
    }
    
    public int getSplitIndex() {
        return splitIndex;
    }
    
    public NumeralArray[] getSplits() {
        int radix = radices[splitIndex];
        
        NumeralArray[] elementSplits = new NumeralArray[radix];
        
        for(int i=0;i < radix;i++) {
            NumeralArray newSplit = elements.copy();
            Element newElement = new Element(i);
            newSplit.set(splitIndex, newElement);
            
            elementSplits[i] = newSplit;
        }
        
        return elementSplits;
    }

    public boolean hasSplit() {
        logger.info("elements: " + elements.toString());
        
        if (splitIndex > -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public String printElements() {
        StringBuilder stringBuilder = new StringBuilder();
        
        Iterator<Numeral> it = elements.iterator();
        while(it.hasNext()) {
            Numeral numeral = it.next();
            stringBuilder.append(numeral.toString());
        }
        
        return stringBuilder.toString();
    }
    
    public boolean verifyIntegrity() {
        for(NumeralArray filter : filters) {
            if(elements.getMatch(filter, splitIndex) != Matches.PARTLY) {
                return false;
            }
        }
        return true;
    }

}
