/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */
public enum FilterState {

    ALL, ONE;
    
    private int comparator;

    static {
        ALL.comparator = 2;
        ONE.comparator = 3;
    }

    public int comparator() {
        return comparator;
    }
}
