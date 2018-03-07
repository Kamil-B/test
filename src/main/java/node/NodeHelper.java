package node;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
        private Node<T> next;

        NodeHelperIterator() {
            this.next = root;
            this.iterator = root.getChildren().iterator();
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
            if (!iterator.hasNext()) {
                Node<T> actual = next;
                iterator = actual.getChildren().iterator();
                next = getNextNode(actual);
                return actual;
            }
            return iterator.next();
        }

        private Node<T> getNextNode(Node<T> node) {
            Iterator<Node<T>> iterator = node.getChildren().iterator();
            if (iterator.hasNext())
                return iterator.next();
            return null;
        }
    }
}