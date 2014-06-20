/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.*;
import org.apache.log4j.Logger;

/**

 @author alex
 */
// Wraps an Numeral[] as a class
public class FilterList {

    private static Logger logger = Logger.getLogger(FilterList.class);
    private Set<Integer> unmatchedFilterIndex;
    private Filter[] filters;
//    private Filter[] filterElements;

    public FilterList(Filter[] myFilterElements) {
        unmatchedFilterIndex = new HashSet<Integer>();

        for (int i = 0; i < myFilterElements.length; i++) {
            if (myFilterElements[i].getFilterState() != FilterState.ALL) {
                unmatchedFilterIndex.add(i);
            }
        }

        filters = myFilterElements;
    }

    public int getLength() {
        return filters.length;
    }

    public int[] getOrdinals() {
        int[] ordinals = new int[filters.length];
        for (int i = 0; i < filters.length; i++) {
            ordinals[i] = this.getFilter(i).getOrdinal();
        }

        return ordinals;
    }

    public FilterState[] getFilterStates() {
        FilterState[] filterStates = new FilterState[filters.length];
        for (int i = 0; i < filters.length; i++) {
            filterStates[i] = this.getFilter(i).getFilterState();
        }

        return filterStates;
    }

    public Filter getFilter(int i) {
        return filters[i];
    }

    public Matches applyMatch(ElementList elementList) {

        boolean possibleMatch = false;

//        for (int i = start; i < filters.length; i++) {
        Iterator<Integer> it = unmatchedFilterIndex.iterator();
        while(it.hasNext()) {
            
            int i = it.next();

            ElementState elementState = elementList.getElement(i).getElementState();

            //Order of if statements matters!
            //1) If either has a Both then they match
            //2) If 1 is false, and the element is UNSET it is now a part match.
            //3) If the bits match, then they still match.
            //4) Otherwise they completely don't match.
            if (elementState == ElementState.ALL) {
                continue;
            } else if (elementState == ElementState.UNSET) {
                possibleMatch = true; // No longer a match, only a possible match.
            } else {
                int firstOrdinal = this.getFilter(i).getOrdinal();
                int secondOrdinal = elementList.getElement(i).getOrdinal();

                if (firstOrdinal == secondOrdinal) {
                    it.remove();
                    continue;
                } else {
                    return Matches.NO;
                }

            }
        }

        if (possibleMatch) {
            return Matches.PARTLY;
        } else {
            return Matches.ENTIRELY;
        }
    }
    
    public Matches checkMatch(ElementList elementList) {

        boolean possibleMatch = false;

//        for (int i = start; i < filters.length; i++) {
        for (int i : unmatchedFilterIndex) {

            ElementState elementState = elementList.getElement(i).getElementState();

            //Order of if statements matters!
            //1) If either has a Both then they match
            //2) If 1 is false, and the element is UNSET it is now a part match.
            //3) If the bits match, then they still match.
            //4) Otherwise they completely don't match.
            if (elementState == ElementState.ALL) {
                continue;
            } else if (elementState == ElementState.UNSET) {
                possibleMatch = true; // No longer a match, only a possible match.
            } else {
                int firstOrdinal = this.getFilter(i).getOrdinal();
                int secondOrdinal = elementList.getElement(i).getOrdinal();

                if (firstOrdinal == secondOrdinal) {
                    continue;
                } else {
                    return Matches.NO;
                }

            }
        }

        if (possibleMatch) {
            return Matches.PARTLY;
        } else {
            return Matches.ENTIRELY;
        }
    }

    @Override
    public String toString() {
        String[] copy = new String[filters.length];
        
        for(int i=0;i < filters.length; i++) {
            Filter filter = filters[i];
            
            if(filter.getFilterState() == FilterState.ALL) {
                copy[i] = "*";
            } else {
                copy[i] = "" + filter.getOrdinal();
            }
        }
        
        return Arrays.toString(copy);
    }
}
