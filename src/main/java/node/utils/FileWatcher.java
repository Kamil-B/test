package node.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventType;
import node.model.Node;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileWatcher implements Runnable {

    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys;
    private NodeTree<Path> tree;
    private PublishSubject<Event> publisher;

    public FileWatcher(WatchService watchService, Path path, PublishSubject<Event> publisher) {
        this.tree = new NodeTree<>(NodePathUtils.createNodeTree(path));
        this.watchService = watchService;
        this.watchKeys = new HashMap<>();
        this.publisher = publisher;
        addToWatched(path);
    }

    @Override
    public void run() {
        while (!watchKeys.isEmpty()) {
            try {
/*
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = new LinkedList<>();
                new LinkedList<>(key.pollEvents()).descendingIterator().forEachRemaining(events::add);
                List<WatchEventKeyTuple> ev = events.stream().map(event -> new WatchEventKeyTuple(key, ((WatchEvent<Path>) event)))
                        .collect(Collectors.toList());
                Observable.fromIterable(ev).subscribe(event -> update(event).forEach(
                        ev1 -> publisher.onNext(ev1)));
*/
                Observable.just(watchService.take())
                        .buffer(100, TimeUnit.MILLISECONDS)
                        .flatMap(Observable::fromIterable)
                        .flatMap(key -> Observable.fromIterable(key.pollEvents())
                                .map(value ->
                                {
                                    log.info("value: " + value.toString());
                                    return new WatchEventKeyTuple(key, ((WatchEvent<Path>) value));
                                }))
                        .subscribe(event -> update(event).forEach(ev -> publisher.onNext(ev)));

            } catch (InterruptedException e) {
                log.warn("Exception while processing events: ", e);
            }
        }
    }

    public void addToWatched(Path path) {
        if (!Files.isDirectory(path)) {
            return;
        }
        registerToWatcher(path);
        for (Path subDir : PathUtils.getSubdirectories(path)) {
            addToWatched(subDir);
        }
    }

    private Stream<Event> update(WatchEventKeyTuple event) {
        List<Event> events = new ArrayList<>();
        Path path = watchKeys.get(event.getKey()).resolve(event.getEvent().context().getFileName());
        log.info("path: " + path);

        if (event.getEvent().kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            addToWatched(path);
            addBranchToTree(path);
            log.info(event.toString());
            events.add(new Event(path, EventType.CREATE));
        }
        if (event.getEvent().kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            events.add(new Event(path, EventType.DELETE));
            events.addAll(removeBranchFromTree(path)
                    .map(element -> new Event(element, EventType.DELETE))
                    .collect(Collectors.toList()));
        }
        if (!event.getKey().reset()) {
            watchKeys.remove(event.getKey());
        }
        return events.stream();
    }

    private void addBranchToTree(Path path) {
        Optional<Node<Path>> parent = tree.asStream().filter(node -> node.getPayload().equals(path.getParent())).findFirst();
        if (parent.isPresent()) {
            parent.get().addChild(NodePathUtils.createNodeTree(path));
        } else if (tree.getRoot().getPayload().equals(path.getParent())) {
            tree.getRoot().addChild(NodePathUtils.createNodeTree(path));
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
            WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);
            watchKeys.put(key, dir);
        } catch (IOException e) {
            log.warn("Couldn't register directory: " + dir + " to file watcher");
        }
    }

    @Data
    @ToString
    @AllArgsConstructor
    private class WatchEventKeyTuple {
        private WatchKey key;
        private WatchEvent<Path> event;
    }
}