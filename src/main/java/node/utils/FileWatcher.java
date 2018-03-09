package node.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FileWatcher implements Runnable {
    private WatchService watchService;
    private Map<Path, WatchKey> watchKeys;

    public FileWatcher(Path path) throws IOException {
        this.watchService = createWatcher(path);
        this.watchKeys = new HashMap<>();
        registerWatcher(path);
    }

    @Override
    public void run() {
        WatchKey key = null;
        while (true) {
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                Path name = ((WatchEvent<Path>) event).context();
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    log.info("folder created" + name.toAbsolutePath());
                    registerWatcher(name.toAbsolutePath());
                }

                if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    log.info("folder deleted" + name.toAbsolutePath());
                }
            }
            key.reset();
        }
    }

    private void registerWatcher(Path path) {
        try {
           // Files.list(path).map(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE))
            WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            watchKeys.put(path, watchKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WatchService createWatcher(Path path) throws IOException {
        return path.getFileSystem().newWatchService();
    }
}