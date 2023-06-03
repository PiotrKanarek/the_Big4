package pl.edu.wit.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
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

    @Test
    void copyFilesTest_noFilesToCopy(@TempDir Path tempDir) throws IOException, InterruptedException {
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
    void copyFilesTest_rightAmountOfTasksCreated(@TempDir Path tempDir) throws IOException, InterruptedException {
        // given
        populateTempDirWithJsonFiles(tempDir);
        doReturn(new ArrayList<>()).when(mockExecutorService).invokeAll(taskListCaptor.capture());

        // when
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            service.copyFiles(tempDir.toString(), tempDir + "/subfolder/result");
        }

        // then
        assertEquals(3, taskListCaptor.getValue().size());
    }

    @Test
    void copyFilesTest_rightNumberReturned(@TempDir Path tempDir) throws InterruptedException, IOException {
        // given
        populateTempDirWithJsonFiles(tempDir);
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

    @Test
    void copyFilesTest_processingException(@TempDir Path tempDir) throws InterruptedException, IOException {
        // given
        populateTempDirWithJsonFiles(tempDir);
        Mockito.doThrow(InterruptedException.class).when(mockExecutorService).invokeAll(any());

        // when
        int actual;
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            actual = service.copyFiles(tempDir.toString(), tempDir + "/subfolder/result");
        }

        // then
        assertEquals(0, actual);
    }

    @Test
    void copyFilesTest_invalidDirectories() throws InterruptedException {
        // given
        String sourceDirectory = "foo";
        String destinationDirectory = "bar";

        // when
        int actual;
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            actual = service.copyFiles(sourceDirectory, destinationDirectory);
        }

        // then
        Mockito.verify(mockExecutorService, times(0)).invokeAll(anyCollection());
        assertEquals(0, actual);
    }

    @Test
    void copyFilesTest_nullDirectories() throws InterruptedException {
        // when
        int actual;
        try (FileCopyingService service = new FileCopyingService(mockExecutorService)) {
            actual = service.copyFiles(null, null);
        }

        // then
        Mockito.verify(mockExecutorService, times(0)).invokeAll(anyCollection());
        assertEquals(0, actual);
    }

    @Test
    void copyFilesTest_nullExecutorService() {
        Assertions.assertThrows(RuntimeException.class, () -> new FileCopyingService(null));
    }

    private void populateTempDirWithJsonFiles(Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("jpgFile1.jpg"));
        Files.createFile(tempDir.resolve("jpgFile2.jpg"));
        Files.createDirectory(Path.of(tempDir + "/subfolder"));
        Files.createFile(tempDir.resolve(Path.of(tempDir + "/subfolder/jpgFile3.jpg")));
    }

}