package node;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@Slf4j
public class NodeHelper<T> implements Iterable<Node<T>> {

    private Node<T> root;

    public NodeHelper(Node<T> root) {
        this.root = root;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new NodeHelperIterator();
    }

    private class NodeHelperIterator implements Iterator<Node<T>> {

        private Iterator<Node<T>> iterator;
        private Queue<Node<T>> children;
        private Node<T> next;

        NodeHelperIterator() {
            this.children = new LinkedList<>();
            this.iterator = root.getChildren().iterator();
            this.next = iterator.next();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Node<T> next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            Node<T> node = next;
            if (node.getChildren().iterator().hasNext()) {
                children.add(node);
            }
            next = getNextNode();
            return node;
        }

        private Node<T> getNextNode() {
            if (!iterator.hasNext()) {
                if (children.isEmpty()) {
                    return null;
                }
                iterator = children.poll().getChildren().iterator();
            }
            return iterator.next();
        }

    }
}