/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Arrays;
import java.util.Iterator;

/**

 @author alex
 */
// Wraps an Numeral[] as a class
public class NumeralArray implements Iterable {

    private Numeral[] numeralArray;

    public NumeralArray(Numeral[] myNumeralArray) {
        numeralArray = myNumeralArray;
    }

    public int getLength() {
        return numeralArray.length;
    }

    public Numeral get(int i) {
        return numeralArray[i];
    }

    public NumeralArray copy() {
        Numeral[] newElementArray = Arrays.copyOf(numeralArray, numeralArray.length);
        return new NumeralArray(newElementArray);
    }

    public void set(int i, Element element) {
        numeralArray[i] = element;
    }

    @Override
    public Iterator<Numeral> iterator() {
        return new NumeralArrayIterator(numeralArray);
    }

    public Matches getMatch(NumeralArray second, int splitIndex) {

        boolean possibleMatch = false;

        for (int i = splitIndex; i < numeralArray.length; i++) {
            Numeral firstNumeral = numeralArray[i];
            Numeral secondNumeral = second.get(i);

            Enum firstState = firstNumeral.getState();
            Enum secondState = secondNumeral.getState();

            Enum firstAll;
            Enum firstUnset = null;
            if (firstState instanceof ElementState) {
                firstAll = ElementState.ALL;
                firstUnset = ElementState.UNSET;
            } else if (firstState instanceof FilterElementState) {
                firstAll = FilterElementState.ALL;
            } else {
                throw new RuntimeException("Invalid match enum passed: " + firstState);
            }

            Enum secondAll;
            Enum secondUnset = null;
            if (secondState instanceof ElementState) {
                secondAll = ElementState.ALL;
                secondUnset = ElementState.UNSET;
            } else if (secondState instanceof FilterElementState) {
                secondAll = FilterElementState.ALL;
            } else {
                throw new RuntimeException("Invalid match enum passed: " + secondState);
            }

            //Order of if statements matters!
            //1) If either has a Both then they match
            //2) If 1 is false, and either has an UNSET it is now a part match.
            //3) If the bits match, then they still match.
            //4) Otherwise they completely don't match.
            if (firstState == firstAll || secondState == secondAll) {
                continue;
            } else if (firstState == firstUnset || secondState == secondUnset) {
                possibleMatch = true; // No longer a match, only a possible match.
            } else {
                int firstOrdinal = firstNumeral.getOrdinal();
                int secondOrdinal = secondNumeral.getOrdinal();

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
    
    public String toString() {
        return Arrays.toString(numeralArray);
    }
}
