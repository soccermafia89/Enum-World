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
    private int[] radices;
    private int[] ordinals;
    private Enum[] combinationStates;
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

    public NumeralArrayBuilder setBlankWorld() {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        blankWorld = true;

        return this;
    }

    public NumeralArrayBuilder setRadices(int[] myRadices) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        radices = myRadices;
        return this;
    }

    public NumeralArrayBuilder setOrdinals(int[] myOrdinals) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        ordinals = myOrdinals;
        return this;
    }

    public NumeralArrayBuilder setElementStates(ElementState[] myElementStates) {
        if (numeralArray != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        combinationStates = myElementStates;
        return this;
    }

    public NumeralArray getNumeralArray() {
        if (numeralArray == null) {

            if (blankWorld) {
                combinationStates = new ElementState[radices.length];
                for (int i = 0; i < combinationStates.length; i++) {
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

            Numeral[] numerals = new Numeral[radices.length];

            if (!isFilter) {

                for (int i = 0; i < radices.length; i++) {

                    Numeral newNumeral;
                    if (combinationStates[i] == ElementState.SET) {
                        newNumeral = new Element(radices[i], ordinals[i]);
                    } else {
                        newNumeral = new Element(radices[i], combinationStates[i]);
                    }

                    numerals[i] = newNumeral;
                }

            } else if (isFilter) {
                for (int i = 0; i < radices.length; i++) {

                    numerals[i] = new FilterElement(radices[i], ordinals[i]);
                }
            }
            
            numeralArray = new NumeralArray(numerals);
        }
        
        return numeralArray;
    }
}
