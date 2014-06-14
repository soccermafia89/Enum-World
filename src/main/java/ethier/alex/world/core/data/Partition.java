/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**

 @author alex
 */
public class Partition {

    NumeralArray elements;
    Collection<NumeralArray> filters;
    private int splitIndex = -1;

    public Partition(NumeralArray myElements, Collection<NumeralArray> myFilters) {
        elements = myElements;
        filters = myFilters;

        for (int i = 0; i < elements.getLength(); i++) {

            Numeral element = elements.get(i);
            if (splitIndex < 0 && element.getState() == ElementState.UNSET) {
                splitIndex = i;
            }
        }
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
        Numeral elementSplit = elements.get(splitIndex);
        int radix = elementSplit.getRadix();
        
        NumeralArray[] elementSplits = new NumeralArray[radix];
        
        for(int i=0;i < radix;i++) {
            NumeralArray newSplit = elements.copy();
            Element newElement = new Element(elementSplit.getRadix(), i, ElementState.SET);
            newSplit.set(i, newElement);
            
            elementSplits[i] = newSplit;
        }
        
        return elementSplits;
    }

    public boolean hasSplit() {
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
            Element element = it.next();
            stringBuilder.append(element.toString());
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
