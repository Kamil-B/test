package node;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node<T> {

    private T data;
    private Node<T> parent;
    private List<Node<T>> childrens;

    public Node() {
        this.childrens = new ArrayList<>();
    }

    public void addChildren(Node<T> node) {
        childrens.add(node);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
        return !childrens.isEmpty();
    }
}
