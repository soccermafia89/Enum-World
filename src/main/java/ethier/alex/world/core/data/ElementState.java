/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */
public enum ElementState {
    SET, UNSET, ALL;
    
    private int comparator;
    
    static {
        SET.comparator = 0;
        UNSET.comparator = 1;
        ALL.comparator = 2; 
    }
    
    public int comparator() {
        return comparator;
    }
}

