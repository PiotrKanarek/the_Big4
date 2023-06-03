package pl.edu.wit.file;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;
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
        List<Callable<Boolean>> fileCopyingTasks = FileReader.getFilesToBeCopied(sourceDirectory)
                .stream()
                .map(fileToBeCopied -> new FileCopyingTask(fileToBeCopied, destinationDirectory))
                .collect(Collectors.toList());

        if (fileCopyingTasks.isEmpty()) {
            return 0;
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
