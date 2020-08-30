package local.nix.financial.management.logger;

import org.apache.log4j.Logger;

public class ApplicationLogger {

    private final static Logger logger = Logger.getLogger("Application logger");

    public static Logger getLogger() {
        return logger;
    }
}
