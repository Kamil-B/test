package node;

public class Tree<T> {

    private Node<T> root;

    public Tree(T rootData){
        this.root = new Node<T>();
        this.root.setData(rootData);
    }
}