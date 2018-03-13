package node.service;

import com.sun.nio.file.SensitivityWatchEventModifier;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.utils.NodeHelperUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class FileWatcherServiceImpl implements FileWatcherService {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;
    private List<Event> events = new LinkedList<>();
    private FileWatcher fileWatcher;
    private Observable<Event> observable = Observable.fromIterable(events);

    public FileWatcherServiceImpl() {
        this.watchKeys = new HashMap<>();
    }

    @Override
    // Autoclosable
    public Observable<Event> startWatching(Path path) {
        if (!Files.exists(path)) {
            return Observable.empty();
        }
        if (watchService == null) {
            startFileWatcher(path);
        }
        register(path);
        return observable;
    }

    @Override
    public void update() {
        if (watchKeys.isEmpty()) {
            return;
        }
        WatchKey key = null;
        try {
            key = watchService.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (key == null || !watchKeys.containsKey(key)) {
            return;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                register(path);
                events.add(new Event(path, EventType.CREATE));
            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                events.add(new Event(path, EventType.DELETE));
            }
        }
        key.reset();
    }

    private void register(Path path) {
        if (Files.isDirectory(path)) {
            registerWatcher(path);
            for (Path subDir : NodeHelperUtils.getAllSubDirectories(path)) {
                register(subDir);
            }
        } else {
            log.info("New file found: " + path);
        }
    }

    private void registerWatcher(Path dir) {
        WatchKey key = null;
        try {
            key = dir.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("New folder found: " + dir + ". Added to watcher registry");
        watchKeys.put(key, dir);
    }

    private void startFileWatcher(Path path) {
        try {
            watchService = path.toAbsolutePath().getFileSystem().newWatchService();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            future = executorService.submit(new FileWatcher(events));
        } catch (IOException e) {
            log.error("Couldn't initiate service with " + path + ". " + e.getMessage());
        }

    }
}