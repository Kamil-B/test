package node.socket;


import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
public class StompDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private PublishSubject<String> publishSubject;

    public StompDisconnectEventListener(PublishSubject<String> publishSubject) {
        this.publishSubject = publishSubject;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        publishSubject.onNext(event.getSessionId());
    }
}
