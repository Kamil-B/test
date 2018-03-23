package node.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PathActionResult {

    String path;
    String action;
    boolean result;
    String reason;
}
