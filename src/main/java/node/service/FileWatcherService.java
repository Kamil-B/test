package node.service;

import io.reactivex.Observable;
import node.model.Event;

import java.nio.file.Path;

public interface FileWatcherService {

    void update();

    Observable<Event> startWatching(Path path);

}
