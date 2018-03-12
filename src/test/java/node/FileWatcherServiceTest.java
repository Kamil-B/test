package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import node.service.FileWatcherServiceImpl;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FileWatcherServiceTest {

    @Test
    public void when_watchDirectoryCreation_returnTrue() throws IOException {
        List<Path> expectedPaths = new ArrayList<>();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        expectedPaths.add(Files.createDirectory(root));

        FileWatcherServiceImpl fileWatcher = new FileWatcherServiceImpl();
        fileWatcher.watch(root);

        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder2")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder2")));

        fileWatcher.update();

        Set<Path> actualPaths = fileWatcher.getWatchedPaths();
        assertThat(actualPaths).containsAll(expectedPaths);

    }

    @Test(expected = IllegalArgumentException.class)
    public void when_addNotExistedPath_returnIllegalArgumentException(){
        FileWatcherServiceImpl fileWatcher = new FileWatcherServiceImpl();
        fileWatcher.watch(Paths.get("C:\\unreal\\path"));
    }
}
