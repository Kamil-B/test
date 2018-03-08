package node.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
public class FileWatcher implements Runnable {
    private WatchService watchService;
    private Path dir;
    private volatile boolean shutdown;

    public FileWatcher(Path path) throws IOException {
        this.dir = path;
        this.watchService = createWatcher();
        registerWatcher();
    }

    @Override
    public void run() {
        while (!shutdown) {
            WatchKey keys = null;
            try {
                keys = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                keys.pollEvents().forEach(event -> log.info(event.kind() + " " + event.context()));
                keys.reset();
        }
    }

    public void stop() {
        shutdown = true;
    }

    private void registerWatcher() throws IOException {
        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
    }

    private WatchService createWatcher() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }
}
