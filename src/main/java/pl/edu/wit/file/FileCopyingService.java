package pl.edu.wit.file;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.wit.config.PropertySource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * service class which orchestrates the core file copying logic
 * </p>
 *
 * @author Katarzyna Nowak
 */
public class FileCopyingService implements AutoCloseable {

    private static final Logger log = LogManager.getLogger(FileCopyingService.class.getName());

    private final ExecutorService executorService;

    /**
     * initialize the underlying ExecutorService with a thread pool size as defined in the 'application.properties' file
     * under the 'pool-size' key, or default to size=3 if the relevant property cannot be obtained
     */
    public FileCopyingService() {
        String poolSizeProperty = PropertySource.getProperty("pool-size");

        // default pool size value that will be applied if a relevant configuration property is unavailable
        int poolSize = 3;

        try {
            poolSize = Integer.parseInt(poolSizeProperty);
        } catch (NumberFormatException e) {
            log.warn("Unable to get pool size from property source (expected valid int, found: " +
                    poolSizeProperty + "), will default to " + poolSize);
        }

        log.info("Starting ExecutorService with a thread pool size = " + poolSize);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * this method will examine the provided source directory & its subdirectories
     * and copy any .jpg files to the provided destination directory
     *
     * @param sourceDirectory      path to the source directory containing the .jpg files to be copied
     * @param destinationDirectory path to the destination directory (where the copied files will be placed)
     * @return number of files which were successfully copied over
     */
    public int copyFiles(String sourceDirectory, String destinationDirectory) {
        List<File> jpgFiles = FileReader.getAllFiles(sourceDirectory)
                .stream()
                .filter(file -> file.getName().endsWith(".jpg"))
                .collect(Collectors.toList());

        if (jpgFiles.size() == 0) {
            log.info("No '.jpg' files found in the provided directory: '" + sourceDirectory + "'");
            return 0;
        }

        log.info("Found " + jpgFiles.size() + " '.jpg' files to be copied");

        List<Callable<Boolean>> fileCopyingTasks = new ArrayList<>();

        for (File fileToBeCopied : jpgFiles) {
            fileCopyingTasks.add(new FileCopyingTask(fileToBeCopied, destinationDirectory));
        }

        List<Future<Boolean>> processingResults;

        try {
            processingResults = executorService.invokeAll(fileCopyingTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int filesCopied = 0;

        for (Future<Boolean> result : processingResults) {
            try {
                if (result.get()) {
                    filesCopied++;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        }

        log.info("Successfully copied " + filesCopied + " files");
        return filesCopied;
    }

    /**
     * method responsible for closing the underlying ExecutorService; it will wait 3 seconds for ExecutorService termination
     * to complete gracefully before forcing a shutdown; it will be called automatically if the FileCopyingService is
     * initialized as resource of the 'try-with-resources' syntax
     */
    @Override
    public void close() {
        log.info("Shutting down the ExecutorService");

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
