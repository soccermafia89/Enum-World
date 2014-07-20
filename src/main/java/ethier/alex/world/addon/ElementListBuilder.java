/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.addon;

import ethier.alex.world.core.data.Element;
import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.ElementState;
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
