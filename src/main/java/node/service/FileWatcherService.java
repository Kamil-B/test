package node.service;

import node.utils.FileWatcher;

import java.io.IOException;
import java.nio.file.Path;

public interface FileWatcherService {

    void update();

    FileWatcher registerPath(Path path) throws IOException;
}
