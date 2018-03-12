package node.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FileWatcher implements Runnable {
    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;

    public FileWatcher(Path path) throws IOException {
        this.watchService = createWatcher(path);
        this.watchKeys = new HashMap<>();
        register(path);
    }

    @Override
    public void run() {
        WatchKey key = null;
        while (true) {
            try {
                key = watchService.take();
                log.info("key taken");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                Path path = ((WatchEvent<Path>) event).context();
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    log.info("file created " + path);
                    register(watchKeys.get(key).resolve(path.getFileName()));
                }
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    log.info("file deleted " + path);
                }
            }
            key.reset();
        }
    }

    public List<Path> getWatchedPaths() {
        return new ArrayList<>(watchKeys.values());
    }

    private void register(Path path) {
        if (Files.isDirectory(path)) {
            registerWatcher(path);
            for (Path subDir : getSubDirectories(path)) {
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

    private List<Path> getSubDirectories(Path path) {
        try {
            return Files.list(path).filter(Files::isDirectory).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private WatchService createWatcher(Path path) throws IOException {
        return path.toAbsolutePath().getFileSystem().newWatchService();
    }
}