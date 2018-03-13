package node.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import node.model.Node;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class NodeImpl<T> implements Node<T> {

    private T payload;
    private List<Node<T>> children;

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
    public List<Node<T>> getChildren() {
        return children;
    }

    @Override
    public boolean addChild(Node<T> node) {
        return children.add(node);
    }

    @Override
    public boolean removeChild(Node<T> node) {
        return children.remove(node);
    }
}