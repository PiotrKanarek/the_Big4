package pl.edu.wit.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileCopyingTaskTest {

    @Test
    public void copyFileTest() {
        File file = new File("src/test/resources/test_files/test.jpg");
        String destinationFolder = "src/test/resources/test_destination/";

        FileCopyingTask fileCopyingTask = new FileCopyingTask(file, destinationFolder);
        boolean testResult = fileCopyingTask.call();

        Assertions.assertTrue(testResult);
    }
}