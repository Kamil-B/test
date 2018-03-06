package node;

import lombok.Data;

import java.util.List;

@Data
public class Node<T> {

    private T data;
    private Node<T> parent;
    private List<Node<T>> childrens;

    public void addChildren(Node<T> node) {
        childrens.add(node);
    }

}
