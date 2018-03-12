package node.utils;

import lombok.extern.slf4j.Slf4j;
import node.dto.FileWatcherEvent;
import node.dto.FileWatcherEventType;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Slf4j
public class FileWatcher {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;
    private Queue<FileWatcherEvent> events;


    public FileWatcher(Path path) throws IOException {
        this.watchService = createWatcher(path);
        this.events = new LinkedList<>();
        this.watchKeys = new HashMap<>();
        register(path);
    }

    public void update() {
        WatchKey key = null;
        try {
            key = watchService.take();
            log.info("key taken");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
                Path path = watchKeys.get(key).resolve(fileName);
                FileWatcherEvent fileWatcherEvent = new FileWatcherEvent(path);

                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    register(watchKeys.get(key).resolve(fileName));
                    addToEvents(fileWatcherEvent, FileWatcherEventType.CREATE);
                }
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    addToEvents(fileWatcherEvent, FileWatcherEventType.DELETE);

                }
            }
            key.reset();
        }

    }

    public Queue getEvent() {
        return events;
    }

    private void addToEvents(FileWatcherEvent fileWatcherEvent, FileWatcherEventType create) {
        fileWatcherEvent.setFileWatcherEventType(create);
        log.info("Event occurred: " + fileWatcherEvent);
        events.add(fileWatcherEvent);
    }

/*    private void addToNodeTree(Path path) {
        log.info("adding path to tree: " + path);
        while (node.iterator().hasNext()) {
            Node<Path> node = root.iterator().next();
            if (node.getPayload().equals(path.getParent())) {
                node.addChild(PathTreeUtils.createPathTree(path));
                return;
            }
        }
    }*/

    private void register(Path path) {
        if (Files.isDirectory(path)) {
            registerWatcher(path);
            for (Path subDir : PathTreeUtils.getAllSubDirectories(path)) {
                register(subDir);
            }
        }
    }

    private void registerWatcher(Path dir) {
        WatchKey key = null;
        try {
            key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("path registered to watcher: " + dir);
        watchKeys.put(key, dir);

    }

    private WatchService createWatcher(Path path) throws IOException {
        return path.toAbsolutePath().getFileSystem().newWatchService();
    }
}