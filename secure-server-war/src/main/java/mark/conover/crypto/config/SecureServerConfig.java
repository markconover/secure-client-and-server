package mark.conover.crypto.config;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class loads the necessary configurations for the server web application.
 * 
 * @author Mark Conover (conoverm157@gmail.com)
 */
public class SecureServerConfig implements ServletContextListener {
    
    private static final String configFilePath = 
        "/etc/opt/secure-server/config.properties";
    
    // Properties
    public static final String PROP_PUBLIC_KEY_FILE_PATH = "publicKeyFilePath";
    public static final String PROP_PRIVATE_KEY_FILE_PATH = 
		"privateKeyFilePath";
    
    private static final Logger LOG = LoggerFactory.getLogger(
            SecureServerConfig.class);
    
    public static String SECURE_SERVER_PUBLIC_KEY_FILE_PATH = null;
    public static String SECURE_SERVER_PRIVATE_KEY_FILE_PATH = null;
    
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // Load properties from the file
        LOG.debug("Checking for configuration file '{}'", configFilePath);
        
        File configFile = new File(configFilePath);
        PropertiesConfiguration propertiesConfig = null;
        if (configFile.exists()) {
            LOG.debug("Loading configuration from '{}'", configFilePath);
            try {
                propertiesConfig = new PropertiesConfiguration(configFile);
            } catch (ConfigurationException e) {
                LOG.error("Failed to load configuration file '{}'", 
                    configFilePath, e);
            }

            LOG.debug("Loaded configuration file '{}'", configFilePath);
            if (propertiesConfig != null) {
                SECURE_SERVER_PUBLIC_KEY_FILE_PATH = 
                    propertiesConfig.getString(PROP_PUBLIC_KEY_FILE_PATH);
                SECURE_SERVER_PRIVATE_KEY_FILE_PATH = 
                        propertiesConfig.getString(PROP_PRIVATE_KEY_FILE_PATH);
                
            }
        } else {
            LOG.debug("configuration file '{}' does not exist.", 
                configFilePath);
        }      
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
