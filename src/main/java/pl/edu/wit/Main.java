package pl.edu.wit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.wit.file.FileCopyingService;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

        log.info("Application started");

        // TODO: front-end + walidacja
        String sourceDirectory = "tu dajcie ścieżkę do folderu ze zdjęciami do przekopiowania";
        String destinationDirectory = "a tu ścieżkę do folderu do którego mają być przekopiowane pliki";
        // albo przekażcie je bezpośrednio do metody poniżej, to tylko wstępny setup

        int filesCopied = 0;

        try (FileCopyingService copyingService = new FileCopyingService()) {
            filesCopied = copyingService.copyFiles(sourceDirectory, destinationDirectory);
        }

        log.info("Processing finished - copied " + filesCopied + " files");
    }

}