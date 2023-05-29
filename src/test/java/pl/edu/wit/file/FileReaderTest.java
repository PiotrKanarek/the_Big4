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
        Files.createFile(tempDir.resolve("jpgFile.jpg"));
        Files.createFile(tempDir.resolve("csvFile.csv"));
        Files.createFile(tempDir.resolve("txtFile.txt"));
        Files.createDirectory(Path.of(tempDir + "/subfolder"));
        Files.createFile(tempDir.resolve(Path.of(tempDir + "/subfolder/jpgFile2.jpg")));

        // when
        List<File> actual = FileReader.getFilesToBeCopied(tempDir.toString());

        // then
        assertEquals(2, actual.size());
    }

    @Test
    void readAllFilesNullPathTest() {
        // when
        List<File> actual = FileReader.getFilesToBeCopied(null);

        // then
        assertEquals(0, actual.size());
    }

}