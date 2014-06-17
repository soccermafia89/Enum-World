/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import ethier.alex.world.core.data.*;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class FilterListBuilder {

    private static Logger logger = Logger.getLogger(FilterListBuilder.class);
    private FilterList filterArray;
    private int[] ordinals;
    private FilterState[] filterStates;
    private int worldLength;

    public static FilterListBuilder newInstance() {
        return new FilterListBuilder();
    }

    public FilterListBuilder() {
    }

    public FilterListBuilder setQuick(String inputStr) {

        if (filterArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        char[] chars = inputStr.toCharArray();
        int[] myOrdinals = new int[chars.length];

        for (int i = 0; i < chars.length; i++) {
            char charchar = chars[i];

            if (charchar == '*') {
                myOrdinals[i] = -1;
            } else {
                myOrdinals[i] = Character.getNumericValue(charchar);;
            }
        }

        ordinals = myOrdinals;
        worldLength = ordinals.length;
        return this;
    }

    public FilterListBuilder setOrdinals(int[] myOrdinals) {
        if (filterArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        ordinals = myOrdinals;
        worldLength = ordinals.length;
        return this;
    }

    public FilterListBuilder setFilterStates(FilterState[] states) {
        if (filterArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        filterStates = states;
        return this;
    }

    public FilterList getFilterList() {
        if (filterArray == null) {

            if (filterStates == null) {
                filterStates = new FilterState[ordinals.length];
            }

            for (int i = 0; i < ordinals.length; i++) {
                if (filterStates[i] == null) {
                    if (ordinals[i] < 0) {
                        filterStates[i] = FilterState.ALL;
                    } else {
                        filterStates[i] = FilterState.ONE;
                    }
                }
            }


            Filter[] newFilterElements = new Filter[worldLength];


            for (int i = 0; i < worldLength; i++) {

                if (filterStates[i] == FilterState.ALL) {
                    newFilterElements[i] = new Filter(filterStates[i]);
                } else {
                    newFilterElements[i] = new Filter(ordinals[i]);
                }
            }


            filterArray = new FilterList(newFilterElements);
        }

        return filterArray;
    }
}
