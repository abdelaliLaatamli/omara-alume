package main.Logging;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MyFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        //don't log CONFIG logs in file
        if(record.getLevel() == Level.CONFIG) return false;
        return true;
    }
}
