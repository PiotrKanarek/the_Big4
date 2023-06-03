package pl.edu.wit.file;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Katarzyna Nowak
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileCopyingServiceTest {

    private final ArgumentCaptor<List<FileCopyingTask>> taskListCaptor = ArgumentCaptor.forClass(List.class);
    private final ExecutorService mockExecutorService = mock(ExecutorService.class);

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
    @Order(1)
    void copyFilesTest_NoFilesToCOpy() throws IOException, InterruptedException {
        // given
        Files.createFile(tempDir.resolve("notJpgFile.txt"));
        Files.createDirectory(Path.of(tempDir + "/subfolder"));
        Files.createFile(tempDir.resolve(Path.of(tempDir + "/subfolder/notJpgFile.txt")));


        // when
        int actual;
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            actual = service.copyFiles(tempDir.toString(), tempDir + "/subfolder/result");
        }

        // then
        Mockito.verify(mockExecutorService, times(0)).invokeAll(anyCollection());
        assertEquals(0, actual);
    }

    @Test
    @Order(2)
    void copyFilesTest_rightAmountOfTasksCreated() throws IOException, InterruptedException {
        // given
        Files.createFile(tempDir.resolve("jpgFile1.jpg"));
        Files.createFile(tempDir.resolve("jpgFile2.jpg"));
        Files.createFile(tempDir.resolve(Path.of(tempDir + "/subfolder/jpgFile3.jpg")));

        doReturn(new ArrayList<>()).when(mockExecutorService).invokeAll(taskListCaptor.capture());

        // when
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            service.copyFiles(tempDir.toString(), tempDir + "/subfolder/result");
        }

        // then
        assertEquals(3, taskListCaptor.getValue().size());
    }

    @Test
    @Order(3)
    void copyFilesTest_rightNumberReturned() throws IOException, InterruptedException {
        // given
        doReturn(
                List.of(CompletableFuture.completedFuture(true),
                        CompletableFuture.completedFuture(true),
                        CompletableFuture.completedFuture(false)
                )
        ).when(mockExecutorService).invokeAll(any());

        // when
        int actual;
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            actual = service.copyFiles(tempDir.toString(), tempDir + "/subfolder/result");
        }

        // then
        assertEquals(2, actual);
    }

}