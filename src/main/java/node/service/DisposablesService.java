package node.service;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;
import node.socket.StompDisconnectEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DisposablesService {
    private Map<String, Disposable> disposables = new HashMap<>();
    private PublishSubject<String> publisher = PublishSubject.create();

    @Bean
    public StompDisconnectEventListener stompDisconnectEventListener() {
        publisher.subscribe(sessionId ->
        {
            Disposable disposable = disposables.remove(sessionId);
            if (disposable != null) disposable.dispose();
            log.info("Session: " + sessionId + " disconnected. Disposed");
        });
        return new StompDisconnectEventListener(publisher);
    }

    public void put(String sessionId, Disposable subscriber) {
        disposables.put(sessionId, subscriber);
    }
}