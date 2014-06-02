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

    public SimpleTranslator() {
    }

    //Translate an Enum World into a Bit World
    public void translateEnumWorld() {

//        Enum[] world = null;
//        Collection<Enum[]> filters = null;
        int[] sizes = {2, 3};

        int worldSize = 1;
        for (int size : sizes) {
            System.out.println("Incoming combination sizes: ");
            System.out.println(size);

            worldSize = worldSize * size;

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
