package node.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FileWatcherService {

    private FileWatcher fileWatcher;
    private ExecutorService executorService;
    private Future future;

    public FileWatcherService() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void watchDirectory(Path path) {
        try {
            fileWatcher = new FileWatcher(path);
            future = executorService.submit(fileWatcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
