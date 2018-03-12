package node.service;

import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.slf4j.Slf4j;
import node.utils.NodeHelperUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FileWatcherServiceImpl implements FileWatcherService {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;

    public FileWatcherServiceImpl() {
        this.watchKeys = new HashMap<>();
    }

    @Override
    public boolean watch(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Given Path doesn't exist!!");
        }
        if (watchService == null) {
            initiateWatchService(path);
        }
        register(path);
        return true;
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
        if (key == null) {
            return;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                register(watchKeys.get(key).resolve(fileName));
            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {

            }
        }
        key.reset();
    }

    public Set<Path> getWatchedPaths() {
        return new HashSet<>(watchKeys.values());
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

    private void initiateWatchService(Path path) {
        try {
            watchService = path.toAbsolutePath().getFileSystem().newWatchService();
        } catch (IOException e) {
            log.error("Couldn't initiate service with " + path + ". " + e.getMessage());
        }
    }
}