package node.utils;

import node.model.Node;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NodeTree<T> implements Iterable<Node<T>> {

    private Node<T> root;

    public NodeTree(Node<T> root) {
        this.root = root;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new NodeTreeIterator();
    }

    public Stream<Node<T>> asStream() {
        return StreamSupport.stream(this.spliterator(), false);
    }


    private class NodeTreeIterator implements Iterator<Node<T>> {

        private Iterator<Node<T>> iterator;
        private Queue<Node<T>> parents;
        private Node<T> next;

        NodeTreeIterator() {
            this.parents = new LinkedList<>();
            this.iterator = root.getChildren().iterator();
            this.next = iterator.hasNext() ? iterator.next() : null;
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