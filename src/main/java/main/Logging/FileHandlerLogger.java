package main.Logging;

import java.io.IOException;
import java.util.logging.*;


public class FileHandlerLogger {

    public static Logger getHandlerFile( String className ){


         Logger logger = Logger.getLogger(className);

        try {
            //LogManager.getLogManager().readConfiguration(new FileInputStream("mylogging.properties"));
            LogManager.getLogManager().readConfiguration(FileHandlerLogger.class.getClassLoader().getResourceAsStream("mylogging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }

            logger.setLevel(Level.FINE);
            logger.addHandler(new ConsoleHandler());
            //adding custom handler
            logger.addHandler(new MyHandler());
            try {

                Handler fileHandler = new FileHandler("./logger.log" , true);
                fileHandler.setFormatter(new MyFormatter());
                //setting custom filter for FileHandler
                fileHandler.setFilter(new MyFilter());
                logger.addHandler(fileHandler);
                fileHandler.close();
            } catch (SecurityException | IOException e) {
                return null;
            }
         return  logger;
    }

}
