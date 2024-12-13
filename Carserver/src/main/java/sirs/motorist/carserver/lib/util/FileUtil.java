package sirs.motorist.carserver.lib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * Reads the content of a file as String
     * @param filePath Path to the file
     * @return Content of the file in String
     */
    public static String readContent(String filePath){
        String content = null;
        try{
            Path path = Paths.get(filePath);
            content = Files.readString(path);
            return content;
        } catch(IOException e){
            logger.error("An error occurred while reading the file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Reads the content of a file as bytes
     * @param filePath Path to the file
     * @return Content of the file in bytes
     */
    public static byte[] readBytes(String filePath){
        byte[] content = null;
        try {
            Path path = Paths.get(filePath);
            content = Files.readAllBytes(path);
            return content;
        } catch(IOException e){
            logger.error("An error occurred while reading the file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Writes content to a file
     * @param filePath Path to the file
     * @param content Content to be written
     */
    public static void writeBytes(String filePath, byte[] content){
        try {
            Path path = Paths.get(filePath);
            Files.write(path, content);
        } catch (IOException e) {
            logger.error("Unable to write to file: {}", e.getMessage());
        }
    }

    public static void writeAsString(String filePath, String content){
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            logger.error("Error while writing to file {}, Error: {}", filePath, e.getMessage());
        }
    }
}
