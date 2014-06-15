/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */

public class FilterElement implements Numeral {
    
    private Enum filterElementState;
    private int ordinal; // The ordinal value.
    private int radix; // Total number of possible ordinals.
    
    public FilterElement(int myRadix, int myOrdinal) {
        radix = myRadix;
        ordinal = myOrdinal;
        if(myOrdinal < 0) {
            filterElementState = FilterElementState.ALL;
        } else {
            filterElementState = FilterElementState.ONE;
        }
    }
    
    public FilterElement(int myRadix, int myOrdinal, Enum myElementState) {
        filterElementState = myElementState;
        radix = myRadix;
        ordinal = myOrdinal;
    }    
    
    @Override
    public int getOrdinal() {
        return ordinal;
    }
    
    @Override
    public Enum getState() {
        return filterElementState;
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
