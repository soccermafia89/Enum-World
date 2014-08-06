/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.test.driver;

import com.google.common.base.Stopwatch;
import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.*;
import ethier.alex.world.core.processor.MetricsProcessor;
import ethier.alex.world.core.processor.SimpleProcessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */

public class TestDriver {
    
    private static Logger logger = LogManager.getLogger(TestDriver.class);
    
    public static void main(String[] args) {
        TestDriver driver = new TestDriver();
        driver.drive();
    }
    
    public void drive() {
//        BasicConfigurator.configure();
        this.testComplements();
    }
    
    public void testComplements() {
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


//            int ones = 10;
//            int worldLength = 20;
            int ones = 9;
            int worldLength = 18;

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

                    logger.debug("Adding filter: {}", filter);
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
            processor.printFullMetrics();
            stopWatch.stop();
//            Collection<ElementList> resultCombinations = processor.getCompletedPartitions();
//
//            Collection<FilterList> complementFilters = generateComplementFilters(resultCombinations);
//
//            Partition complementPartition = PartitionBuilder.newInstance()
//                    .setRadices(radices)
//                    .setBlankWorld()
//                    .addFilters(complementFilters)
//                    .getPartition();
//
//            processor.reset();
//            processor.setPartition(complementPartition);
//            stopWatch.start();
//            processor.runAll();
//            processor.printMetrics();
//            stopWatch.stop();
//            Collection<ElementList> originalCombinations = processor.getCompletedPartitions();
//
//            Set<String> outputSet = new HashSet<String>();
//            for(ElementList origList : originalCombinations) {
//                
//                String enumStr = origList.toString();
//                int numOnes = StringUtils.countMatches(enumStr, "1");
//                
//                outputSet.add(enumStr);
//            }
//            
//            int expectedCombinations = (int) MathUtils.binomialCoefficient(worldLength, ones);  
//
            int secondsRun = (int) stopWatch.elapsed(TimeUnit.SECONDS);
//            int origSize = originalCombinations.size();
            logger.info("Complements found took {} seconds to run.", secondsRun);
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
