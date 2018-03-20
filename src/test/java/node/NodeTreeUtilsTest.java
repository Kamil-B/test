package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.model.Node;
import node.model.NodeImpl;
import node.utils.NodeTree;
import node.utils.NodeUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NodeTreeUtilsTest {

    @Test
    public void generatePathsTree_sameAsExpected() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        Files.createDirectory(fs.getPath("root"));
        Files.createDirectory(fs.getPath("root/folder1"));
        Files.createFile(fs.getPath("root/folder1/file1.txt"));
        Files.createDirectory(fs.getPath("root/folder2"));
        Files.createDirectory(fs.getPath("root/folder1/subfolder1"));
        Files.createDirectory(fs.getPath("root/folder1/subfolder2"));

        val subfolder1 = new NodeImpl<Path>(fs.getPath("root/folder1/subfolder1"));
        val subfolder2 = new NodeImpl<Path>(fs.getPath("root/folder1/subfolder2"));
        val file1 = new NodeImpl<Path>(fs.getPath("root/folder1/file1.txt"));
        val folder1 = new NodeImpl<Path>(fs.getPath("root/folder1"), Arrays.asList(file1, subfolder1, subfolder2));
        val folder2 = new NodeImpl<Path>(fs.getPath("root/folder2"));
        val expectedNodeTree = new NodeImpl<Path>(fs.getPath("root"), Arrays.asList(folder1, folder2));

        Node<Path> actualNodeTree = NodeUtils.createNodeTree(root);
        new NodeTree<>(actualNodeTree).iterator().forEachRemaining(node -> log.info(node.getPayload().toString()));
        assertThat(actualNodeTree).isEqualTo(expectedNodeTree);
    }
}
