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
    private Path root;

    public FileWatcher(Path path) throws IOException {
        this.root = path.toAbsolutePath();
        this.watchService = createWatcher(root);
        this.watchKeys = new HashMap<>();
        registerToWatcher(root);
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
                    log.info("folder created " + path);
                    registerToWatcher(path);
                }
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    log.info("folder deleted " + path);
                }
            }
            key.reset();
        }
    }

    private void registerToWatcher(Path dir) {
        try {
            dir.toAbsolutePath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            for (Path subDir : getSubDirectories(dir)) {
                registerToWatcher(subDir);
                subDir.toAbsolutePath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
                log.info("registered path: " + subDir);
                //watchKeys.put(dir, watchKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Path> getSubDirectories(Path path) throws IOException {
        return Files.list(path).filter(Files::isDirectory).collect(Collectors.toList());
    }

    private WatchService createWatcher(Path path) throws IOException {
        return path.toAbsolutePath().getFileSystem().newWatchService();
    }
}