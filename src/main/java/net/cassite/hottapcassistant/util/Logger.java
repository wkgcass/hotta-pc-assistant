package net.cassite.hottapcassistant.util;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hotta-pc-assistant");

    static {
        // %1:date, %2:source, %3:logger, %4:level, %5:message, %6:thrown
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
        logger.setLevel(Level.ALL);
        var handler = new ConsoleHandler() {
            @Override
            protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
                super.setOutputStream(System.out);
            }
        };
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    private Logger() {
    }

    public static void debug(String msg) {
        logger.log(Level.FINE, msg);
    }

    public static void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    public static void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    }

    public static void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }
}
