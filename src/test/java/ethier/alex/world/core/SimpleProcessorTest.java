/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core;

import com.google.common.base.Stopwatch;
import ethier.alex.world.addon.NumeralArrayBuilder;
import ethier.alex.world.addon.PartitionBuilder;
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
    
    @Test
    public void testProcessor() throws Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********         Basic Test        *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println(""); 
        
        int[] radices = new int[3];
        radices[0] = 3;
        radices[1] = 2;
        radices[2] = 2;
        
        NumeralArray filter1 = NumeralArrayBuilder.newInstance()
                .setRadices(radices)
                .setOrdinals(new int[] {0, -1, -1})
                .setAsFilter()
                .getNumeralArray();
        
        Partition rootPartition = PartitionBuilder
                .newInstance()
                .setRadices(radices)
                .addFilter(filter1)
                .getPartition();
        
        SimpleProcessor simpleProcessor = new SimpleProcessor(rootPartition);
        
        simpleProcessor.runAll();
        
        Collection<NumeralArray> finalPartitions = simpleProcessor.getCompletedPartitions();
        for(NumeralArray finalPartition : finalPartitions) {
            System.out.println("Final partition computed: " + finalPartition.toString());
        }
        
//        BitList combination = BitListBuilder.buildBitList("---");
//        BitList filter1 = BitListBuilder.buildBitList("1**");
//        BitList filter2 = BitListBuilder.buildBitList("*11");
////        BitList filter3 = BitListBuilder.buildBitList("**01*");
//        
//        Collection<BitList> filters = new ArrayList<BitList>();
//        filters.add(filter1);
//        filters.add(filter2);
////        filters.add(filter3);
//        
//        
//        Partition partition = new Partition(combination, filters);
//        Collection<Partition> partitions = new ArrayList<Partition>();
//        partitions.add(partition);
//        
//        SimpleProcessor simpleProcessor = new SimpleProcessor(partitions);
//        
//        simpleProcessor.runAll();
//        Collection<BitList> finalCombinations = simpleProcessor.getCompletedPartitions();
//        
//        for(BitList finalCombination : finalCombinations) {
//            logger.info("Combination found: " + finalCombination.toString());
//        }
//        
//        logger.error("TODO: make proper assertions in test cases.");
        
        Assert.assertTrue(true);
    }
    
//    @Test
//    public void testComplements() throws Exception {
//            System.out.println("");
//            System.out.println("");
//            System.out.println("********************************************");
//            System.out.println("********      Complement Test      *********");
//            System.out.println("********************************************");
//            System.out.println("");
//            System.out.println("");
//
//            //This is testing the complement done by creating an arbitrary set of allowed combinations
//            //Then creating filters out of them, applying and getting the new set of combinations (which is the complement)
//            //Do the process again to get the complement of the complement and we should get back to what we started.
//
//            //Note the complement is a great way to determine the efficiency of the algorithm.
//
//
////            int ones = 8;
////            int worldLength = 16;
//            int ones = 3;
//            int worldLength = 8;
//
//            Collection<BitList> filters = new ArrayList<BitList>();
//
//            String breakStr = "";
//            for (int i = 0; i < worldLength; i++) {
//                    breakStr += '1';
//            }
//
//            String filterStr = "";
//            int count = 0;
//            while (true) {
//                    if (filterStr.equals(breakStr)) {
//                            break;
//                    }
//
//                    filterStr = "" + Integer.toBinaryString(count);
//                    filterStr = StringUtils.leftPad(filterStr, worldLength, '0');
//                    int combOnes = StringUtils.countMatches(filterStr, "1");
//                    if (combOnes == ones) {
//                        BitList filter = BitListBuilder.buildBitList(filterStr);
//                        logger.debug("Adding filter: " + filter);
//                        filters.add(filter);
//                    }
//
//                    count++;
//            }
//
//            String combinationStr = StringUtils.leftPad("", worldLength, '-');
//            BitList combinationBitList = BitListBuilder.buildBitList(combinationStr);
//
//            Partition partition = new Partition(combinationBitList, filters);
//
//            SimpleProcessor processor = new SimpleProcessor(partition);
//            Stopwatch stopWatch =  Stopwatch.createStarted();
//            processor.runAll();
//            stopWatch.stop();
//            Collection<BitList> resultCombinations = processor.getCompletedPartitions();
//
//            Collection<BitList> complementFilters = new ArrayList<BitList>();
//            for(BitList bitList : resultCombinations) {
//                logger.debug(bitList);
//                String complementFilterStr = bitList.toString();
//                BitList complementFilter = BitListBuilder.buildBitList(complementFilterStr);
//                complementFilters.add(complementFilter);                            
//            }
//
//            BitList complementCombination = BitListBuilder.buildBitList(combinationStr);
//            Partition complementPartition = new Partition(complementCombination, complementFilters);
//
//            processor = new SimpleProcessor(complementPartition);
//            stopWatch.start();
//            processor.runAll();
//            stopWatch.stop();
//            Collection<BitList> originalCombinations = processor.getCompletedPartitions();
//
//            Set<String> outputSet = new HashSet<String>();
//            for(BitList origList : originalCombinations) {
//                logger.debug(origList);
//                
//                String bitStr = origList.toString();
//                int numOnes = StringUtils.countMatches(bitStr, "1");
//                Assert.assertTrue(numOnes == ones);
//                
//                Assert.assertFalse(outputSet.contains(bitStr));
//                outputSet.add(bitStr);
//            }
//            
//            int expectedCombinations = (int) MathUtils.binomialCoefficient(worldLength, ones);  
//            Assert.assertTrue(expectedCombinations == outputSet.size());
//
//            int secondsRun = (int) stopWatch.elapsed(TimeUnit.SECONDS);
//            int origSize = originalCombinations.size();
//            logger.info(origSize + " complements found and reverted in " + secondsRun + " seconds");
//    }
}
