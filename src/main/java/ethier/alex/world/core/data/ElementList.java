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
public class ElementList implements Iterable {

    private static Logger logger = Logger.getLogger(ElementList.class);
    private Element[] elementArray;

    public ElementList(Element[] myElementArray) {
        elementArray = myElementArray;
    }

    public int getLength() {
        return elementArray.length;
    }

    public int[] getOrdinals() {
        int[] ordinals = new int[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            ordinals[i] = this.getElement(i).getOrdinal();
        }

        return ordinals;
    }

    public ElementState[] getStates() {
        ElementState[] elementStates = new ElementState[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            elementStates[i] = (ElementState)this.getElement(i).getElementState();
        }
        
        return elementStates;
    }

    public Element getElement(int i) {
        return elementArray[i];
    }

    public ElementList copy() {
        Element[] newElementArray = Arrays.copyOf(elementArray, elementArray.length);
        return new ElementList(newElementArray);
    }

    public void set(int i, Element element) {
        elementArray[i] = element;
    }

    @Override
    public Iterator<Element> iterator() {
        return new NumeralArrayIterator(elementArray);
    }
    
    public Matches getMatch(FilterList filterList, int splitIndex) {

        //TODO: Instead of constantly retesting for matching, save the match results within each filter.
        //Only retest matching on split indexes.
        int start;
        if (splitIndex == -1) {
            start = 0;
        } else {
            start = splitIndex;
        }

        boolean possibleMatch = false;

        for (int i = start; i < elementArray.length; i++) {
            
            FilterElementState filterElementState = filterList.getFilter(i).getFilterState();
            ElementState elementState = this.getElement(i).getElementState();


            //Order of if statements matters!
            //1) If either has a Both then they match
            //2) If 1 is false, and the element is UNSET it is now a part match.
            //3) If the bits match, then they still match.
            //4) Otherwise they completely don't match.
            if (filterElementState == FilterElementState.ALL || elementState == ElementState.ALL) {
                continue;
            } else if (elementState == ElementState.UNSET) {
                possibleMatch = true; // No longer a match, only a possible match.
            } else {
                int firstOrdinal = this.getElement(i).getOrdinal();
                int secondOrdinal = filterList.getFilter(i).getOrdinal();

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

//    public Matches getMatch(FilterList filterList, int splitIndex) {
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
//        for (int i = start; i < elementArray.length; i++) {
//
//            Numeral firstNumeral = elementArray[i];
//            Numeral secondNumeral = filterList.getFilter(i);
//
//            Enum firstState = firstNumeral.getState();
//            Enum secondState = secondNumeral.getState();
//
//            Enum firstAll;
//            Enum firstUnset = null;
//            if (firstState instanceof ElementState) {
//                firstAll = ElementState.ALL;
//                firstUnset = ElementState.UNSET;
//            } else if (firstState instanceof FilterElementState) {
//                firstAll = FilterElementState.ALL;
//            } else {
//                throw new RuntimeException("Invalid match enum passed: " + firstState);
//            }
//
//            Enum secondAll;
//            Enum secondUnset = null;
//            if (secondState instanceof ElementState) {
//                secondAll = ElementState.ALL;
//                secondUnset = ElementState.UNSET;
//            } else if (secondState instanceof FilterElementState) {
//                secondAll = FilterElementState.ALL;
//            } else {
//                throw new RuntimeException("Invalid match enum passed: " + secondState);
//            }
//
//            //Order of if statements matters!
//            //1) If either has a Both then they match
//            //2) If 1 is false, and either has an UNSET it is now a part match.
//            //3) If the bits match, then they still match.
//            //4) Otherwise they completely don't match.
//            if (firstState == firstAll || secondState == secondAll) {
//                continue;
//            } else if (firstState == firstUnset || secondState == secondUnset) {
//                possibleMatch = true; // No longer a match, only a possible match.
//            } else {
//                int firstOrdinal = firstNumeral.getOrdinal();
//                int secondOrdinal = secondNumeral.getOrdinal();
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
        return Arrays.toString(elementArray);
    }
}
