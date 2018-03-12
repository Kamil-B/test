package node.service;

import lombok.extern.slf4j.Slf4j;
import node.service.FileWatcherService;
import node.utils.FileWatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FileWatcherServiceImpl implements FileWatcherService {

    private Map<Path, FileWatcher> fileWatchers;

    public FileWatcherServiceImpl() {
        this.fileWatchers = new HashMap<>();
    }

    public FileWatcher registerPath(Path path) throws IOException {
        if (fileWatchers.containsKey(path)) {
            return fileWatchers.get(path);
        }
        FileWatcher fileWatcher = new FileWatcher(path);
        fileWatchers.put(path, fileWatcher);
        return fileWatcher;
    }

    public void update(){
        for(FileWatcher fileWatcher: fileWatchers.values()){
            fileWatcher.update();
        }
    }
}