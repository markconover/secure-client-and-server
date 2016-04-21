package mark.conover.crypto.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Logging Configuration.
 *
 * Checks for an external log file, and defaults to classpath configuration.
 */
public class LoggingConfig {

    private static final String LOGBACK_CONFIG = "/resources/logback.xml";

    private Logger log;

    /**
     * Configure logback
     */
    public void configure() {
        // Look for external configuration file first
        File configFile = new File(LOGBACK_CONFIG);
        if(configFile.exists()) {
            // Attempt to configure with it
            try {
                configureFile(configFile);
                getLogger();
                log.info("Logging configured with file: {}", configFile);
            } catch(JoranException e) {
                // Configuration failed, use default logging config
                getLogger();
                log.error("Failed to configure logback with file: {}", 
                    configFile);
                log.info("Using default logging configuration.");
            }
        } else {
            // No external file found, use default configuration
            getLogger();
            log.debug("Logging configuration file '{}' not found");
            log.info("Using default logging configuration.");
        }
    }

    private void configureFile(File file) throws JoranException {
        LoggerContext context = (LoggerContext) 
            LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        configurator.doConfigure(file);
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    private void getLogger() {
        this.log = LoggerFactory.getLogger(LoggingConfig.class);
    }

}

