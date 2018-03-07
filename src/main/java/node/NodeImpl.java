package node;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@ToString
public class NodeImpl<T> implements Node<T> {

    private T payload;
    private Iterable<Node<T>> children;

    public NodeImpl(T payload) {
        this.payload = payload;
        this.children = new ArrayList<>();
    }

    public NodeImpl(T payload, List<Node<T>> children) {
        this.payload = payload;
        this.children = new ArrayList<>(children);
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public Iterable<Node<T>> getChildren() {
        return children;
    }
}
