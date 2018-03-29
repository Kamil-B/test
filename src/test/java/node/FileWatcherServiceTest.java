package node;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import node.model.Event;
import node.model.EventType;
import node.service.FileWatcherService;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FileWatcherServiceTest {

    @Test
    public void addNewPathsToWatchedDirectory_returnCreateEvents() throws IOException {
        List<Event> expected = new ArrayList<>();
        @Cleanup val fs = Jimfs.newFileSystem(Configuration.windows());

        val catcher = ReplaySubject.<Event>create();

        FileWatcherService fileWatcher = new FileWatcherService();
        fileWatcher.startWatching(Files.createDirectory(fs.getPath("root"))).subscribe(catcher);

        expected.add(new Event(Files.createDirectory(fs.getPath("root/folder1")), EventType.CREATE));
        expected.add(new Event(Files.createDirectory(fs.getPath("root/folder2")), EventType.CREATE));
        expected.add(new Event(Files.createDirectory(fs.getPath("root/folder1/subfolder")), EventType.CREATE));
        val actual = StreamSupport.stream(catcher.blockingIterable().spliterator(), false).limit(5).collect(Collectors.toList());

        assertThat(actual).containsAll(expected);
    }

    @Test
    public void deleteFilesInWatchedDirectory_returnDeleteEvents() throws IOException {
        List<Event> expected = new ArrayList<>();
        @Cleanup val fs = Jimfs.newFileSystem(Configuration.windows());
        Path root = Files.createDirectory(fs.getPath("root"));
        Files.createDirectory(fs.getPath("root/folder1"));

        FileWatcherService fileWatcher = new FileWatcherService();
        val catcher = ReplaySubject.<Event>create();
        fileWatcher.startWatching(root).subscribe(catcher);

        expected.add(new Event(fs.getPath("root/folder1"), EventType.DELETE));

        Files.delete(fs.getPath("root/folder1"));
        val actual = StreamSupport.stream(catcher.blockingIterable().spliterator(), false).limit(1).collect(Collectors.toList());

        assertThat(actual).containsAll(expected);
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
        @Cleanup val fs = Jimfs.newFileSystem(Configuration.windows());

        Path path1 = Files.createDirectory(fs.getPath("path1"));
        Path path2 = Files.createDirectory(fs.getPath("path2"));

        FileWatcherService fileWatcher = new FileWatcherService();
        Observable<Event> observable1 = fileWatcher.startWatching(path1);
        Observable<Event> observable2 = fileWatcher.startWatching(path2);
        assertThat(observable1).isSameAs(observable2);
    }
}