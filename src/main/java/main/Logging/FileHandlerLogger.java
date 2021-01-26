package main.Logging;

import java.io.IOException;
import java.util.logging.FileHandler;


public class FileHandlerLogger {

    public static FileHandler getHandlerFile(){

        FileHandler fh;

        try {
            fh = new FileHandler("default.log" , true);
            return fh ;
        } catch (SecurityException e) {
            return null ;
        } catch (IOException e) {
            return null ;
        }

    }

}
