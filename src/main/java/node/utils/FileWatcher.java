package node.utils;

import com.sun.nio.file.SensitivityWatchEventModifier;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.model.Node;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileWatcher implements Runnable {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;
    private Queue<Event> events;
    private NodeTree<Path> tree;

    public FileWatcher(Queue<Event> events, WatchService watchService, Path path) {
        this.tree = new NodeTree<>(NodeUtils.createPathTree(path));
        this.events = events;
        this.watchService = watchService;
        this.watchKeys = new HashMap<>();
        addToWatched(path);
    }

    @Override
    public void run() {
        //while (!watchKeys.isEmpty()) {
            try {
                WatchKey id = Observable.fromArray(watchService.take()).blockingFirst();
                events.addAll(update(id));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       // }
    }

    public void addToWatched(Path path) {
        registerToWatcher(path);
        if (Files.isDirectory(path)) {
            for (Path subDir : NodeUtils.getAllSubDirectories(path)) {
                addToWatched(subDir);
            }
        }
    }

    private List<Event> update(WatchKey key) {
        List<Event> events = new LinkedList<>();
        Queue<WatchEvent<?>> events1 = new LinkedList<>(key.pollEvents());

        for (WatchEvent<?> event : events1) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                events.add(new Event(path, EventType.CREATE));
                if (Files.isDirectory(path)) {
                    addToWatched(path);
                }
                addToParentDirectory(path);
            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                events.add(new Event(path, EventType.DELETE));
                events.addAll(removeBranchFromTree(path)
                        .map(element -> new Event(element, EventType.DELETE))
                        .collect(Collectors.toList()));
            }
        }

        if (!key.reset()) {
            watchKeys.remove(key);
        }
        return events;
    }

    private void addToParentDirectory(Path path) {
        Optional<Node<Path>> parent = tree.asStream().filter(node -> node.getPayload().equals(path.getParent())).findFirst();
        if (parent.isPresent()) {
            parent.get().addChild(NodeUtils.createPathTree(path));
        } else if (tree.getRoot().getPayload().equals(path.getParent())) {
            tree.getRoot().addChild(NodeUtils.createPathTree(path));
        }
    }

    private Stream<Path> removeBranchFromTree(Path path) {
        Optional<Node<Path>> toRemove = tree.asStream().filter(element -> element.getPayload().equals(path)).findFirst();
        Optional<Node<Path>> parent = tree.asStream().filter(element -> element.getPayload().equals(path.getParent())).findFirst();
        if (!toRemove.isPresent()) {
            return Stream.empty();
        }
        if (parent.isPresent()) {
            parent.get().removeChild(toRemove.get());
        } else if (tree.getRoot().getChildren().contains(toRemove.get())) {
            tree.getRoot().removeChild(toRemove.get());
        }
        return new NodeTree<>(toRemove.get()).asStream()
                .map(Node::getPayload).collect(Collectors.toList()).stream();
    }

    private void registerToWatcher(Path dir) {
        try {
            WatchKey key = dir.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
            watchKeys.put(key, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}