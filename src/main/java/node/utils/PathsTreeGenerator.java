package node.utils;

import node.Node;
import node.NodeImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathsTreeGenerator {

    public static Node<Path> createPathTree(Path path) {
        return new NodeImpl<>(path, convertFilesToNodes(getChildren(path)));
    }

    private static List<Node<Path>> convertFilesToNodes(List<Path> names) {
        return names.stream().map(name -> new NodeImpl<>(name, convertFilesToNodes(getChildren(name))))
                .collect(Collectors.toList());
    }

    private static List<Path> getChildren(Path path) {
        if (!Files.isDirectory(path)) {
            return new ArrayList<>();
        }
        return getAllSubDirectories(path);
    }

    private static List<Path> getAllSubDirectories(Path path) {
        try {
            return Files.list(path).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}