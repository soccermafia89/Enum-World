/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core;

import com.google.common.base.Stopwatch;
import ethier.alex.world.addon.NumeralArrayBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.ElementState;
import ethier.alex.world.core.data.FilterElementState;
import ethier.alex.world.core.data.NumeralArray;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.processor.SimpleProcessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**

 @author alex
 */
public class SimpleProcessorTest {
    
    private static Logger logger = Logger.getLogger(SimpleProcessorTest.class);
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }
    
//    @Test
//    public void testProcessor() throws Exception {
//        System.out.println("");
//        System.out.println("");
//        System.out.println("********************************************");
//        System.out.println("********         Basic Test        *********");
//        System.out.println("********************************************");
//        System.out.println("");
//        System.out.println(""); 
//        
//        int[] radices = new int[4];
//        radices[0] = 3;
//        radices[1] = 2;
//        radices[2] = 3;
//        radices[3] = 2;
//        
//        NumeralArray filter1 = NumeralArrayBuilder.newInstance()
//                .setOrdinals(new int[] {0, -1, -1, -1})
//                .setAsFilter()
//                .getNumeralArray();
//        
//        NumeralArray filter2 = NumeralArrayBuilder.newInstance()
//                .setOrdinals(new int[] {-1, -1, 1, -1})
//                .setAsFilter()
//                .getNumeralArray();
//        
//        NumeralArray filter3 = NumeralArrayBuilder.newInstance()
//                .setOrdinals(new int[] {-1, -1, 0, -1})
//                .setAsFilter()
//                .getNumeralArray();
//        
//        NumeralArray filter4 = NumeralArrayBuilder.newInstance()
//                .setOrdinals(new int[] {1, 1, 2, 0})
//                .setAsFilter()
//                .getNumeralArray();
//        
//        Partition rootPartition = PartitionBuilder
//                .newInstance()
//                .setBlankWorld()
//                .setRadices(radices)
//                .addFilter(filter1)
//                .addFilter(filter2)
//                .addFilter(filter3)
//                .addFilter(filter4)
//                .getPartition();
//        
//        SimpleProcessor simpleProcessor = new SimpleProcessor(rootPartition);
//        
//        simpleProcessor.runAll();
//        
//        Collection<NumeralArray> finalPartitions = simpleProcessor.getCompletedPartitions();
//        for(NumeralArray finalPartition : finalPartitions) {
//            System.out.println("Final partition computed: " + finalPartition.toString());
//        }
//        
//        Assert.assertTrue(true);
//    }
    
    @Test
    public void testComplements() throws Exception {
            System.out.println("");
            System.out.println("");
            System.out.println("********************************************");
            System.out.println("********      Complement Test      *********");
            System.out.println("********************************************");
            System.out.println("");
            System.out.println("");

            //This is testing the complement done by creating an arbitrary set of allowed combinations
            //Then creating filters out of them, applying and getting the new set of combinations (which is the complement)
            //Do the process again to get the complement of the complement and we should get back to what we started.

            //Note the complement is a great way to determine the efficiency of the algorithm.


//            int ones = 8;
//            int worldLength = 16;
            int ones = 3;
            int worldLength = 8;

            Collection<NumeralArray> filters = new ArrayList<NumeralArray>();
            int[] radices = new int[worldLength];

            String breakStr = "";
            for (int i = 0; i < worldLength; i++) {
                    breakStr += '1';
                    radices[i] = 2;
            }

            String filterStr = "";
            int count = 0;
            while (true) {
                    if (filterStr.equals(breakStr)) {
                            break;
                    }

                    filterStr = "" + Integer.toBinaryString(count);
                    filterStr = StringUtils.leftPad(filterStr, worldLength, '0');
                    int combOnes = StringUtils.countMatches(filterStr, "1");
                    if (combOnes == ones) {
//                        BitList filter = BitListBuilder.buildBitList(filterStr);
                        NumeralArray filter = NumeralArrayBuilder.newInstance()
                                .setAsFilter(filterStr)
                                .getNumeralArray();
                                
                        logger.debug("Adding filter: " + filter);
                        filters.add(filter);
                    }
                    

                    count++;
            }
            
            Partition rootPartition = PartitionBuilder.newInstance()
                    .setBlankWorld()
                    .setRadices(radices)
                    .addFilters(filters)
                    .getPartition();

            SimpleProcessor processor = new SimpleProcessor(rootPartition);
            Stopwatch stopWatch =  Stopwatch.createStarted();
            processor.runAll();
            stopWatch.stop();
            Collection<NumeralArray> resultCombinations = processor.getCompletedPartitions();

            Collection<NumeralArray> complementFilters = new ArrayList<NumeralArray>();
            for(NumeralArray resultCombination : resultCombinations) {
                logger.debug(resultCombination);
                int[] resultOrdinals = resultCombination.getOrdinals();
                
                Enum[] resultElementStates = resultCombination.getStates();
                FilterElementState[] complementFilterStates = new FilterElementState[resultElementStates.length];
                for(int i=0; i < resultElementStates.length; i++) {
                    if(resultElementStates[i] == ElementState.ALL) {
                        complementFilterStates[i] = FilterElementState.ALL;
                    } else if(resultElementStates[i] == ElementState.SET){
                        complementFilterStates[i] = FilterElementState.ONE;
                    } else {
                        throw new RuntimeException("Invalid state reached.");
                    }                    
                }
                                
                NumeralArray complementFilter = NumeralArrayBuilder.newInstance()
                        .setAsFilter()
                        .setOrdinals(resultOrdinals)
                        .setStates(complementFilterStates)
                        .getNumeralArray();
                                
                complementFilters.add(complementFilter);                            
            }

            Partition complementPartition = PartitionBuilder.newInstance()
                    .setRadices(radices)
                    .setBlankWorld()
                    .addFilters(complementFilters)
                    .getPartition();

            processor = new SimpleProcessor(complementPartition);
            stopWatch.start();
            processor.runAll();
            stopWatch.stop();
            Collection<NumeralArray> originalCombinations = processor.getCompletedPartitions();

            Set<String> outputSet = new HashSet<String>();
            for(NumeralArray origList : originalCombinations) {
                logger.debug(origList);
                
                String enumStr = origList.toString();
                int numOnes = StringUtils.countMatches(enumStr, "1");
                Assert.assertTrue(numOnes == ones);
                
                Assert.assertFalse(outputSet.contains(enumStr));
                outputSet.add(enumStr);
            }
            
            int expectedCombinations = (int) MathUtils.binomialCoefficient(worldLength, ones);  
            Assert.assertTrue(expectedCombinations == outputSet.size());

            int secondsRun = (int) stopWatch.elapsed(TimeUnit.SECONDS);
            int origSize = originalCombinations.size();
            logger.info(origSize + " complements found and reverted in " + secondsRun + " seconds");
    }
}
