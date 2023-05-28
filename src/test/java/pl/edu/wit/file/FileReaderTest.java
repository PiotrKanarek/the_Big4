package pl.edu.wit.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileReaderTest {

    @Test
    void readAllFilesTest(@TempDir Path tempDir) throws IOException {
        // given
        Files.createFile(tempDir.resolve("jpgFile1.jpg"));
        Files.createFile(tempDir.resolve("jpgFile2.jpg"));
        Files.createDirectory(Path.of(tempDir + "/subfolder"));
        Files.createFile(tempDir.resolve(Path.of(tempDir + "/subfolder/jpgFile3.jpg")));

        // when
        List<File> actual = FileReader.getAllFiles(tempDir.toString());

        // then
        assertEquals(4, actual.size());
    }

}