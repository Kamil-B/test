package node.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.nio.file.Path;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Event {
    private Path path;
    private EventType event;
}
