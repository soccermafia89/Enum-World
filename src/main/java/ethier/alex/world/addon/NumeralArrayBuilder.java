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
public class NumeralArrayBuilder {

    private static Logger logger = Logger.getLogger(NumeralArrayBuilder.class);
    private NumeralArray numeralArray;
    private int[] ordinals;
    private Enum[] combinationStates;
    private int worldLength;
    private boolean blankWorld = false;
    private boolean isFilter = false;

    public static NumeralArrayBuilder newInstance() {
        return new NumeralArrayBuilder();
    }

    public NumeralArrayBuilder() {
    }

    public NumeralArrayBuilder setAsFilter() {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        isFilter = true;

        return this;
    }

    public NumeralArrayBuilder setAsFilter(String inputStr) {

        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        isFilter = true;


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

    public NumeralArrayBuilder setBlankWorld(int myWorldLength) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        blankWorld = true;
        worldLength = myWorldLength;

        return this;
    }

    public NumeralArrayBuilder setOrdinals(int[] myOrdinals) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        ordinals = myOrdinals;
        worldLength = ordinals.length;
        return this;
    }

    public NumeralArrayBuilder setStates(Enum[] states) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        combinationStates = states;
        return this;
    }

    public NumeralArray getNumeralArray() {
        if (numeralArray == null) {

            if (blankWorld) {
                combinationStates = new ElementState[worldLength];
                for (int i = 0; i < worldLength; i++) {
                    combinationStates[i] = ElementState.UNSET;
                }
            }

            if (isFilter) {

                if (combinationStates == null) {
                    combinationStates = new Enum[ordinals.length];
                }

                for (int i = 0; i < ordinals.length; i++) {
                    if (combinationStates[i] == null) {
                        if (ordinals[i] < 0) {
                            combinationStates[i] = FilterElementState.ALL;
                        } else {
                            combinationStates[i] = FilterElementState.ONE;
                        }
                    }
                }
            }

            Numeral[] numerals = new Numeral[worldLength];

            if (!isFilter) {

                for (int i = 0; i < worldLength; i++) {

                    Numeral newNumeral;
                    if (combinationStates[i] == ElementState.SET) {
                        newNumeral = new Element(ordinals[i]);
                    } else {
                        newNumeral = new Element(combinationStates[i]);
                    }

                    numerals[i] = newNumeral;
                }

            } else if (isFilter) {
                for (int i = 0; i < worldLength; i++) {

                    if (combinationStates[i] == FilterElementState.ALL) {
                        numerals[i] = new FilterElement(combinationStates[i]);
                    } else {
                        numerals[i] = new FilterElement(ordinals[i]);
                    }
                }
            }

            numeralArray = new NumeralArray(numerals);
        }

        return numeralArray;
    }
}
