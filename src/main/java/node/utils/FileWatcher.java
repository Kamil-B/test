package node.utils;

import com.sun.nio.file.SensitivityWatchEventModifier;
import io.reactivex.subjects.PublishSubject;
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
    private NodeTree<Path> tree;
    private PublishSubject<Event> publisher;

    public FileWatcher(WatchService watchService, Path path, PublishSubject<Event> publisher) {
        this.tree = new NodeTree<>(NodeUtils.createNodeTree(path));
        this.watchService = watchService;
        this.watchKeys = new HashMap<>();
        this.publisher = publisher;
        addToWatched(path);
    }

    @Override
    public void run() {
        while (!watchKeys.isEmpty()) {
            try {
                update(watchService.take()).forEach(publisher::onNext);
            } catch (Exception e) {
                log.error("Exception while processing events: ", e);
            }
        }
    }

    public void addToWatched(Path path) {
        if (!Files.isDirectory(path)) {
            return;
        }
        registerToWatcher(path);
        for (Path subDir : NodeUtils.getChildren(path)) {
            addToWatched(subDir);
        }

    }

    private List<Event> update(WatchKey key) {
        Queue<WatchEvent<?>> watchEvents = new LinkedList<>(key.pollEvents());
        List<Event> events = new ArrayList<>();
        while (!watchEvents.isEmpty()) {
            WatchEvent<?> event = watchEvents.poll();
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            if (fileName.contains(".tmp")) {
                log.debug("Found temporary file: " + fileName + ". Ignoring.");
                continue;
            }
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                if (!Files.exists(path) || !Files.exists(path.getParent())) {
                    watchEvents.add(event);
                } else {
                    addToWatched(path);
                    addToParentDirectory(path);
                    events.add(new Event(path, EventType.CREATE));
                }
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
            parent.get().addChild(NodeUtils.createNodeTree(path));
        } else if (tree.getRoot().getPayload().equals(path.getParent())) {
            tree.getRoot().addChild(NodeUtils.createNodeTree(path));
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