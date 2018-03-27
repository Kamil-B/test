package node.utils;

import lombok.extern.slf4j.Slf4j;
import node.model.Node;
import node.model.NodeImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NodeUtils {

    public static Node<Path> createNodeTree(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("file does not exist");
        }
        return new NodeImpl<>(path, convertFilesToNodes(PathUtils.getSubdirectories(path)));
    }

    private static List<Node<Path>> convertFilesToNodes(List<Path> names) {
        return names.stream().map(name -> new NodeImpl<>(name, convertFilesToNodes(PathUtils.getSubdirectories(name))))
                .collect(Collectors.toList());
    }
}