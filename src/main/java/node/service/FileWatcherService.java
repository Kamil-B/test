package node.service;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventMessage;
import node.utils.FileWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class FileWatcherService {

    @Autowired
    private SimpMessagingTemplate template;

    private Queue<Event> events;
    private Future future;
    private FileWatcher fileWatcher;
    private Observable<Event> observable;

    public FileWatcherService() {
        this.events = new ConcurrentLinkedQueue<>();
        this.observable = Observable.create(emitter -> {
            try {
                for (Event event : events) {
                    log.info("sending event: " + event);
                    emitter.onNext(event);
                    events.remove(event);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
            if (emitter.isDisposed()) {
                emitter.onComplete();
            }
        });

        observable.subscribeWith(new DisposableObserver<Event>() {
            @Override
            public void onNext(Event event) {
                template.convertAndSend("/topic/file", new EventMessage(event.getPath().toString(), event.getEvent()));
            }

            @Override
            public void onError(Throwable throwable) {
                log.info(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("stream completed");
            }
        });

                /*.subscribe(event ->
                template.convertAndSend("/topic/file", new EventMessage(event.getPath().toString(), event.getEvent())));*/
/*                Observable.fromIterable(events).doAfterNext(event ->
                {
                    log.info("Event occurred: " + event.toString());
                    events.remove(event);
                }
        );*/
    }

    // Autoclosable
    public Observable<Event> startWatching(Path path) {
        if (!Files.exists(path)) {
            log.info("Path: " + path + " does not exist!!");
            return Observable.empty();
        }
        addToFileWatcher(path);
        return observable;
    }

    private void addToFileWatcher(Path path) {
        if (fileWatcher == null) {
            startFileWatcher(path);
        } else {
            fileWatcher.addToWatched(path);
        }
    }

    private void startFileWatcher(Path path) {
        try {
            WatchService watchService = path.toAbsolutePath().getFileSystem().newWatchService();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            fileWatcher = new FileWatcher(events, watchService, path);
            future = executorService.submit(fileWatcher);
        } catch (IOException e) {
            log.error("Couldn't initiate service with " + path + ". " + e.getMessage());
        }
    }
}