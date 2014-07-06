/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import ethier.alex.world.addon.FilterListBuilder;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class Partition implements Writable {
    
    private static Logger logger = Logger.getLogger(Partition.class);

    ElementList elements;
    Collection<FilterList> filters;
    private int splitIndex = -1;
    private int[] radices;
    
    public Partition(DataInput in) throws IOException {
        this.readFields(in);
    }

    public Partition(int[] myRadices, ElementList myElements, Collection<FilterList> myFilters) {
        elements = myElements;
        filters = myFilters;
        radices = myRadices;

        for (int i = 0; i < elements.getLength(); i++) {

            Element element = elements.getElement(i);
            if (splitIndex < 0 && element.getElementState() == ElementState.UNSET) {
                splitIndex = i;
            }
        }
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
    
    public int getSplitIndex() {
        return splitIndex;
    }
    
    public ElementList[] getSplits() {
        int radix = radices[splitIndex];
        
        ElementList[] elementSplits = new ElementList[radix];
        
        for(int i=0;i < radix;i++) {
            ElementList newSplit = elements.copy();
            Element newElement = new Element(i);
            newSplit.set(splitIndex, newElement);
            
            elementSplits[i] = newSplit;
        }
        
        return elementSplits;
    }

    public boolean hasSplit() {
//        logger.info("elements: " + elements.toString());
        if (splitIndex > -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public String printElements() {
        StringBuilder stringBuilder = new StringBuilder();
        
        Iterator<Element> it = elements.iterator();
        while(it.hasNext()) {
            Numeral numeral = it.next();
            stringBuilder.append(numeral.toString());
        }
        
        return stringBuilder.toString();
    }
    
    public boolean verifyIntegrity() {
        for(FilterList filter : filters) {
            if(filter.checkMatch(elements) != Matches.PARTLY) {
                return false;
            }
        }
        return true;
    }

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
        
        for (int i = 0; i < elements.getLength(); i++) {

            Element element = elements.getElement(i);
            if (splitIndex < 0 && element.getElementState() == ElementState.UNSET) {
                splitIndex = i;
            }
        }
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

        for (FilterList filter : filters) {
            int[] ordinals = filter.getOrdinals();

            for (int i = 0; i < ordinals.length; i++) {
                out.writeInt(ordinals[i]);
            }

            FilterState[] filterStates = filter.getFilterStates();
            for (int i = 0; i < filterStates.length; i++) {
                out.writeUTF(filterStates[i].name());
            }
        }
    }

    private Collection<FilterList> readFilters(DataInput in) throws IOException {
        int numFilters = in.readInt();

        Collection<FilterList> readFilters = new ArrayList<FilterList>();
        for (int i = 0; i < numFilters; i++) {
            FilterListBuilder filterListBuilder = FilterListBuilder.newInstance();

            int[] ordinals = new int[radices.length];
            for (int j = 0; j < radices.length; j++) {
                ordinals[j] = in.readInt();
            }

            filterListBuilder.setOrdinals(ordinals);

            FilterState[] filterStates = new FilterState[radices.length];
            for (int j = 0; j < radices.length; j++) {
                filterStates[j] = FilterState.valueOf(in.readUTF());
            }

            filterListBuilder.setFilterStates(filterStates);

            readFilters.add(filterListBuilder.getFilterList());
        }

        return readFilters;
    }
}
