package node.utils;

import lombok.extern.slf4j.Slf4j;
import node.model.Node;
import node.model.NodeImpl;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NodePathUtils {

    public static Stream<Path> getNodeTreePaths(Path path) {
        return new NodeTree<>(createNodeTree(path)).asStream().map(Node::getPayload);
    }

    public static Node<Path> createNodeTree(Path path) {
        return new NodeImpl<>(path, convertFilesToNodes(PathUtils.getSubdirectories(path)));
    }

    private static List<Node<Path>> convertFilesToNodes(List<Path> names) {
        return names.stream().map(name -> new NodeImpl<>(name, convertFilesToNodes(PathUtils.getSubdirectories(name))))
                .collect(Collectors.toList());
    }
}