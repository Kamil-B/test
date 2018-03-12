package node;

import java.util.List;

public interface Node<T> {

    T getPayload();

    List<Node<T>> getChildren();

    boolean addChild(Node<T> node);

    boolean removeChild(Node<T> node);
}