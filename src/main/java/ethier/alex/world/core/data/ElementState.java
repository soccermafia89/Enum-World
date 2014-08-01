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
    
    // Comparator states should be negative to not conflict with possible ordinal numbers
    static {
        SET.comparator = -1;
        UNSET.comparator = -2;
        ALL.comparator = -3; 
    }
    
    public int comparator() {
        return comparator;
    }
}


