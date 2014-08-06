package ethier.alex.world.app.test;

import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.core.data.ElementList;
import ethier.alex.world.core.data.FilterList;
import ethier.alex.world.core.data.Partition;
import ethier.alex.world.core.processor.DeepProcessor;
import ethier.alex.world.core.processor.Processor;
import ethier.alex.world.core.processor.SimpleProcessor;
import ethier.alex.world.query.SimpleQuery;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
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
public class ResistanceTest {

    private static Logger logger = LogManager.getLogger(ResistanceTest.class);

    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
    }

    @Test
    public void testGame() throws Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********         Game Test         *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
//        this.setLogLevel(Level.TRACE);

        List<String> players = new ArrayList<String>();
        players.add("alex");
        players.add("charlie");
        players.add("james");
        players.add("liz");
        players.add("hersh");
        players.add("raj");
        players.add("annie");
        players.add("gabe");

        int[] rounds = {3, 4, 4, 5, 5};

        Game game = new Game(players, 3, rounds);

        Set<String> round0Players = new HashSet<String>();
        round0Players.add("alex");
        round0Players.add("gabe");
        round0Players.add("annie");
        game.playRound(round0Players, 0);

        game.applyVouch("alex", "gabe");

        Set<String> round1Players = new HashSet<String>();
        round1Players.add("alex");
        round1Players.add("gabe");
        round1Players.add("annie");
        round1Players.add("charlie");
        game.playRound(round1Players, 1);

        game.applyVouch("james", "charlie");
        game.applyVoteVouch("gabe", "charlie", 1);

        Set<String> round2Players = new HashSet<String>();
        round2Players.add("gabe");
        round2Players.add("charlie");
        round2Players.add("james");
        round2Players.add("raj");
        game.playRound(round2Players, 1);

        game.applyVoteVouch("gabe", "james", 2);

        Set<String> round3Players = new HashSet<String>();
        round3Players.add("gabe");
        round3Players.add("hersh");
        round3Players.add("charlie");
        round3Players.add("james");
        round3Players.add("raj");
        game.playRound(round3Players, 0);

//        game.assumeResistance("john");

        Partition gamePartition = game.createRootPartition();

        Processor processor = new DeepProcessor();
        processor.setPartition(gamePartition);
//        simpleProcessor.setPartition(gamePartition);
        processor.runAll();
        Collection<ElementList> elements = processor.getCompletedPartitions();

        SimpleQuery wizard = new SimpleQuery(gamePartition.getRadices(), elements);
        for (int i = 0; i < players.size(); i++) {
            String player = players.get(i);

            String queryStr = StringUtils.leftPad("", i, "*");
            queryStr += "1";
            queryStr = StringUtils.rightPad(queryStr, gamePartition.getRadices().length, "*");

            FilterList query = FilterListBuilder.newInstance().setQuick(queryStr).getFilterList();

            logger.info("Query: " + query);

            double probSpy = wizard.query(query);

            logger.info("Player: " + player + " has probability of being a spy: " + probSpy);
        }

        logger.info("World Size: " + wizard.getWorldSize());
    }

    @Test
    public void testAdvancedResistance() throws Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********       Advanced Test       *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
//        this.setLogLevel(Level.TRACE);

        List<String> players = new ArrayList<String>();
        players.add("alex");
        players.add("john");
        players.add("liz");
        players.add("liban");
        players.add("jeanie");

        int[] rounds = {2, 3, 2, 3, 3};

        Game game = new Game(players, 2, rounds);

        Set<String> round0Players = new HashSet<String>();
        round0Players.add("alex");
        round0Players.add("john");
        game.playRound(round0Players, 0);

        Set<String> round1Players = new HashSet<String>();
        round1Players.add("jeanie");
        round1Players.add("liban");
        round1Players.add("liz");
        game.playRound(round1Players, 1);

        game.applyVoteVouch("john", "liban", 2);
        game.applyVoteAccusation("alex", "liban", 2);

        Set<String> round2Players = new HashSet<String>();
        round2Players.add("john");
        round2Players.add("liban");
        game.playRound(round2Players, 0);

        Set<String> round3Players = new HashSet<String>();
        round3Players.add("john");
        round3Players.add("liban");
        round3Players.add("jeanie");
        game.playRound(round3Players, 1);

        game.applyVoteAccusation("jeanie", "john", 3);
        game.applyAccustion("liban", "jeanie");
        game.applyVouch("liban", "john");
        game.applyAccustion("alex", "jeanie");
        game.applyVouch("john", "liz");

