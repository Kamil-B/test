package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.service.FileWatcherServiceImpl;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileWatcherServiceTest {

    @Test
    public void watchDirectoryCreation_SameAsExpected() throws IOException {
        List<Path> expectedPaths = new ArrayList<>();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        expectedPaths.add(Files.createDirectory(root));

        FileWatcherServiceImpl fileWatcher = new FileWatcherServiceImpl();
        fileWatcher.startWatching(root);

        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder2")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder1")));
        expectedPaths.add(Files.createDirectory(fs.getPath("root/folder1/subfolder2")));

        fileWatcher.update();

/*        Set<Path> actualPaths = fileWatcher.getWatchedPaths();
        assertThat(actualPaths).containsAll(expectedPaths);*/
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNotExistingPath_IllegalArgumentExceptionThrown() {
        FileWatcherServiceImpl fileWatcher = new FileWatcherServiceImpl();
        fileWatcher.startWatching(Paths.get("C:\\unreal\\path"));
    }

    @Test
    public void test() throws IOException, InterruptedException {

        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = fs.getPath("root");
        Files.createDirectory(fs.getPath("root"));
        List<Event> expectedEvents = new ArrayList<>();

        FileWatcherServiceImpl fileWatcher = new FileWatcherServiceImpl();
        fileWatcher.startWatching(root);

        expectedEvents.add(new Event(Files.createDirectory(fs.getPath("root/folder1")), EventType.CREATE));
        Thread.sleep(5000);

        Observable<Event> observable = fileWatcher.update();
        ReplaySubject<Event> subscriber = ReplaySubject.create();
        observable.subscribe(subscriber);

        Iterable<Event> items = subscriber.blockingIterable();
        items.forEach(item -> log.info(item.toString()));
    }
}
