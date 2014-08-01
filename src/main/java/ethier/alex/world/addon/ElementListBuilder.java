/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import ethier.alex.world.core.data.Element;
import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.ElementState;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class ElementListBuilder {

    private static Logger logger = Logger.getLogger(ElementListBuilder.class);
    private ElementList elements;
    private int[] ordinals;
    private ElementState[] elementStates;
    private int worldLength;
    private boolean blankWorld = false;

    public static ElementListBuilder newInstance() {
        return new ElementListBuilder();
    }

    public ElementListBuilder() {
    }
    
    public ElementListBuilder copy(ElementList inputElements) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
       
        ordinals = inputElements.getOrdinals();
        elementStates = inputElements.getElementStates();
        worldLength = inputElements.getElementStates().length;
        
        return this;
    }
    
    public ElementListBuilder setElement(int i, Element element) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
//        
//        logger.info("Setting element at: " + i);
//        logger.info("Before Ordinals: " + Arrays.toString(ordinals));
//        logger.info("Before States: " + Arrays.toString(elementStates));
        
        ordinals[i] = element.getOrdinal();
        elementStates[i] = element.getElementState();
//        
//        logger.info("After Ordinals: " + Arrays.toString(ordinals));
//        logger.info("After States: " + Arrays.toString(elementStates));
        
        return this;
    }
    
    public ElementListBuilder setQuick(String inputStr) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        
        int[] myOrdinals;
        ElementState[] myElementStates;
        

        if (inputStr.contains(",")) {
            inputStr = StringUtils.strip(inputStr, ",");
            String[] strs = inputStr.split(",");

            myOrdinals = new int[strs.length];
            myElementStates = new ElementState[strs.length];
            
            for(int i=0; i < strs.length; i++) {
                String str = strs[i];
                
                if(str.equals("*")) {
                    myElementStates[i] = ElementState.ALL;
                } else if(str.equals("-")) {
                    myElementStates[i] = ElementState.UNSET;
                } else {
                    myElementStates[i] = ElementState.SET;
                    myOrdinals[i] = Integer.parseInt(str);
                }
            }
        } else {
            char[] chars = inputStr.toCharArray();
            myOrdinals = new int[chars.length];
            myElementStates = new ElementState[chars.length];

            for (int i = 0; i < chars.length; i++) {
                char charchar = chars[i];

                if(charchar == '*') {
                    myElementStates[i] = ElementState.ALL;
                } else if(charchar == '-') {
                    myElementStates[i] = ElementState.UNSET;
                } else {
                    myElementStates[i] = ElementState.SET;
                    myOrdinals[i] = Character.getNumericValue(charchar);
                }
            }
        }
        
        elementStates = myElementStates;
        ordinals = myOrdinals;
        worldLength = ordinals.length;
        
        return this;
    }

    public ElementListBuilder setBlankWorld(int myWorldLength) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }
        blankWorld = true;
        worldLength = myWorldLength;

        return this;
    }

    public ElementListBuilder setOrdinals(int[] myOrdinals) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        ordinals = myOrdinals;
        worldLength = ordinals.length;
        return this;
    }

    public ElementListBuilder setStates(ElementState[] states) {
        if (elements != null) {
            throw new RuntimeException("NumeralArray already created.");
        }

        elementStates = states;
        return this;
    }

    public ElementList getElementList() {
        if (elements == null) {

            if (blankWorld) {
                elementStates = new ElementState[worldLength];
                for (int i = 0; i < worldLength; i++) {
                    elementStates[i] = ElementState.UNSET;
                }
            }

            Element[] newElements = new Element[worldLength];

            for (int i = 0; i < worldLength; i++) {

                Element newElement;
                if (elementStates[i] == ElementState.SET) {
                    newElement = new Element(ordinals[i]);
                } else {
                    newElement = new Element(elementStates[i]);
                }

                newElements[i] = newElement;
            }



            elements = new ElementList(newElements);
        }

        return elements;
    }
}
