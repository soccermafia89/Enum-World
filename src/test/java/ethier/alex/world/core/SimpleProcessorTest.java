/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core;

import com.google.common.base.Stopwatch;
import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.SimpleProcessor;
import ethier.alex.world.query.Wizard;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
        
        int[] radices = new int[4];
        radices[0] = 3;
        radices[1] = 2;
        radices[2] = 3;
        radices[3] = 2;
        
        FilterList filter1 = FilterListBuilder.newInstance()
                .setOrdinals(new int[] {0, -1, -1, -1})
                .getFilterList();
        
        FilterList filter2 = FilterListBuilder.newInstance()
                .setOrdinals(new int[] {-1, -1, 1, -1})
                .getFilterList();
        
        FilterList filter3 = FilterListBuilder.newInstance()
                .setOrdinals(new int[] {-1, -1, 0, -1})
                .getFilterList();
        
        FilterList filter4 = FilterListBuilder.newInstance()
                .setOrdinals(new int[] {1, 1, 2, 0})
                .getFilterList();
        
        Partition rootPartition = PartitionBuilder
                .newInstance()
                .setBlankWorld()
                .setRadices(radices)
                .addFilter(filter1)
                .addFilter(filter2)
                .addFilter(filter3)
                .addFilter(filter4)
                .getPartition();
        
        SimpleProcessor simpleProcessor = new SimpleProcessor(rootPartition);
        
        simpleProcessor.runAll();
        
        Collection<ElementList> finalElements = simpleProcessor.getCompletedPartitions();
        for(ElementList finalElement : finalElements) {
            System.out.println("Final partition computed: " + finalElement.toString());
        }
        
        FilterList filter1query1 = FilterListBuilder.newInstance()
                .setQuick("**2*")
                .getFilterList();
        
//        FilterList filter1query2 = FilterListBuilder.newInstance()
//                .setQuick("10**")
//                .getFilterList();
//        FilterList filter2query2 = FilterListBuilder.newInstance()
//                .setQuick("2***")
//                .getFilterList();
//        Collection<FilterList> query2 = new ArrayList();
//        query2.add(filter1query2);
//        query2.add(filter2query2);
        
        Wizard query = new Wizard(radices, finalElements);
        double queryResult1 = query.query(filter1query1);
//        double queryResult2 = query.query(query2);
        
        Assert.assertTrue(queryResult1 == 1.0);
//        Assert.assertTrue(queryResult2 == 6 / (double) 7);
//        System.out.println("Query: " + filter1query1 + " => " + queryResult1);
//        System.out.println("Query: " + query2 + " => " + queryResult2);
    }
    
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
            int ones = 2;
            int worldLength = 4;

            Collection<FilterList> filters = new ArrayList<FilterList>();
            int[] radices = new int[worldLength];

            String breakStr = "";
            for (int i = 0; i < worldLength; i++) {
                    breakStr += '1';
                    radices[i] = 2;
            }

            String filterStr = "";
            int count = 0;
            while (!filterStr.equals(breakStr)) {

                filterStr = "" + Integer.toBinaryString(count);
                filterStr = StringUtils.leftPad(filterStr, worldLength, '0');
                int combOnes = StringUtils.countMatches(filterStr, "1");
                if (combOnes == ones) {
                    FilterList filter = FilterListBuilder.newInstance()
                            .setQuick(filterStr)
                            .getFilterList();

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
            Collection<ElementList> resultCombinations = processor.getCompletedPartitions();

            Collection<FilterList> complementFilters = new ArrayList<FilterList>();
            for(ElementList resultCombination : resultCombinations) {
                logger.debug(resultCombination);
                int[] resultOrdinals = resultCombination.getOrdinals();
                
                Enum[] resultElementStates = resultCombination.getElementStates();
                FilterState[] complementFilterStates = new FilterState[resultElementStates.length];
                for(int i=0; i < resultElementStates.length; i++) {
                    if(resultElementStates[i] == ElementState.ALL) {
                        complementFilterStates[i] = FilterState.ALL;
                    } else if(resultElementStates[i] == ElementState.SET){
                        complementFilterStates[i] = FilterState.ONE;
                    } else {
                        throw new RuntimeException("Invalid state reached.");
                    }                    
                }
                                
                FilterList complementFilter = FilterListBuilder.newInstance()
                        .setOrdinals(resultOrdinals)
                        .setFilterStates(complementFilterStates)
                        .getFilterList();
                                
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
            Collection<ElementList> originalCombinations = processor.getCompletedPartitions();

            Set<String> outputSet = new HashSet<String>();
            for(ElementList origList : originalCombinations) {
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
