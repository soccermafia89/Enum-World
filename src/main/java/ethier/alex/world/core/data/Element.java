/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**

 @author alex
 */
public class Element implements Numeral {

    private static Logger logger = LogManager.getLogger(Element.class);
    private ElementState elementState;
    private int ordinal; // The ordinal value.

    public Element(Enum myElementState) {
        if (myElementState == ElementState.SET) {
            throw new RuntimeException("Must use constructor with ordinal of elementState == 'SET'");
        }

        elementState = (ElementState)myElementState;
        ordinal = elementState.comparator();
    }

    public Element(int myOrdinal) {
        elementState = ElementState.SET;
        ordinal = myOrdinal;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    public ElementState getElementState() {
        return elementState;
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
