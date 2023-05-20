package pl.edu.wit.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * utility class for reading files
 * </p>
 *
 * @author Katarzyna Nowak
 */
public class FileReader {

    /**
     * this method will list all files available in the provided directory and its subdirectories
     *
     * @param pathName path to the directory that is to be examined
     * @return list of all {@link java.io.File} found in that directory, including subdirectories
     */
    public static List<File> getAllFiles(String pathName) {
        System.out.println("Reading files from " + pathName);

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
