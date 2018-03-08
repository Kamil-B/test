package node;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.utils.PathsTreeGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PathsTreeGeneratorTest {

    @Test
    public void generateTree() {
        val file1 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder1\\subfolder1\\file1"));
        val file2 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder1\\subfolder1\\file2"));
        val subfolder1 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder1\\subfolder1"), Arrays.asList(file1, file2));
        val subfolder2 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder2\\subfolder2"));
        val folder1 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder1"), Arrays.asList(subfolder1));
        val folder2 = new NodeImpl<Path>(Paths.get("src\\test\\resources\\test_folder\\folder2"), Arrays.asList(subfolder2));
        val expectedNodeTree = new NodeImpl<>(Paths.get("src\\test\\resources\\test_folder"), Arrays.asList(folder1, folder2));

        Node<Path> actualNodeTree = PathsTreeGenerator.createPathTree(Paths.get("src\\test\\resources\\test_folder"));
        assertThat(actualNodeTree).isEqualTo(expectedNodeTree);
    }

/*    @Test
    public void watchFileTest() throws IOException {
        FileWatcher fileWatcher = new FileWatcher(Paths.get("src/test/resources/test_folder"));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future future = executor.submit(fileWatcher);
        future.cancel(true);

    }*/
}
