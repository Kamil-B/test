package node.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PathUtils {

    public static Boolean createFile(Path path) {
        try {
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            log.error("Could not create file: " + path, e);
            return false;
        }
    }

    public static boolean deleteFile(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            log.error("Could not delete file: " + path, e);
            return false;
        }
    }

    public static List<Path> getSubdirectories(Path path) {
        if (Files.isDirectory(path)) {
            try {
                return Files.list(path).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
