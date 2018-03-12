package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import node.utils.FileWatcher;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Slf4j
public class FileWatcherTest {

    @Test
    public void when_watchDirectoryCreation_returnTrue() throws IOException {
        List<Path> expectedPaths = new ArrayList<>();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        expectedPaths.add(Files.createDirectory(root));

        FileWatcher fileWatcher = new FileWatcher(root);

        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1")));
        fileWatcher.update();
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder2")));
        fileWatcher.update();
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder1")));
        fileWatcher.update();
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder2")));
        fileWatcher.update();
        Queue queue = fileWatcher.getEvent();
        while (queue.peek() != null) {
            log.info(queue.poll().toString());
        }

        //List<Path> actualPaths = fileWatcherService.getWatchedPaths(root);
        //assertThat(actualPaths).containsAll(expectedPaths);

    }
}
