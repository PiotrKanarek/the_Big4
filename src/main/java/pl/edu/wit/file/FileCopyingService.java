package pl.edu.wit.file;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * service class which orchestrates the core file copying logic
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
    public FileCopyingService(ExecutorService executorService) {
        if (executorService == null) {
            throw new RuntimeException("Constructor parameter 'executorService' was null, unable to instantiate FileCopyingService");
        }
        this.executorService = executorService;
    }

    /**
     * examine the provided source directory & its subdirectories and copy any .jpg files to the provided destination directory
     *
     * @param sourceDirectory      path to the source directory containing the .jpg files to be copied
     * @param destinationDirectory path to the destination directory (where the copied files will be placed)
     * @return number of files which were successfully copied over
     */
    public int copyFiles(String sourceDirectory, String destinationDirectory) {
        List<FileCopyingTask> fileCopyingTasks = FileReader.getFilesToBeCopied(sourceDirectory)
                .stream()
                .map(fileToBeCopied -> new FileCopyingTask(fileToBeCopied, destinationDirectory))
                .collect(Collectors.toList());

        return fileCopyingTasks.isEmpty() ? 0 : countSuccessful(executeTasks(fileCopyingTasks));
    }

    private List<Future<Boolean>> executeTasks(List<FileCopyingTask> fileCopyingTasks) {
        List<Future<Boolean>> processingResults;
        try {
            processingResults = executorService.invokeAll(fileCopyingTasks);
        } catch (Exception e) {
            log.error("Unable to copy files due to unexpected exception: " + e.getMessage());
            return Collections.emptyList();
        }
        return processingResults;
    }

    private int countSuccessful(List<Future<Boolean>> processingResults) {
        int filesCopied = 0;

        for (Future<Boolean> result : processingResults) {
            try {
                if (result.get()) filesCopied++;
            } catch (InterruptedException | ExecutionException e) {
                log.error("Unable to determine the result of file copying task due to " + e.getMessage());
            }
        }
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
