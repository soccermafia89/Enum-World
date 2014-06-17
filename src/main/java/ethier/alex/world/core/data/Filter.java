/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */

public class Filter implements Numeral {
    
    private FilterState filterElementState;
    private int ordinal; // The ordinal value.
    
    public Filter(Enum myFilterElementState) {
        if(myFilterElementState == FilterState.ONE) {
             throw new RuntimeException("FilterElementState.ONE not allowed for this constructor.");
        }
        
        filterElementState = (FilterState) myFilterElementState;
        ordinal = -1;
    }
    
    public Filter(int myOrdinal) {
        ordinal = myOrdinal;
        if(myOrdinal < 0) {
            filterElementState = FilterState.ALL;
        } else {
            filterElementState = FilterState.ONE;
        }
    }
    
    public Filter(int myRadix, int myOrdinal, Enum myElementState) {
        filterElementState = (FilterState) myElementState;
        ordinal = myOrdinal;
    }    
    
    @Override
    public int getOrdinal() {
        return ordinal;
    }
    
    public FilterState getFilterState() {
        return filterElementState;
    }
    
    @Override
    public String toString() {
        return "" + ordinal;
    }
}
