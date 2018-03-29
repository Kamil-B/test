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
            if (!Files.exists(path.getParent().toAbsolutePath())) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            log.error("Could not create file: " + path, e.getMessage());
            return false;
        }
    }

    public static boolean deleteFile(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            log.error("Could not delete file: " + path, e.getMessage());
            return false;
        }
    }

    public static List<Path> getSubdirectories(Path path) {
        if (!Files.isDirectory(path)) {
            return new ArrayList<>();
        }
        try {
            return Files.list(path).collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Couldn't get subdirectories for file: " + path, e.getMessage());
        }
        return new ArrayList<>();
    }
}