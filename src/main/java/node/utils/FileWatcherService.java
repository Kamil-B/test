package node.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
public class FileWatcherService {

    private ExecutorService executorService;
    private Future future;
    private Map<Path, FileWatcher> fileWatchers;

    public FileWatcherService() {
        this.executorService = Executors.newFixedThreadPool(10);
        this.fileWatchers = new HashMap<>();
    }

    public FileWatcher getWatcher(Path path) throws IOException {
        if(fileWatchers.containsKey(path)){
            return fileWatchers.get(path);
        }
        FileWatcher fileWatcher = new FileWatcher(path);
        future = executorService.submit(fileWatcher);
        fileWatchers.put(path, fileWatcher);
        return fileWatcher;
    }

    public List<Path> getWatchedPaths(Path root) {
        return fileWatchers.get(root).getWatchedPaths();
    }
}