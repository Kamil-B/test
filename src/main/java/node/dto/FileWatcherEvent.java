package node.dto;

import lombok.Data;
import lombok.ToString;

import java.nio.file.Path;

@Data
@ToString
public class FileWatcherEvent {
    private FileWatcherEventType fileWatcherEventType;
    private Path path;

    public FileWatcherEvent(Path path) {
        this.path = path;
    }
}
