package pl.edu.wit.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * utility class responsible for providing an ExecutorService configured with appropriate pool size
 *
 * @author Katarzyna Nowak
 */
public class ExecutorConfigurator {

    private static final Logger log = LogManager.getLogger(ExecutorConfigurator.class.getName());

    private ExecutorConfigurator() {
        // constructor is private because this class is not supposed to be instantiated (utility clas
    }

    /**
     * utility method for obtaining a configured {@link ExecutorService}
     *
     * @param propertySource {@link PropertySource} from which the required thread pool size should be read
     * @return {@link ExecutorService} configured with a fixed thread pool of appropriate size,
     * or with a default size=3 if the relevant property (pool-size) is not available in the provided {@link PropertySource}
     */
    public static ExecutorService getConfiguredExecutor(PropertySource propertySource) {
        // default pool size value that will be applied if a relevant configuration property is unavailable
        int poolSize = 3;

        if (propertySource != null) {
            String poolSizeProperty = propertySource.getProperty("pool-size");
            try {
                poolSize = Integer.parseInt(poolSizeProperty);
            } catch (NumberFormatException e) {
                log.warn("Unable to get pool size from property source (expected valid int, found: " +
                        poolSizeProperty + "), will default to " + poolSize);
            }
        }

        log.info("Starting ExecutorService with a fixed thread pool of size=" + poolSize);
        return Executors.newFixedThreadPool(poolSize);
    }
}
