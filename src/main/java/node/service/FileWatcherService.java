package node.service;

import java.nio.file.Path;

public interface FileWatcherService {

    void update();

    boolean watch(Path path);
}
