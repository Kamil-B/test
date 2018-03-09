package node.utils;

import node.Node;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NodeHelper<T> implements Iterable<Node<T>> {

    private Node<T> root;

    public NodeHelper(Node<T> root) {
        this.root = root;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new NodeHelperIterator();
    }

    public Stream<Node<T>> asStream() {
        return StreamSupport.stream(this.spliterator(), false);

    }

    private class NodeHelperIterator implements Iterator<Node<T>> {

        private Iterator<Node<T>> iterator;
        private Queue<Node<T>> parents;
        private Node<T> next;

        NodeHelperIterator() {
            this.parents = new LinkedList<>();
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
            if (hasChildren(node)) {
                parents.add(node);
            }
            next = getNextNode();
            return node;
        }


        private boolean hasChildren(Node<T> node) {
            return node.getChildren().iterator().hasNext();
        }

        private Node<T> getNextNode() {
            if (!iterator.hasNext()) {
                if (parents.isEmpty()) {
                    return null;
                }
                iterator = parents.poll().getChildren().iterator();
            }
            return iterator.next();
        }
    }
}