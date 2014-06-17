/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */

public class FilterElement implements Numeral {
    
    private FilterElementState filterElementState;
    private int ordinal; // The ordinal value.
    
    public FilterElement(Enum myFilterElementState) {
        if(myFilterElementState == FilterElementState.ONE) {
             throw new RuntimeException("FilterElementState.ONE not allowed for this constructor.");
        }
        
        filterElementState = (FilterElementState) myFilterElementState;
        ordinal = -1;
    }
    
    public FilterElement(int myOrdinal) {
        ordinal = myOrdinal;
        if(myOrdinal < 0) {
            filterElementState = FilterElementState.ALL;
        } else {
            filterElementState = FilterElementState.ONE;
        }
    }
    
    public FilterElement(int myRadix, int myOrdinal, Enum myElementState) {
        filterElementState = (FilterElementState) myElementState;
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
    public String toString() {
        return "" + ordinal;
    }
}
