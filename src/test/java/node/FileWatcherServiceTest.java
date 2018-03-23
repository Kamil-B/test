package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.service.FileWatcherService;
import org.junit.After;
import org.junit.Before;
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

    private FileSystem fs;

    @After
    public void cleanUp() throws IOException {
       if(fs != null) fs.close();
    }

    @Test
    public void addNotExistedPath_returnEmpty() {
        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable = fileWatcher.startWatching(Paths.get("C:\\path\\does\\not\\exist"));

        TestObserver<Event> testObserver = new TestObserver<>();
        observable.subscribe(testObserver);
        testObserver.assertError(IllegalArgumentException.class);
        testObserver.assertNotComplete();
        testObserver.assertNoValues();
    }

    @Test
    public void addingTwoPathsToFileWatcher_returnSameObservable() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.windows());

        Path path1 = Files.createDirectory(fs.getPath("path1"));
        Path path2 = Files.createDirectory(fs.getPath("path2"));

        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable1 = fileWatcher.startWatching(path1);
        Observable<Event> observable2 = fileWatcher.startWatching(path2);
        assertThat(observable1).isSameAs(observable2);
    }

    @Test
    public void addNewPathsToWatchedDirectory_returnCreateEvents() throws IOException, InterruptedException {
        List<Event> expectedEvents = new ArrayList<>();
        List<Event> actualEvents = new ArrayList<>();
        fs = Jimfs.newFileSystem(Configuration.windows());

        FileWatcherService fileWatcher = new FileWatcherService();
        fileWatcher.startWatching(Files.createDirectory(fs.getPath("root"))).subscribe(actualEvents::add);

        expectedEvents.add(new Event(Files.createDirectory(fs.getPath("root/folder1")), EventType.CREATE));
        expectedEvents.add(new Event(Files.createDirectory(fs.getPath("root/folder2")), EventType.CREATE));

        Thread.sleep(7000); // wait for fileWatcher to notice changes

        assertThat(actualEvents).containsAll(expectedEvents);
    }

    @Test
    public void deleteFilesInWatchedDirectory_returnDeleteEvents() throws IOException, InterruptedException {
        List<Event> expectedEvents = new ArrayList<>();
        List<Event> actualEvents = new ArrayList<>();
        fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = Files.createDirectory(fs.getPath("root"));
        Files.createDirectory(fs.getPath("root/folder1"));

        FileWatcherService fileWatcher = new FileWatcherService();
        fileWatcher.startWatching(root).subscribe(actualEvents::add);

        expectedEvents.add(new Event(fs.getPath("root/folder1"), EventType.DELETE));

        Files.delete(fs.getPath("root/folder1"));
        Thread.sleep(7000); // wait for fileWatcher to notice changes

        assertThat(actualEvents).containsAll(expectedEvents);
    }

}