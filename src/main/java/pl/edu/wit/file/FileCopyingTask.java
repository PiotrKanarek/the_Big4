package pl.edu.wit.file;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.Callable;

public class FileCopyingTask implements Callable<Boolean> {

    private static final Logger log = LogManager.getLogger(FileCopyingTask.class.getName());

    public FileCopyingTask(File file, String destinationFolder) {

    }

    @Override
    public Boolean call() {
        // TODO implement me :)
        log.warn("file copying yet to be implemented :)");
        return true;
    }
}
