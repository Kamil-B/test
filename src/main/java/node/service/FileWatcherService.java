package node.service;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.utils.FileWatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;

@Slf4j
@Service
public class FileWatcherService {

    private FileWatcher fileWatcher;
    private PublishSubject<Event> publisher;

    public FileWatcherService() {
        this.publisher = PublishSubject.create();
    }

    // Autoclosable
    public Observable<Event> startWatching(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            log.info("Path: " + path + " does not exist or is not a directory!! ");
            return Observable.error(IllegalArgumentException::new);
        }
        addToFileWatcher(path);
        return publisher;
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
            fileWatcher = new FileWatcher(watchService, path, publisher);
            new Thread(fileWatcher).start();
        } catch (IOException e) {
            log.error("Couldn't initiate service with " + path + ". " + e.getMessage());
        }
    }
}