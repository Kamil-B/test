package node.service;

import node.model.Event;
import node.model.EventType;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public class FileWatcher implements Runnable {

    @Override
    public void run() {
        while (true) {
            update();
        }
    }

    public void update() {
        if (watchKeys.isEmpty()) {
            return;
        }
        WatchKey key = null;
        try {
            key = watchService.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (key == null || !watchKeys.containsKey(key)) {
            return;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
            String fileName = ((WatchEvent<Path>) event).context().getFileName().toString();
            Path path = watchKeys.get(key).resolve(fileName);

            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                register(path);
                events.add(new Event(path, EventType.CREATE));
            }
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                events.add(new Event(path, EventType.DELETE));
            }
        }
        key.reset();
    }
}
