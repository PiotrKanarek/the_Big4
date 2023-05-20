package pl.edu.wit.file;

import java.io.File;
import java.util.concurrent.Callable;

public class FileCopyingTask implements Callable<Boolean> {

    public FileCopyingTask(File file, String destinationFolder) {

    }

    @Override
    public Boolean call() throws Exception {
        // TODO implement me :)
        return true;
    }
}
