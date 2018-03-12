package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import node.utils.FileWatcherService;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FileWatcherTest {

    @Test
    public void when_watchDirectoryCreation_returnTrue() throws IOException, InterruptedException {
        List<Path> expectedPaths = new ArrayList<>();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        expectedPaths.add(Files.createDirectory(root));

        FileWatcherService fileWatcherService = new FileWatcherService();
        fileWatcherService.getWatcher(root);

        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder2")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder2")));
        Thread.sleep(5000);
        List<Path> actualPaths = fileWatcherService.getWatchedPaths(root);
        assertThat(actualPaths).containsAll(expectedPaths);

    }
}
