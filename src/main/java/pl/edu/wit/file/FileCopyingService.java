package pl.edu.wit.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * service class which handles the core file copying logic
 * </p>
 *
 * @author Katarzyna Nowak
 */
public class FileCopyingService {

    private final ExecutorService executorService;

    /**
     * @param poolSize size of the thread pool which will be used by the {@link ExecutorService} handling file copying
     */
    public FileCopyingService(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * this method will examine the provided source directory & its subdirectories
     * and copy any .jpg files to the provided destination directory
     *
     * @param sourceFolder      path to the source directory containing the .jpg files to be copied
     * @param destinationFolder path to the destination directory (where the copied files will be placed)
     * @return number of files which were successfully copied over
     */
    public int copyFiles(String sourceFolder, String destinationFolder) {
        List<File> jpgFiles = FileReader.getAllFiles(sourceFolder)
                .stream()
                .filter(file -> file.getName().endsWith(".jpg"))
                .collect(Collectors.toList());

        if (jpgFiles.size() == 0) {
            System.out.println("No .jpg files found in the provided directory: " + sourceFolder);
            return 0;
        }

        System.out.println("Found " + jpgFiles.size() + " .jpg files to be copied");

        List<Callable<Boolean>> fileCopyingTasks = new ArrayList<>();

        for (File fileToBeCopied : jpgFiles) {
            fileCopyingTasks.add(new FileCopyingTask(fileToBeCopied, destinationFolder));
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

        System.out.println("Successfully copied " + filesCopied + " files");
        return filesCopied;
    }
}
