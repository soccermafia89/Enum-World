/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Iterator;

/**

 @author alex
 */
public class NumeralArrayIterator implements Iterator {
    
    int index = 0;
    Numeral[] numerals;
    
    public NumeralArrayIterator(Numeral[] myNumerals) {
        numerals = myNumerals;
    }

    @Override
    public boolean hasNext() {
        if(index < numerals.length) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Numeral next() {
        Numeral toReturn = numerals[index];
        index++;
        
        return toReturn;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from ElementArrays.");
    }
}
