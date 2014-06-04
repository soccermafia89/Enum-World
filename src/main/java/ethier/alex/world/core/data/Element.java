/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

/**

 @author alex
 */

// Wraps passed in enums.
public class Element implements Ordinal {
    
    private Enum myEnum;
    
    private ElementState elementState;
    
    public Element(Enum setEnum, ElementState myElementState) {
        myEnum = setEnum;
        elementState = myElementState;
    }    
    
    @Override
    public int getOrdinal() {
        return myEnum.ordinal();
    }
    
    public ElementState getState() {
        return elementState;
    }

    @Override
    public Enum getEnum() {
        return myEnum;
    }
}
