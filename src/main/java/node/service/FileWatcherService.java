package node.service;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.utils.FileWatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class FileWatcherService {

    private Queue<Event> events;
    private Future future;
    private FileWatcher fileWatcher;
    private Observable<Event> observable;

    public FileWatcherService() {
        this.events = new ConcurrentLinkedQueue<>();
        this.observable = Observable.fromIterable(events).doAfterNext(event ->
                {
                    log.info("Event occurred: " + event.toString());
                    events.remove(event);
                }
        );
    }

    // Autoclosable
    public Observable<Event> startWatching(Path path) {
        if (!Files.exists(path)) {
            log.info("Path: " + path + " does not exist!!");
            return Observable.empty();
        }
        addToFileWatcher(path);
        return observable;
    }

    private void addToFileWatcher(Path path) {
        if (fileWatcher == null) {
            startFileWatcher(path);
        } else {
            fileWatcher.addToWatched(path);
        }
    }

    private void startFileWatcher(Path path) {
        try {
            WatchService watchService = path.toAbsolutePath().getFileSystem().newWatchService();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            fileWatcher = new FileWatcher(events, watchService, path);
            future = executorService.submit(fileWatcher);
        } catch (IOException e) {
            log.error("Couldn't initiate service with " + path + ". " + e.getMessage());
        }
    }
}