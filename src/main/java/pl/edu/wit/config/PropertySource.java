package pl.edu.wit.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertySource {

    private static final Logger log = LogManager.getLogger(PropertySource.class.getName());

    /**
     * this method will try to load properties from 'resources/application.properties' and extract the requested property;
     * if loading fails, of if the requested property key is not found among the loaded properties, null will be returned
     *
     * @param propertyKey key (name) of the required property
     * @return value from the 'application.properties' config file corresponding to the requested key
     * or 'null' if value was unavailable (e.g. file was not loaded properly or it didn't contain the required key mapping)
     */
    public static String getProperty(String propertyKey) {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            log.error("Unable to load properties from path: 'src/main/resources/application.properties' - " + e.getMessage());
            e.printStackTrace();
        }

        return (String) properties.get(propertyKey);
    }

}
