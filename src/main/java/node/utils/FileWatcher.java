package node.utils;

import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Slf4j
public class FileWatcher implements Runnable {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;
    private Queue<Event> events;

    public FileWatcher(Queue<Event> events, WatchService watchService, Path path) {
        this.events = events;
        this.watchService = watchService;
        this.watchKeys = new HashMap<>();
        addToWatched(path);
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!watchKeys.isEmpty()) {
                    WatchKey key = watchService.take();
                    if (key == null) return;
                    update(key);
                }
            }
        } catch (InterruptedException e) {
            log.warn("File Watcher has been interrupted");
        }
    }

    public void addToWatched(Path path) {
        if (Files.isDirectory(path)) {
            registerToWatcher(path);
            for (Path subDir : NodeHelperUtils.getAllSubDirectories(path)) {
                addToWatched(subDir);
            }
        } else {
            log.info("New file found: " + path);
        }
    }

    private void update(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                addEvent(path, EventType.CREATE);
                addToWatched(path);

            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                addEvent(path, EventType.DELETE);
                removeFromWatched(key);
            }
        }
        key.reset();
    }

    private void removeFromWatched(WatchKey key) {
        watchKeys.remove(key);
    }

    private void addEvent(Path path, EventType type) {
        log.info("Event occurred: " + type + " " + path);
        events.add(new Event(path, type));
    }

    private void registerToWatcher(Path dir) {
        try {
            WatchKey key = dir.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
            watchKeys.put(key, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("New folder found: " + dir + ". Added to watcher registry");

    }

}