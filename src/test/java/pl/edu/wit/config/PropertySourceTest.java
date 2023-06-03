package pl.edu.wit.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Katarzyna Nowak
 */
class PropertySourceTest {

    private static Path tempDir;

    @BeforeAll
    public static void before() throws IOException {
        tempDir = Files.createTempDirectory(null);
    }

    @AfterAll
    public static void after() {
        tempDir.toFile().delete();
    }

    @Test
    void getPropertyTest_validSource() throws IOException {
        // given
        File propertyFile = new File(tempDir.toFile(), "application.properties");
        Files.write(propertyFile.toPath(), "pool-size=5".getBytes());

        PropertySource propertySource = new PropertySource(tempDir + "/application.properties");

        // when
        String actual = propertySource.getProperty("pool-size");

        // then
        assertEquals("5", actual);
    }

    @Test
    void getPropertyTest_invalidSource() {
        // given
        PropertySource propertySource = new PropertySource("");

        // when
        String actual = propertySource.getProperty("");

        // then
        assertNull(actual);
    }
}