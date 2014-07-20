/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.query;

import ethier.alex.world.core.data.FilterList;
import java.math.BigDecimal;
import java.util.Collection;

/**

 @author alex
 */
public interface Query {
    
    public BigDecimal getWorldSize();
    
    public double query(FilterList filter);
    
    public double query(Collection<FilterList> filters);
}
