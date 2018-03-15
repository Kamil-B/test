package node.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import node.model.EventType;

@Data
@AllArgsConstructor
public class EventMessage {
    private String path;
    private EventType eventType;
}
