package node;

public interface Node<T> {

    T getPayload();

    Iterable<Node<T>> getChildren();

    //boolean addChildren(Node<T> node);

    //boolean removeChildren(Node<T> node);
}