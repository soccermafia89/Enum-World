/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Arrays;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
// Wraps an Numeral[] as a class
public class FilterList implements Iterable {

    private static Logger logger = Logger.getLogger(FilterList.class);
    private FilterElement[] filterElements;

    public FilterList(FilterElement[] myFilterElements) {
        filterElements = myFilterElements;
    }

    public int getLength() {
        return filterElements.length;
    }

    public int[] getOrdinals() {
        int[] ordinals = new int[filterElements.length];
        for (int i = 0; i < filterElements.length; i++) {
            ordinals[i] = this.getFilter(i).getOrdinal();
        }

        return ordinals;
    }

    public FilterElementState[] getFilterStates() {
        FilterElementState[] filterStates = new FilterElementState[filterElements.length];
        for (int i = 0; i < filterElements.length; i++) {
            filterStates[i] = this.getFilter(i).getFilterState();
        }
        
        return filterStates;
    }

    public FilterElement getFilter(int i) {
        return filterElements[i];
    }

    public FilterList copy() {
        FilterElement[] newFilterElementArray = Arrays.copyOf(filterElements, filterElements.length);
        return new FilterList(newFilterElementArray);
    }

    public void set(int i, FilterElement filterElement) {
        filterElements[i] = filterElement;
    }

    @Override
    public Iterator<FilterElement> iterator() {
        return new NumeralArrayIterator(filterElements);
    }

//    public Matches getMatch(ElementList elementList, int splitIndex) {
//
//        //TODO: Instead of constantly retesting for matching, save the match results within each filter.
//        //Only retest matching on split indexes.
//        int start;
//        if (splitIndex == -1) {
//            start = 0;
//        } else {
//            start = splitIndex;
//        }
//
//        boolean possibleMatch = false;
//
//        for (int i = start; i < filterElements.length; i++) {
//            
//            FilterElementState filterElementState = filterElements[i].getFilterState();
//            ElementState elementState = elementList.getElement(i).getElementState();
//
//
//            //Order of if statements matters!
//            //1) If either has a Both then they match
//            //2) If 1 is false, and the element is UNSET it is now a part match.
//            //3) If the bits match, then they still match.
//            //4) Otherwise they completely don't match.
//            if (filterElementState == FilterElementState.ALL || elementState == ElementState.ALL) {
//                continue;
//            } else if (elementState == ElementState.UNSET) {
//                possibleMatch = true; // No longer a match, only a possible match.
//            } else {
//                int firstOrdinal = this.getFilter(i).getOrdinal();
//                int secondOrdinal = elementList.getElement(i).getOrdinal();
//
//                if (firstOrdinal == secondOrdinal) {
//                    continue;
//                } else {
//                    return Matches.NO;
//                }
//
//            }
//        }
//
//        if (possibleMatch) {
//            return Matches.PARTLY;
//        } else {
//            return Matches.ENTIRELY;
//        }
//    }

    public String toString() {
        return Arrays.toString(filterElements);
    }
}
