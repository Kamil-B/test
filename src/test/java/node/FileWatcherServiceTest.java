package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.service.FileWatcherService;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FileWatcherServiceTest {

    @Test
    public void addNotExistedPath_returnEmpty() {
        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable = fileWatcher.startWatching(Paths.get("C:\\path\\does\\not\\exist"));

        Iterable<Event> items = observable.blockingIterable();
        assertThat(items.iterator()).isEmpty();
    }

    @Test
    public void addNewPathsToWatchedDirectory_returnNewEvents() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());

        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable = fileWatcher.startWatching(Files.createDirectory(fs.getPath("root")));

        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(new Event(Files.createDirectory(fs.getPath("root/folder1")), EventType.CREATE));
        expectedEvents.add(new Event(Files.createDirectory(fs.getPath("root/folder2")), EventType.CREATE));

        Thread.sleep(5000); // wait for fileWatcher to notice changes

        Iterable<Event> items = observable.blockingIterable();
        assertThat(items.iterator()).containsAll(expectedEvents);
    }

    @Test
    public void deleteFilesInWatchedDirectory_returnNewEvents() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = Files.createDirectory(fs.getPath("root"));
        Files.createDirectory(fs.getPath("root/folder1"));
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(new Event(fs.getPath("root/folder1"), EventType.DELETE));

        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable = fileWatcher.startWatching(root);

        Files.delete(fs.getPath("root/folder1"));
        Thread.sleep(5000); // wait for fileWatcher to notice changes

        Iterable<Event> items = observable.blockingIterable();
        assertThat(items.iterator()).containsAll(expectedEvents);
    }
}
