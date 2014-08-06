/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.core;

import com.google.common.base.Stopwatch;
import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.MetricsProcessor;
import ethier.alex.world.core.processor.SimpleProcessor;
import ethier.alex.world.query.SimpleQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**

 @author alex
 */
public class SimpleProcessorTest {
    
    private static Logger logger = LogManager.getLogger(SimpleProcessorTest.class);
    
    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
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
        
        int[] radices = new int[6];
        radices[0] = 3;
        radices[1] = 2;
        radices[2] = 3;
        radices[3] = 2;
        radices[4] = 5;
        radices[5] = 5;
        
        FilterList filter1 = FilterListBuilder.newInstance()
                .setQuick("0*****")
                .getFilterList();
        
        FilterList filter2 = FilterListBuilder.newInstance()
                .setQuick("**1***")
                .getFilterList();
        
        FilterList filter3 = FilterListBuilder.newInstance()
                .setQuick("**0***")
                .getFilterList();
        
        FilterList filter4 = FilterListBuilder.newInstance()
                .setQuick("1120**")
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
        
//        Processor simpleProcessor = new SimpleProcessor();
        MetricsProcessor processor = new MetricsProcessor(new SimpleProcessor());
        processor.setPartition(rootPartition);
        
        processor.runAll();
        processor.printFullMetrics();
        
        Collection<ElementList> finalElements = processor.getCompletedPartitions();
        for(ElementList finalElement : finalElements) {
            System.out.println("Final partition computed: " + finalElement.toString());
        }
        
        
        FilterList filter1query1 = FilterListBuilder.newInstance()
                .setQuick("**2***")
                .getFilterList();
        
        Collection<FilterList> filterQueries2 = new ArrayList<FilterList>();
        FilterList filter1query2 = FilterListBuilder.newInstance()
    		  .setQuick("1*****")
    		  .getFilterList();
        FilterList filter2query2 = FilterListBuilder.newInstance()
    		  .setQuick("2*****")
    		  .getFilterList();
        filterQueries2.add(filter1query2);
        filterQueries2.add(filter2query2);
        
        Collection<FilterList> filterQueries3 = new ArrayList<FilterList>();
        FilterList filter1query3 = FilterListBuilder.newInstance()
    		  .setQuick("10****")
    		  .getFilterList();
        FilterList filter2query3 = FilterListBuilder.newInstance()
    		  .setQuick("2**1**")
    		  .getFilterList();
        filterQueries3.add(filter1query3);
        filterQueries3.add(filter2query3);
        
        SimpleQuery query = new SimpleQuery(radices, finalElements);
        System.out.println("World Size: " + query.getWorldSize());
        
        double queryResult1 = query.query(filter1query1);
        Assert.assertTrue(queryResult1 == 1.0);
        
        double queryResult2 = query.query(filterQueries2);
        Assert.assertTrue(queryResult2 == 1.0);
        
        System.out.println("Running query 3.");
        System.out.println("");
        double queryResult3 = query.query(filterQueries3);
        System.out.println("Query Result 3: " + queryResult3);
        Assert.assertTrue(TestUtils.compareDoubles(queryResult3, (4.0 / 7.0), 0.000001));

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
            
            //Since logging significantly effects performance metrics, raise the level.
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig("ethier.alex");
//            loggerConfig.setLevel(Level.ERROR);
            loggerConfig.setLevel(Level.ERROR);
            ctx.updateLoggers();  // This causes all Loggers to refetch information

            //This is testing the complement done by creating an arbitrary set of allowed combinations
            //Then creating filters out of them, applying and getting the new set of combinations (which is the complement)
            //Do the process again to get the complement of the complement and we should get back to what we started.

            //Note the complement is a great way to determine the efficiency of the algorithm.

//            int ones = 10;
//            int worldLength = 20;
            int ones = 8;
            int worldLength = 16;

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

            MetricsProcessor processor = new MetricsProcessor(new SimpleProcessor());
            processor.setPartition(rootPartition);
            Stopwatch stopWatch =  Stopwatch.createStarted();
            processor.runAll();
            processor.printAggregateMetrics();
            stopWatch.stop();

            int secondsRun = (int) stopWatch.elapsed(TimeUnit.SECONDS);
            logger.info("Complements found in {} seconds", secondsRun);
    }
    
    @Test
    public void testFullComplements() throws Exception {
            System.out.println("");
            System.out.println("");
            System.out.println("********************************************");
            System.out.println("********    Full Complement Test   *********");
            System.out.println("********************************************");
            System.out.println("");
            System.out.println("");

            //This is testing the complement done by creating an arbitrary set of allowed combinations
            //Then creating filters out of them, applying and getting the new set of combinations (which is the complement)
            //Do the process again to get the complement of the complement and we should get back to what we started.

            //Note the complement is a great way to determine the efficiency of the algorithm.


//            int ones = 10;
//            int worldLength = 20;
            int ones = 4;
            int worldLength = 8;

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

            MetricsProcessor processor = new MetricsProcessor(new SimpleProcessor());
            processor.setPartition(rootPartition);
            Stopwatch stopWatch =  Stopwatch.createStarted();
            processor.runAll();
            processor.printAggregateMetrics();
            stopWatch.stop();
            Collection<ElementList> resultCombinations = processor.getCompletedPartitions();

            Collection<FilterList> complementFilters = generateComplementFilters(resultCombinations);

            Partition complementPartition = PartitionBuilder.newInstance()
                    .setRadices(radices)
                    .setBlankWorld()
                    .addFilters(complementFilters)
                    .getPartition();

            processor.reset();
            processor.setPartition(complementPartition);
            stopWatch.start();
            processor.runAll();
            processor.printAggregateMetrics();
            stopWatch.stop();
            Collection<ElementList> originalCombinations = processor.getCompletedPartitions();

            Set<String> outputSet = new HashSet<String>();
            for(ElementList origList : originalCombinations) {
                
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
    
    public Collection<FilterList> generateComplementFilters(Collection<ElementList> elements) {
        
        Collection<FilterList> complementFilters = new ArrayList<FilterList>();
        for(ElementList resultCombination : elements) {
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
        
        return complementFilters;
    }
}
