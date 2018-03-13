package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.model.Node;
import node.model.NodeImpl;
import node.utils.NodeTree;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ObservableTest {

    @Test
    public void subscribeToNodeTree_getAllElements_returnTrue() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
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
        NodeTree<Path> nodeTree = new NodeTree<>(new NodeImpl<>(fs.getPath("root"), Arrays.asList(folder1, folder2)));

        List<Node<Path>> actualNodes = new ArrayList<>();
        Observable<Node<Path>> observable = Observable.fromIterable(nodeTree);
        observable.subscribe(actualNodes::add);
        assertThat(actualNodes).containsAll(Arrays.asList(subfolder1, subfolder2, file1, folder1, folder2));
    }


}
