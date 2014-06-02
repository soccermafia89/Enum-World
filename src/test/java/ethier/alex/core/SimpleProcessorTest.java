///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package ethier.alex.core;
//
//import ethier.alex.core.addon.BitListBuilder;
//import ethier.alex.core.data.BitList;
//import ethier.alex.core.data.Partition;
//import ethier.alex.core.processor.SimpleProcessor;
//import java.util.ArrayList;
//import java.util.Collection;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//
///**
//
// @author alex
// */
//public class SimpleProcessorTest {
//    
//    private static Logger logger = Logger.getLogger(SimpleProcessorTest.class);
//    
//    @BeforeClass
//    public static void setUpClass() {
//        BasicConfigurator.configure();
//    }
//    
//    @Test
//    public void testProcessor() throws Exception {
//        System.out.println("Running test.");                
//        
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
//        Assert.assertTrue(true);
//    }
//    
//}
