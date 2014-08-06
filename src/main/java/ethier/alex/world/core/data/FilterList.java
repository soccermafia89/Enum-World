/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import ethier.alex.world.addon.CollectionByteSerializer;
import ethier.alex.world.addon.FilterListBuilder;
import java.io.*;
import java.util.*;
import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
// Wraps an Numeral[] as a class
public class FilterList implements Writable {

    private static Logger logger = LogManager.getLogger(FilterList.class);
    private Filter[] filters;
    
    public FilterList(DataInput in) throws IOException {
        this.readFields(in);
    }

    public FilterList(Filter[] myFilterElements) {

        filters = myFilterElements;
    }

    public int getLength() {
        return filters.length;
    }

    public int[] getOrdinals() {
        int[] ordinals = new int[filters.length];
        for (int i = 0; i < filters.length; i++) {
            ordinals[i] = this.getFilter(i).getOrdinal();
        }

        return ordinals;
    }

    public FilterState[] getFilterStates() {
        FilterState[] filterStates = new FilterState[filters.length];
        for (int i = 0; i < filters.length; i++) {
            filterStates[i] = this.getFilter(i).getFilterState();
        }

        return filterStates;
    }

    public Filter getFilter(int i) {
        return filters[i];
    }
    
//    public Matches applyMatch(ElementList elementList) {
//        Set<Integer> uncheckedRadices = new HashSet<Integer>();
//        for(int i=0; i < elementList.getLength(); i++) {
//            uncheckedRadices.add(i);
//        }
//        
//        return applyMatch(elementList, uncheckedRadices);
//    }

//    // TODO: Figure out a better way to handle the element state being ALL
//    // Consider making a second method to handle that case when needed, otherwise ALL counts torwards an ENTIRELY MATCH.
//    public Matches applyMatch(ElementList elementList, Set<Integer> uncheckedRadices) {
//        
//        logger.info("Filter: " + this + " matching with: " + elementList);
//
//        boolean possibleMatch = false;
//
////        for (int i = start; i < filters.length; i++) {
//        Iterator<Integer> it = uncheckedRadices.iterator();
//        while(it.hasNext()) {
//            
//            int i = it.next();
//
//            ElementState elementState = elementList.getElement(i).getElementState();
//            FilterState filterState = filters[i].getFilterState();
//            
//            if(elementState == ElementState.ALL) {
//                throw new RuntimeException("Invalid input to match, elements can only have a state of SET or UNSET, not ALL");
//            }
//
//            //Order of if statements matters!
//            //1) If either has a Both then they match
//            //2) If 1 is false, and the element is UNSET it is now a part match.
//            //3) If the bits match, then they still match.
//            //4) Otherwise they completely don't match.
//            if (filterState == FilterState.ALL) {
//                continue;
//            } else if (elementState == ElementState.UNSET) {
//                possibleMatch = true; // No longer a match, only a possible match.
//            } else {
// 
//                int firstOrdinal = this.getFilter(i).getOrdinal();
//                int secondOrdinal = elementList.getElement(i).getOrdinal();
//                
//                logger.info("Comparing: " + firstOrdinal + " to " + secondOrdinal);
//
//                if (firstOrdinal == secondOrdinal) {
////                    it.remove();
//                    continue;
//                } else {
//                    return Matches.NO;
//                }
//
//            }
//        }
//
//        if (possibleMatch) {
//            return Matches.PARTLY;
//        } else {
//            return Matches.ENTIRELY;
//        }
//    }
    
//    public Matches checkMatch(ElementList elementList) {
//
//        boolean possibleMatch = false;
//
////        for (int i = start; i < filters.length; i++) {
//        for (int i : unmatchedFilterIndex) {
//
//            ElementState elementState = elementList.getElement(i).getElementState();
//
//            //Order of if statements matters!
//            //1) If either has a Both then they match
//            //2) If 1 is false, and the element is UNSET it is now a part match.
//            //3) If the bits match, then they still match.
//            //4) Otherwise they completely don't match.
//            if (elementState == ElementState.ALL) {
//                continue;
//            } else if (elementState == ElementState.UNSET) {
//                possibleMatch = true; // No longer a match, only a possible match.
//            } else {
//                int firstOrdinal = this.getFilter(i).getOrdinal();
//                int secondOrdinal = elementList.getElement(i).getOrdinal();
//
//                if (firstOrdinal == secondOrdinal) {
//                    continue;
//                } else {
//                    return Matches.NO;
//                }
//
//            }
//        }
//
//        if (possibleMatch) {
//            return Matches.PARTLY;
//        } else {
//            return Matches.ENTIRELY;
//        }
//    }

    @Override
    public String toString() {
        String[] copy = new String[filters.length];
        
        for(int i=0;i < filters.length; i++) {
            Filter filter = filters[i];
            
            if(filter.getFilterState() == FilterState.ALL) {
                copy[i] = "*";
            } else {
                copy[i] = "" + filter.getOrdinal();
            }
        }
        
        return Arrays.toString(copy);
    }
    
    /**
    
    Writable serialization methods
    
    **/
    
    @Override
    public void write(DataOutput out) throws IOException {
        this.writeWorldSize(out);
        this.writeFilters(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.readWorldSize(in);
        filters = this.readFilters(in).filters;
    }
    
    private void writeWorldSize(DataOutput out) throws IOException {
        out.writeInt(filters.length);
    }

    private void readWorldSize(DataInput in) throws IOException {
        int worldSize = in.readInt();
        filters = new Filter[worldSize];
    }
    
    private void writeFilters(DataOutput out) throws IOException {
        for (int i = 0; i < filters.length; i++) {
            out.writeInt(filters[i].getOrdinal());
        }

        for (int i = 0; i < filters.length; i++) {
            out.writeUTF(filters[i].getFilterState().name());
        }
    }
    
    private FilterList readFilters(DataInput in) throws IOException {
        FilterListBuilder filterListBuilder = FilterListBuilder.newInstance();

        int[] ordinals = new int[filters.length];
        for (int j = 0; j < filters.length; j++) {
            ordinals[j] = in.readInt();
        }

        filterListBuilder.setOrdinals(ordinals);

        FilterState[] filterStates = new FilterState[filters.length];
        for (int j = 0; j < filters.length; j++) {
            filterStates[j] = FilterState.valueOf(in.readUTF());
        }

        filterListBuilder.setFilterStates(filterStates);
        return filterListBuilder.getFilterList();
    }
    
    /**
    
    Writable serialization methods end
    
    **/
    
    /**
    
    Additional helper methods.
    
    **/
    public static Collection<FilterList> deserializeFilters(String serializedFilters) throws IOException, DecoderException {

        Collection<byte[]> bytes = CollectionByteSerializer.toBytes(serializedFilters);
        Collection<FilterList> inputFilters = new ArrayList<FilterList>();
        for (byte[] byteArray : bytes) {
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            DataInput dataInput = new DataInputStream(bais);
            FilterList filter = new FilterList(dataInput);
            inputFilters.add(filter);
        }

        return inputFilters;
    }
    
    public static String serializeFilters(Collection<FilterList> myFilters) throws IOException {

        Collection<byte[]> byteCollection = new ArrayList<byte[]>();
        for(FilterList filter : myFilters) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutput dataOutput = new DataOutputStream(baos);
            filter.write(dataOutput);
            byte[] serializedElementList = baos.toByteArray();
            byteCollection.add(serializedElementList);
        }

        String serializedFilters = CollectionByteSerializer.toString(byteCollection);
        return serializedFilters;
    }
}
