package pl.edu.wit.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertySource {

    private static final Logger log = LogManager.getLogger(PropertySource.class.getName());

    private final Properties properties;

    /**
     * this constructor will initialize a {@link Properties} object and attempt to load properties from the file
     * in the specified path; if loading the file fails, properties will remain empty
     *
     * @param path path to the properties file which should be loaded as the property source
     */
    public PropertySource(String path) {
        this.properties = new Properties();

        try {
            log.info("Loading properties from '" + path + "'");
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            log.error("Unable to load properties from '" + path + "' - " + e.getMessage());
        }
    }

    /**
     * extract property corresponding to the provided key from the underlying {@link Properties};
     * if no mapping for the requested key is found, null will be returned
     *
     * @param propertyKey key (name) of the required property
     * @return {@link String} value from the available properties corresponding to the requested key, or 'null' if value was unavailable
     * (e.g. file was not loaded properly, or it didn't contain the required key mapping)
     */
    public String getProperty(String propertyKey) {
        return (String) properties.get(propertyKey);
    }

}
