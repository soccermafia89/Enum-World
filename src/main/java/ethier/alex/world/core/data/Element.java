/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import org.apache.log4j.Logger;

/**

 @author alex
 */
public class Element implements Numeral {

    private static Logger logger = Logger.getLogger(Element.class);
    private ElementState elementState;
    private int ordinal; // The ordinal value.
    private int radix; // Total number of possible ordinals.

    public Element(int myRadix, Enum myElementState) {
        if (myElementState == ElementState.SET) {
            throw new RuntimeException("Must use constructor with ordinal of elementState == 'SET'");
        }

        elementState = (ElementState)myElementState;
        radix = myRadix;
        // The ordinal doesn't matter if the element state is 'ALL' 
    }

    public Element(int myRadix, int myOrdinal) {
        elementState = ElementState.SET;
        radix = myRadix;
        ordinal = myOrdinal;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public Enum getState() {
        return elementState;
    }

    @Override
    public int getRadix() {
        return radix;
    }

    @Override
    public String toString() {
        if(elementState == ElementState.ALL) {
            return "*";
        } else if(elementState == ElementState.UNSET) {
            return "-";
        } else if(elementState == ElementState.SET) {
            return "" + ordinal;
        }
            
        throw new RuntimeException("Invalid state reached.");
    }
}
