package node.model;

import lombok.Data;

@Data
public class SubscriptionMessage {
    private String path;
    private String user;
}
