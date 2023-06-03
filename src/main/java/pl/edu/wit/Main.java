package pl.edu.wit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.wit.config.PropertySource;
import pl.edu.wit.config.ExecutorConfigurator;
import pl.edu.wit.file.FileCopyingService;

import java.util.concurrent.ExecutorService;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

        log.info("Application started");

        // TODO: front-end + walidacja
        String sourceDirectory = "tu dajcie ścieżkę do folderu ze zdjęciami do przekopiowania";
        String destinationDirectory = "a tu ścieżkę do folderu do którego mają być przekopiowane pliki";
        // albo przekażcie je bezpośrednio do metody poniżej, to tylko wstępny setup

        PropertySource propertySource = new PropertySource("src/main/resources/application.properties");
        ExecutorService executorService = ExecutorConfigurator.getConfiguredExecutor(propertySource);

        int filesCopied = 0;
        try (FileCopyingService copyingService = new FileCopyingService(executorService)) {
            filesCopied = copyingService.copyFiles(sourceDirectory, destinationDirectory);
        } catch (RuntimeException e) {
            log.error("Exception occurred while copying the files: " + e.getMessage());
        }

        log.info("Processing finished - copied " + filesCopied + " files");
    }

}