//        game.assumeResistance("john");

        Partition gamePartition = game.createRootPartition();

        Processor processor = new SimpleProcessor();
//        Processor processor = new DeepProcessor();
        processor.setPartition(gamePartition);
//        simpleProcessor.setPartition(gamePartition);
        processor.runAll();
        Collection<ElementList> elements = processor.getCompletedPartitions();

        SimpleQuery wizard = new SimpleQuery(gamePartition.getRadices(), elements);
        for (int i = 0; i < players.size(); i++) {
            String player = players.get(i);

            String queryStr = StringUtils.leftPad("", i, "*");
            queryStr += "1";
            queryStr = StringUtils.rightPad(queryStr, gamePartition.getRadices().length, "*");

            FilterList query = FilterListBuilder.newInstance().setQuick(queryStr).getFilterList();

            logger.info("Query: " + query);

            double probSpy = wizard.query(query);

            if (player.equals("alex") || player.equals("jeanie")) {
                Assert.assertTrue(probSpy == 1.0);
            } else {
                Assert.assertTrue(probSpy == 0.0);
            }
            logger.info("Player: " + player + " has probability of being a spy: " + probSpy);
        }

        logger.info("World Size: " + wizard.getWorldSize());
    }

    @Test
    public void testBasicResistance1() throws Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********        Basic Test  1      *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
//        this.setLogLevel(Level.TRACE);

        List<String> players = new ArrayList<String>();
        players.add("alex");
        players.add("john");
        players.add("liz");
        players.add("liban");
        players.add("jeanie");

//        int[] rounds = {2, 3, 2, 3, 3};
        int[] rounds = {2, 3, 2, 3};

        Game game = new Game(players, 2, rounds);

        game.assumeResistance("alex");
        game.applyAccustion("alex", "john");
        game.applyVouch("alex", "liz");
        game.applyVouch("john", "liban");
        game.applyAccustion("liz", "liban");

        Partition gamePartition = game.createRootPartition();

        Processor processor = new SimpleProcessor();
        processor.setPartition(gamePartition);
//        simpleProcessor.setPartition(gamePartition);
        processor.runAll();
        Collection<ElementList> elements = processor.getCompletedPartitions();
//        for (ElementList element : elements) {
//            System.out.println("Element Found: " + element);
//        }


        SimpleQuery wizard = new SimpleQuery(gamePartition.getRadices(), elements);
        for (int i = 0; i < players.size(); i++) {
            String player = players.get(i);

            String queryStr = StringUtils.leftPad("", i, "*");
            queryStr += "1";
            queryStr = StringUtils.rightPad(queryStr, gamePartition.getRadices().length, "*");

            FilterList query = FilterListBuilder.newInstance().setQuick(queryStr).getFilterList();

//            logger.info("Query: " + query);

            double probSpy = wizard.query(query);

            if (player.equals("liban") || player.equals("john")) {
                Assert.assertTrue(probSpy == 1.0);
            } else {
                Assert.assertTrue(probSpy == 0.0);
            }

            logger.info("Player: " + player + " has probability of being a spy: " + probSpy);
        }

        logger.info("World Size: " + wizard.getWorldSize());
    }
    
    private void setLogLevel(Level level) {
        //Since logging significantly effects performance metrics, raise the level.
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("ethier.alex");
            loggerConfig.setLevel(level);
        ctx.updateLoggers();  // This causes all Loggers to refetch information
    }
}
