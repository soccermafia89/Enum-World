/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import com.google.common.collect.ImmutableSet;
import ethier.alex.world.addon.ElementListBuilder;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class Partition implements Writable {
    
    private static Logger logger = LogManager.getLogger(Partition.class);

    ElementList elements;
    Collection<FilterList> filters;
    private int[] radices;
//    private Set<Integer> checkedRadices; // Contains the radices offsets that have already been checked on.
    
    public Partition(DataInput in) throws IOException {
        this.readFields(in);
    }

    public Partition(int[] myRadices, ElementList myElements, Collection<FilterList> myFilters) {
        elements = myElements;
        filters = myFilters;
        radices = myRadices;
    }
    
    public int[] getRadices() {
        return radices;
    }
    
    public Collection<FilterList> getFilters() {
        return filters;
    }

    public ElementList getElements() {
        return elements;
    }
    
//    public ElementList[] getSplits(int splitOffset) {
//        
//        int radix = radices[splitOffset];
//        
//        ElementList[] elementSplits = new ElementList[radix];
//        
//        for(int i=0;i < radix;i++) {
//            Element newElement = new Element(i);
//            
//            ElementList newSplit = ElementListBuilder.newInstance()
//                    .copy(elements)
//                    .setElement(splitOffset, newElement)
//                    .getElementList();
//                        
//            elementSplits[i] = newSplit;            
//        }
//        
//        return elementSplits;
//    }

    public String printElements() {
        StringBuilder stringBuilder = new StringBuilder();
        
        Iterator<Element> it = elements.iterator();
        while(it.hasNext()) {
            Numeral numeral = it.next();
            stringBuilder.append(numeral.toString());
        }
        
        return stringBuilder.toString();
    }
    
    /**
    
    Writable serialization methods
    
    **/

    @Override
    public void write(DataOutput out) throws IOException {

        writeWorldSize(out);
        writeRadices(out);
        writeElements(out);
        writeFilters(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {

        this.readWorldSize(in);
        radices = readRadices(in);
        elements = readElementList(in);
        filters = readFilters(in);
    }

    private void writeWorldSize(DataOutput out) throws IOException {
        int worldSize = radices.length;
        out.writeInt(worldSize);
    }

    private void readWorldSize(DataInput in) throws IOException {
        radices = new int[in.readInt()];
    }

    private void writeRadices(DataOutput out) throws IOException {
        for (int i = 0; i < radices.length; i++) {
            out.writeInt(radices[i]);
        }
    }

    private int[] readRadices(DataInput in) throws IOException {

        for (int i = 0; i < radices.length; i++) {
            radices[i] = in.readInt();
        }

        return radices;
    }

    private void writeElements(DataOutput out) throws IOException {

        Writable elementListWritable = this.getElements();
        elementListWritable.write(out);
    }

    private ElementList readElementList(DataInput in) throws IOException {
        return new ElementList(in);
    }

    private void writeFilters(DataOutput out) throws IOException {
        int numFilters = filters.size();
        out.writeInt(numFilters);

        for (Writable filterListWritable : filters) {
            filterListWritable.write(out);
        }
    }

    private Collection<FilterList> readFilters(DataInput in) throws IOException {
        int numFilters = in.readInt();

        Collection<FilterList> readFilters = new ArrayList<FilterList>();
        for (int i = 0; i < numFilters; i++) {
            readFilters.add(new FilterList(in));
        }

        return readFilters;
    }
    
    
    /**
    
    Writable serialization methods end
    
    **/
    
    /**
    
    Additional helper methods.
    
    **/
    
    public static String serializeRadices(int[] myRadices) {
        
        StringBuilder stringBuilder = new StringBuilder();
        
        for(int i=0; i < myRadices.length; i++) {
            int radix = myRadices[i];
            stringBuilder.append(radix);
            stringBuilder.append(",");
        }
        
        return StringUtils.stripEnd(stringBuilder.toString(), ",");
    }
    
    public static int[] deserializeRadices(String serializedRadices) {
        String[] intStrs = serializedRadices.split(",");
        int[] myRadices = new int[intStrs.length];
        for(int i=0; i < intStrs.length; i++) {
            myRadices[i] = Integer.parseInt(intStrs[i]);
        }
        
        return myRadices;
    }
}
