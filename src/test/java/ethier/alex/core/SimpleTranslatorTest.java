package ethier.alex.core;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import ethier.alex.world.core.SimpleTranslator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;


/**

 @author alex
 */
public class SimpleTranslatorTest {
    
    private static Logger logger = Logger.getLogger(SimpleTranslatorTest.class);
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }
    
    @Test
    public void testTranslator() throws Exception {
        System.out.println("Running test.");                
        
        SimpleTranslator translator = new SimpleTranslator();
        translator.translateEnumWorld();
    }
    
}
