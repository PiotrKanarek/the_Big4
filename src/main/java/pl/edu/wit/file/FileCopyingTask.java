package pl.edu.wit.file;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * class for copy a file to the destination folder
 *
 * @author piotrkanarek
 */
public class FileCopyingTask implements Callable<Boolean> {

    private static final Logger log = LogManager.getLogger(FileCopyingTask.class.getName());
    private final File file;
    private final String destinationFolder;

    public FileCopyingTask(File file, String destinationFolder) {
        this.file = file;
        this.destinationFolder = destinationFolder;
    }

    /**
     * This method is responsible for performing a copy of the file to the destination folder when called.
     *
     * @return true if the file exists; false if the file does not exist or its existence cannot be determined
     * @see java.util.concurrent.Callable
     */
    @Override
    public Boolean call() {
        return copyFile();
    }

    /**
     * Copies provided file to the destination directory.
     *
     * @return true if the file exists; false if the file does not exist or its existence cannot be determined
     */
    synchronized private Boolean copyFile() {
        String targetDirectory = createTargetDirectory();

        if (targetDirectory == null) {
            log.error("Provided target directory was 'null', unable to copy file");
            return false;
        }

        try {
            return Files.exists(Files.copy(file.toPath(), Path.of(targetDirectory)));

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

        String targetDirectory = destinationFolder + File.pathSeparator + date;
        File subfolder = new File(targetDirectory);
        subfolder.mkdir();

        return targetDirectory;
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
            Metadata metadata = JpegMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

        } catch (ImageProcessingException | IOException | NullPointerException e) {
            log.error("Unable to read metadata from the provided file due to : " + e.getMessage());
            return null;
        }
    }
}
