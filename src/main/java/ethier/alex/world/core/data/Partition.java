/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.util.Collection;

/**

 @author alex
 */
public class Partition {
    Element[] elements;
    
    Collection<FilterElement[]> filters;
    
    public Partition(Element[] myElements, Collection<FilterElement[]> myFilters) {
        elements = myElements;
        filters = myFilters;
    }
    
}
