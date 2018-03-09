package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.utils.FileWatcherService;
import node.utils.NodeHelper;
import node.utils.PathsTreeGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileWatcherTest {

    @Test
    public void watchFileTest() throws IOException {

        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path path = fs.getPath("root");
        Files.createDirectory(path);

        FileWatcherService fileWatcherService = new FileWatcherService();
        fileWatcherService.watchDirectory(path);

        Files.createDirectories(fs.getPath("root/folder1"));
        Files.createDirectories(fs.getPath("root/folder2"));

        Files.createDirectory(fs.getPath("root/folder1/subfolder1"));
        Files.createDirectory(fs.getPath("root/folder1/subfolder2"));

        Files.createFile(fs.getPath("root/folder1/subfolder1/file1.txt"));
        Files.createFile(fs.getPath("root/folder1/subfolder1/file2.txt"));

        Node<Path> root = PathsTreeGenerator.createPathTree(path);
        val helper = new NodeHelper<Path>(root);
        helper.iterator().forEachRemaining(node -> log.info(node.getPayload().toString()));
    }

    @Test
    public void tests() throws IOException, InterruptedException {
        Files.deleteIfExists(Paths.get("C:\\git\\tree_test\\test5"));
        FileWatcherService fileWatcherService = new FileWatcherService();
        fileWatcherService.watchDirectory(Paths.get("C:\\git\\tree_test"));
        Files.createDirectory(Paths.get("C:\\git\\tree_test\\test5"));
        Files.createDirectory(Paths.get("C:\\git\\tree_test\\test5\\testowa"));
        Thread.sleep(1000000);
    }

}
