/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core;

import ethier.alex.core.data.Bit;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**

 @author alex
 */
//Translates enum encoded worlds into Bit Worlds, runs a processor, then translates back to enums.
public class SimpleTranslator {

    int[] ordinals;

    public SimpleTranslator(int[] myOrdinals) {
        //ordinals is the size of each Enum used in our model.

        ordinals = myOrdinals;
    }

    public int translateEnum(Enum[] myEnums) {
        int intValue = 0;

        for (int i = 0; i < myEnums.length; i++) {
            Enum myEnum = myEnums[i];

            int ordinal = myEnum.ordinal();
            System.out.println("WARN: test whether ordinals start at 0 or 1.");
            int product = 1;
            for (int j = i; j < ordinals.length; j++) {
                System.out.println("WARN: test ordinal product start counter.");
                product = product * ordinals[j];
            }

            intValue += ordinal * product;

        }

        System.out.println("New intValue translated: " + intValue);
        return intValue;
    }

    public int[] translateBit(int intValue) {
        int[] translatedOrdinals = new int[ordinals.length];
//        Enum[] enums = new Enum[];

        int product = 1;
        for (int i = 0; i < ordinals.length; i++) {

            int newProduct = product * ordinals[ordinals.length - i];
            if (newProduct > intValue) {
                
                //Find the highest multiplicative factor we can multiply to be less than the intValue.
                int count = 1;
                newProduct = count * product;
                while(newProduct < intValue) {
                    count++;
                    newProduct = count * product;
                }
                
                int ordinalProduct = (count - 1) * product;
                intValue = intValue - ordinalProduct;
                System.out.println("Ordinal product found: " + ordinalProduct);
                System.out.println("New intvalue: " + intValue);
            } else {
                product = newProduct;
            }
        }
        
        return translatedOrdinals;
    }

    //Translate an Enum World into a Bit World
    public void translateEnumWorld() {

//        Enum[] world = null;
//        Collection<Enum[]> filters = null;

        int worldSize = 1;
        for (int ordinal : ordinals) {
            System.out.println("Incoming combination sizes: ");
            System.out.println(ordinal);

            worldSize = worldSize * ordinal;

//            Enum test = Bit.BOTH;

//            myEnum.
//            myEnum.getDeclaringClass().
//            enum enumType = (enum) myEnum.;
        }

        System.out.println("Bit world binary representaiton: " + Integer.toBinaryString(worldSize));

        int format1 = Integer.highestOneBit(worldSize);
        int format2 = Integer.numberOfTrailingZeros(format1);

        int bitWorldSize = format2 + 1;
        System.out.println("Final bit world size: " + bitWorldSize);

        int bitWorldBinary = (int) Math.pow(2, bitWorldSize);
        String bitWorldString = Integer.toBinaryString(bitWorldBinary - 1);
        System.out.println("Final bit world binary representation: " + bitWorldString);

        //Generate removals
        char[] chars = Integer.toBinaryString(worldSize).toCharArray();

        String baseFilterStr = StringUtils.leftPad("", bitWorldSize, "*");
        System.out.println("Base String: " + baseFilterStr);
        char[] baseFilterChars = baseFilterStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char myChar = chars[i];

            baseFilterChars[i] = myChar;

            if (myChar == '0') {
                char[] newFilterChars = Arrays.copyOf(baseFilterChars, baseFilterChars.length);
                newFilterChars[i] = '1';
                String newFilterStr = new String(newFilterChars);
                System.out.println("Enum Filter Found: " + newFilterStr);
            }
        }
        //        for(char myChar : chars) {
//            if(myChar ==)
//        }
    }
}
