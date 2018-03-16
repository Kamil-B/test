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
        while (!watchKeys.isEmpty()) {
            try {
                Observable.just(watchService.take()).filter(Objects::nonNull)
                        .map(this::update)
                        .flatMapIterable(event -> event)
                        .subscribe(event -> events.add(event));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToWatched(Path path) {
        if (Files.isDirectory(path)) {
            registerToWatcher(path);
            for (Path subDir : NodeUtils.getAllSubDirectories(path)) {
                addToWatched(subDir);
            }
        } else {
            log.info("New file found: " + path);
        }
    }

    private List<Event> update(WatchKey key) {
        List<Event> events = new LinkedList<>();
        for (WatchEvent<?> event : key.pollEvents()) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                log.info("Event occurred: " + EventType.CREATE + " " + path);
                events.add(new Event(path, EventType.CREATE));
                addToWatched(path);
            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                log.info("Event occurred: " + EventType.DELETE + " " + path);
                events.add(new Event(path, EventType.DELETE));
/*                events.addAll(removeBranch(path)
                        .map(element -> new Event(element, EventType.DELETE))
                        .collect(Collectors.toList()));*/
            }
        }
        if (!key.reset()) {
            watchKeys.remove(key);
        }
        return events;
    }

/*    private Stream<Path> removeBranch(Path path) {
        Node<Path> parent = null;
        Node<Path> actual = null;
        for (Node<Path> node : tree) {
            if (node.getPayload().equals(path)) {
                log.info("parent found: " + node.toString());
                 parent = node;
                 actuals = node.getChildren();

            }else if(node.getPayload().equals(path)){
                actual = node;
            }
        }
        log.info(parent + " " + actual);
        if (parent != null && actual != null) {
            parent.removeChild(actual);
            return actual.getChildren().stream().map(Node::getPayload).collect(Collectors.toList()).stream();
        }

        return Stream.empty();
        }*/


    private void registerToWatcher(Path dir) {
        if (watchKeys.values().contains(dir)) {
            return;
        }
        try {
            WatchKey key = dir.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
            watchKeys.put(key, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("New folder found: " + dir + ". Added to watcher registry");

    }

}