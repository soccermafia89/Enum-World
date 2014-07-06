/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core.data;

import ethier.alex.world.addon.ElementListBuilder;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
// Wraps an Numeral[] as a class
public class ElementList implements Iterable, Writable {

    private static Logger logger = Logger.getLogger(ElementList.class);
    private Element[] elementArray;
    
    public ElementList(DataInput in) throws IOException {
        this.readFields(in);
    }

    public ElementList(Element[] myElementArray) {
        elementArray = myElementArray;
    }

    public int getLength() {
        return elementArray.length;
    }

    public int[] getOrdinals() {
        int[] ordinals = new int[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            ordinals[i] = this.getElement(i).getOrdinal();
        }

        return ordinals;
    }

    public ElementState[] getElementStates() {
        ElementState[] elementStates = new ElementState[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            elementStates[i] = (ElementState)this.getElement(i).getElementState();
        }
        
        return elementStates;
    }

    public Element getElement(int i) {
        return elementArray[i];
    }

    public ElementList copy() {
        Element[] newElementArray = Arrays.copyOf(elementArray, elementArray.length);
        return new ElementList(newElementArray);
    }

    public void set(int i, Element element) {
        elementArray[i] = element;
    }

    @Override
    public Iterator<Element> iterator() {
        return new NumeralArrayIterator(elementArray);
    }

    @Override
    public String toString() {
        return Arrays.toString(elementArray);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.writeWorldSize(out);
        this.writeElements(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.readWorldSize(in);
        elementArray = this.readElementList(in).elementArray;    
    }

    private void writeWorldSize(DataOutput out) throws IOException {
        out.writeInt(elementArray.length);
    }

    private void readWorldSize(DataInput in) throws IOException {
        int worldSize = in.readInt();
        elementArray = new Element[worldSize];
    }

    private void writeElements(DataOutput out) throws IOException {
        int[] ordinals = this.getOrdinals();
        for (int i = 0; i < ordinals.length; i++) {
            out.writeInt(ordinals[i]);
        }

        ElementState[] elementStates = this.getElementStates();
        for (int i = 0; i < elementStates.length; i++) {
            out.writeUTF(elementStates[i].name());
        }
    }

    private ElementList readElementList(DataInput in) throws IOException {
        ElementListBuilder elementListBuilder = ElementListBuilder.newInstance();

        int[] ordinals = new int[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            ordinals[i] = in.readInt();
        }

        ElementState[] elementStates = new ElementState[elementArray.length];
        for (int i = 0; i < elementArray.length; i++) {
            elementStates[i] = ElementState.valueOf(in.readUTF());
        }

        elementListBuilder.setOrdinals(ordinals);
        elementListBuilder.setStates(elementStates);

        return elementListBuilder.getElementList();
    }
}
