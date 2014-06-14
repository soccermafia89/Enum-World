/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */

public class Element implements Numeral {
    
    private ElementState elementState;
    private int ordinal; // The ordinal value.
    private int radix; // Total number of possible ordinals.
    
    public Element(int myRadix, ElementState myElementState) {
        if(myElementState == ElementState.ALL) {
            elementState = ElementState.ALL;
            int radix = myRadix;
            // The ordinal doesn't matter if the element state is 'ALL' 
        } else {
            throw new RuntimeException("Can only construct Element(ElementState elementState) if the state is 'ALL'.");
        }
    }
    
    public Element(int myRadix, int myOrdinal, ElementState myElementState) {
        elementState = myElementState;
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
        return "" + ordinal;
    }
}
