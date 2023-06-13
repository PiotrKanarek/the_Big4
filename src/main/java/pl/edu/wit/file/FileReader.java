package pl.edu.wit.file;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * utility class for reading files
 *
 * @author Katarzyna Nowak
 */
public class FileReader {

    private static final Logger log = LogManager.getLogger(FileReader.class.getName());

    private FileReader() { }

    /**
     * this method will return a List of relevant '.jpg' files found in the provided directory (or its subdirectories)
     *
     * @param sourceDirectory path to the directory that is to be examined
     * @return list of all {@link java.io.File} with '.jpg' extension found (or an empty list if no relevant files were found)
     */
    public static List<File> getFilesToBeCopied(String sourceDirectory) {
        List<File> jpgFiles = getAllFiles(sourceDirectory)
                .stream()
                .filter(file -> file.getName().toLowerCase().endsWith(".jpg"))
                .collect(Collectors.toList());

        log.info("Found " + jpgFiles.size() + " '.jpg' files to be copied");
        return jpgFiles;
    }

    /**
     * this method will list all files available in the provided directory, along with its subdirectories and their contents
     *
     * @param pathName path to the directory that is to be examined
     * @return list of all {@link java.io.File} found in that directory, including subdirectories
     * (or an empty list if no files were found)
     */
    private static List<File> getAllFiles(String pathName) {
        if (pathName == null) {
            log.error("Provided path was 'null', unable to read files");
            return Collections.emptyList();
        }

        log.info("Reading files from: '" + pathName + "'");

        File directory = new File(pathName);

        File[] fileList = directory.listFiles();

        List<File> resultList;
        if (fileList != null) {
            resultList = new ArrayList<>(Arrays.asList(fileList));
            for (File file : fileList) {
                if (file.isDirectory()) {
                    resultList.addAll(getAllFiles(file.getAbsolutePath()));
                }
            }

            return resultList;

        } else {
            return Collections.emptyList();
        }
    }
}
