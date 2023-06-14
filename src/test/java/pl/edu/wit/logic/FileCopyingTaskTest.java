package pl.edu.wit.logic;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileCopyingTaskTest {

    @Test
    public void copyFileTest() {
        File file = new File("src/test/resources/test_files/test.jpg");
        String destinationFolder = "src/test/resources/destination_directory";

        FileCopyingTask fileCopyingTask = new FileCopyingTask(file, destinationFolder);
        boolean testResult = fileCopyingTask.call();

        Assertions.assertTrue(testResult);
    }

    @AfterEach
    public void cleanup() {
        String path = "src/test/resources/destination_directory/";

        FileUtils.deleteQuietly(new File(path));

    }
}