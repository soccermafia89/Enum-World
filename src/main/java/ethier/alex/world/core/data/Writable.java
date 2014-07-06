/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**

 @author alex
 */
public interface Writable {
    
    public void write(DataOutput d) throws IOException;

    public void readFields(DataInput di) throws IOException;
    
}
