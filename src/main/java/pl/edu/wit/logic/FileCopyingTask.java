package pl.edu.wit.logic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * class for copy a file to the destination folder
 *
 * @author piotrkanarek
 */
public class FileCopyingTask implements Callable<Boolean> {

    private static final Logger log = LogManager.getLogger(FileCopyingTask.class.getName());
    //File to copy
    private final File file;
    //The path to which the file will be copied
    private final String destinationFolder;

    public FileCopyingTask(File file, String destinationFolder) {
        this.file = file;
        this.destinationFolder = destinationFolder;
    }

    /**
     * Copies provided file to the destination directory.
     *
     * @return true if the file exists; false if the file does not exist or its existence cannot be determined
     */
    @Override
    public Boolean call() {
        String targetDirectory = createTargetDirectory();

        if (targetDirectory == null) {
            log.error("Provided target directory was 'null', unable to copy file");
            return false;
        }

        try {
            Path path = Path.of(targetDirectory);
            Path target = path.resolve(getNameForFile(targetDirectory));
            Path resultPath = Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

            log.debug("Copying " + file + " to " + target);

            return Files.exists(resultPath);

        } catch (SecurityException | UnsupportedOperationException | IOException e) {
            log.error("Unable to copy file due to " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a directory to which the file will be copied. If the subfolder does not exist, the method will also create it.
     * The name of the created directory consists of the destination folder and the date the file was created.
     *
     * @return target directory to which the file will be copied
     */
    synchronized private String createTargetDirectory() {
        if (file == null) {
            log.error("Provided file was 'null', unable to read metadata");
            return null;
        }
        Date date = getDateFromFileMetadata();

        if (date == null) {
            log.error("Provided date was 'null, unable to create proper target directory");
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = dateFormat.format(date);

        String targetDirectory = destinationFolder + File.separator + parsedDate;
        new File(targetDirectory).mkdirs();

        return targetDirectory;
    }

    /**
     * Gets a new name for the file. The name created is another integer value.
     *
     * @param directory target directory to which the file will be copied
     * @return integer value for filename
     */
    private String getNameForFile(String directory) {
        synchronized (FileCopyingService.mapFolders) {
            Map<String, Integer> mapFolders = FileCopyingService.mapFolders;

            if (mapFolders.containsKey(directory)) {
                int counter = mapFolders.get(directory);
                counter++;
                mapFolders.put(directory, counter);
                return counter + ".jpg";
            } else {
                mapFolders.put(directory, 1);
                return "1.jpg";
            }
        }
    }

    /**
     * Retrieves the date from the given file metadata.
     *
     * @return picture taken date
     */
    private Date getDateFromFileMetadata() {
        if (file == null) {
            log.error("Provided file was 'null', unable to read metadata");
            return null;
        }

        try {
            FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "creationTime");

            return Date.from(creationTime.toInstant());
        } catch (IOException | NullPointerException e) {
            log.error("Unable to get metadata from file, due to " + e.getMessage());
            return null;
        }
    }


}
