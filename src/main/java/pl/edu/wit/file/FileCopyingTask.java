package pl.edu.wit.file;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Callable;

public class FileCopyingTask implements Callable<Boolean> {

    private static final Logger log = LogManager.getLogger(FileCopyingTask.class.getName());
    private final File file;
    private final String destinationFolder;

    public FileCopyingTask(File file, String destinationFolder) {
        this.file = file;
        this.destinationFolder = destinationFolder;
    }

    @Override
    public Boolean call() throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        String targetDirectory;

        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

        targetDirectory = destinationFolder + File.pathSeparator + date;
        File subfolder = new File(targetDirectory);
        subfolder.mkdir();

        Files.copy(file.toPath(), Path.of(targetDirectory));

        return true;
    }
}
