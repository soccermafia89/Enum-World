/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */
public class FilterElement implements Ordinal {
    
    private FilterElementState filterElementState;
    private Enum filter;
    
    public FilterElement(FilterElementState myFilterElementState, Enum myFilter) {
        filterElementState = myFilterElementState;
        filter = myFilter;
    }
    
    public FilterElementState getFilterElementState() {
        return filterElementState;
    }
    
    @Override
    public int getOrdinal() {
        return filter.ordinal();
    }

    @Override
    public Enum getEnum() {
        return filter;
    }
}
