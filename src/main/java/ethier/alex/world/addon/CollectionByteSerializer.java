/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**

 @author alex
 */

// Until I understand serialization better, this crude class must do.
public class CollectionByteSerializer {
    
    public static String toString(Collection<byte[]> byteCollection) {
        
        StringBuilder hexBuilder = new StringBuilder();
        for(byte[] bytes : byteCollection) {
            String hexStr = Hex.encodeHexString(bytes);
            hexBuilder.append(hexStr);
            hexBuilder.append(",");
        }
        
        return StringUtils.stripEnd(hexBuilder.toString(), ",");
    }
    
    public static Collection<byte[]> toBytes(String serializedCollection) throws DecoderException {
        
        Collection<byte[]> byteCollection = new ArrayList<byte[]>();
        String[] serializedStrs = serializedCollection.split(",");
        for(String serializedStr : serializedStrs) {
            byte[] bytes = Hex.decodeHex(serializedStr.toCharArray());
            byteCollection.add(bytes);
        }
        
        return byteCollection;
    }
}
