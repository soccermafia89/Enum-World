package ethier.alex.world.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class TestUtils {

    public static boolean compareDoubles(double double1, double double2, double precision) {
        double diff = double1 - double2;

        if (diff < 0) {
            diff = -1 * diff;
        }

        if (diff < precision) {
            return true;
        } else {
            return false;
        }
    }

    public static void setLogLevel(Level level) {
        //Since logging significantly effects performance metrics, raise the level.
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("ethier.alex");
        loggerConfig.setLevel(level);
        ctx.updateLoggers();  // This causes all Loggers to refetch information
    }
}
